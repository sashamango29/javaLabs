package Message;

import ChatServer.ClientHandler;
import ChatServer.ClientManager;
import ChatServer.Logger;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class MessageBroadcaster {
    private final List<Message> messageHistory = Collections.synchronizedList(new ArrayList<>());
    private final ClientManager clientManager;
    private final Logger logger;

    public MessageBroadcaster(ClientManager clientManager, Logger logger) {
        this.clientManager = clientManager;
        this.logger = logger;
    }

    public void broadcast(Message message) {
        synchronized (messageHistory) {
            messageHistory.add(message);
            while (messageHistory.size() > 100) {
                messageHistory.removeFirst();
            }
        }
        clientManager.getClients().values().forEach(client -> client.sendMessage(message));
        logger.log(message.toString());
    }

    public void sendPrivateMessage(String sender, String targetNickname, String content) {
        ClientHandler target = clientManager.getClient(targetNickname);
        if (target != null) {
            target.sendMessage(new Message("PRIVATE", content, sender + " (private to " + targetNickname + ")"));
            logger.log("Private message from " + sender + " to " + targetNickname + ": " + content);
        } else {
            ClientHandler senderHandler = clientManager.getClient(sender);
            if (senderHandler != null) {
                senderHandler.sendMessage(new Message("ERROR", "User " + targetNickname + " not found", "Server"));
                logger.log("Failed private message from " + sender + " to " + targetNickname);
            }
        }
    }

    public void sendHistoryToClient(ClientHandler client) {
        synchronized (messageHistory) {
            messageHistory.forEach(client::sendMessage);
        }
    }
}