package ChatServer;

import Message.*;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickname;
    private long lastActivity;
    private volatile boolean isDisconnected = false;
    private final ClientManager clientManager;
    private final MessageBroadcaster broadcaster;
    private final Logger logger;
    private boolean isSuccessfullyAdded = false;

    public ClientHandler(Socket socket, ClientManager clientManager, MessageBroadcaster broadcaster, Logger logger) {
        this.socket = socket;
        this.clientManager = clientManager;
        this.broadcaster = broadcaster;
        this.logger = logger;
        updateActivity();
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void updateActivity() {
        lastActivity = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            Object received = in.readObject();
            if (!(received instanceof Message)) {
                logger.log("Invalid initial message from client");
                sendMessage(new Message("ERROR", "Invalid protocol", "Server"));
                closeResources();
                return;
            }

            Message joinMessage = (Message) received;
            if (!joinMessage.getType().equals("JOIN")) {
                logger.log("Invalid join message from client");
                sendMessage(new Message("ERROR", "Invalid join message", "Server"));
                closeResources();
                return;
            }

            nickname = joinMessage.getSender();
            if (nickname == null || nickname.trim().isEmpty()) {
                logger.log("Empty nickname received");
                sendMessage(new Message("ERROR", "Nickname cannot be empty", "Server"));
                closeResources();
                return;
            }

            synchronized (clientManager) {
                if (!clientManager.addClient(nickname, this)) {
                    sendMessage(new Message("ERROR", "Nickname " + nickname + " already taken", "Server"));
                    closeResources();
                    return;
                }
                isSuccessfullyAdded = true;
            }

            logger.log(nickname + " joined the chat");
            broadcaster.sendHistoryToClient(this);
            broadcaster.broadcast(new Message("JOIN", nickname + " joined the chat", "Server"));
            clientManager.sendUserListToAll();

            while (!socket.isClosed() && !isDisconnected && !Thread.interrupted()) {
                Object obj = in.readObject();
                if (!(obj instanceof Message)) {
                    logger.log("Invalid message format from " + nickname);
                    continue;
                }

                Message message = (Message) obj;
                updateActivity();

                if (message.getType().equals("MESSAGE")) {
                    String content = message.getContent().trim();
                    if (content.startsWith("@")) {
                        int spaceIndex = content.indexOf(" ");
                        if (spaceIndex > 0) {
                            String targetNickname = content.substring(1, spaceIndex).trim();
                            String privateContent = content.substring(spaceIndex + 1).trim();
                            if (!targetNickname.isEmpty() && !privateContent.isEmpty()) {
                                broadcaster.sendPrivateMessage(nickname, targetNickname, privateContent);
                                continue;
                            }
                        }
                    }
                    broadcaster.broadcast(new Message("MESSAGE", content, nickname));
                } else if (message.getType().equals("USER_LIST")) {
                    clientManager.sendUserListToAll();
                } else if (message.getType().equals("LEAVE")) {
                    disconnect();
                }
            }
        } catch (SocketTimeoutException e) {
            logger.log("Timeout reading from client " + (nickname != null ? nickname : "unknown"));
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Error handling client " + (nickname != null ? nickname : "unknown") + ": " + e.getMessage());
        } finally {
            closeResources();
            if (nickname != null && isSuccessfullyAdded) {
                clientManager.removeClient(nickname, this, true);
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            if (!socket.isClosed() && !isDisconnected) {
                synchronized (out) {
                    out.writeObject(message);
                    out.flush();
                }
            }
        } catch (IOException e) {
            logger.log("Error sending message to " + (nickname != null ? nickname : "unknown") + ": " + e.getMessage());
        }
    }

    public void disconnect() {
        if (isDisconnected) {
            return;
        }
        isDisconnected = true;
        closeResources();
    }

    private void closeResources() {
        try {
            synchronized (this) {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            logger.log("Error closing resources for " + (nickname != null ? nickname : "unknown") + ": " + e.getMessage());
        }
    }
}