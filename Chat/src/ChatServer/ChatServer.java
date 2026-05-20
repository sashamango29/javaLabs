package ChatServer;

import Config.Config;
import Message.MessageBroadcaster;

public class ChatServer {
    private final int port;
    private final String host;
    private final boolean loggingEnabled;
    private ServerConnection connectionManager;

    public ChatServer(int port, String host, boolean loggingEnabled) {
        this.port = port;
        this.host = host;
        this.loggingEnabled = loggingEnabled;
    }

    public void start() {
        Logger logger = new Logger(loggingEnabled);
        ClientManager clientManager = new ClientManager(null, logger);
        MessageBroadcaster broadcaster = new MessageBroadcaster(clientManager, logger);
        clientManager.setBroadcaster(broadcaster);

        connectionManager = new ServerConnection(port, host, clientManager, broadcaster, logger);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.log("Shutting down server...");
            connectionManager.stop();
        }));

        connectionManager.start();
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(Config.getServerPort(), Config.getServerHost(), Config.isLoggingEnabled());
        server.start();
    }
}
