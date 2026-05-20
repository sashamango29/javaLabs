package ChatServer;

import Message.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private MessageBroadcaster broadcaster;
    private final Logger logger;

    public ClientManager(MessageBroadcaster broadcaster, Logger logger) {
        this.broadcaster = broadcaster;
        this.logger = logger;
    }

    public void setBroadcaster(MessageBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    public synchronized boolean addClient(String nickname, ClientHandler handler) {
        if (clients.containsKey(nickname)) {
            logger.log("Nickname " + nickname + " already taken");
            return false;
        }
        clients.put(nickname, handler);
        logger.log("Nickname " + nickname + " successfully added to clients");
        return true;
    }

    public synchronized void removeClient(String nickname, ClientHandler handler, boolean broadcast) {
        ClientHandler existingHandler = clients.get(nickname);
        if (existingHandler == handler && clients.remove(nickname) != null) {
            if (broadcast) {
                broadcaster.broadcast(new Message("LEAVE", nickname + " left the chat", "Server"));
            }
            sendUserListToAll();
            logger.log(nickname + " disconnected");
        } else if (existingHandler != handler) {
            logger.log("Attempt to remove " + nickname + " failed: handler mismatch");
        }
    }

    public void sendUserListToAll() {
        String userList = String.join(",", clients.keySet());
        Message userListMessage = new Message("USER_LIST", userList, "Server");
        clients.values().forEach(client -> client.sendMessage(userListMessage));
    }

    public Map<String, ClientHandler> getClients() {
        return clients;
    }

    public ClientHandler getClient(String nickname) {
        return clients.get(nickname);
    }
}