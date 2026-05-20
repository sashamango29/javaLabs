package ChatServer;

import Message.MessageBroadcaster;

import java.io.*;
import java.net.*;

public class ServerConnection {
    private final int port;
    private final String host;
    private final Logger logger;
    private final ClientManager clientManager;
    private final MessageBroadcaster broadcaster;
    private ServerSocket serverSocket;
    private volatile boolean isRunning = true;
    private static final int READ_TIMEOUT = 120000;

    public ServerConnection(int port, String host, ClientManager clientManager, MessageBroadcaster broadcaster, Logger logger) {
        this.port = port;
        this.host = host;
        this.clientManager = clientManager;
        this.broadcaster = broadcaster;
        this.logger = logger;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host));
            logger.log("Server started on " + host + ":" + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(READ_TIMEOUT);
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientManager, broadcaster, logger);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    if (isRunning) {
                        logger.log("Error accepting client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.log("Server error: " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    public void stop() {
        isRunning = false;
        closeServerSocket();
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logger.log("Server socket closed");
            }
        } catch (IOException e) {
            logger.log("Error closing server socket: " + e.getMessage());
        }
    }
}