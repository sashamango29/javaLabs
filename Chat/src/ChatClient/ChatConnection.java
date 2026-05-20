package ChatClient;

import Message.*;
import ChatUI.ChatUI;

import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatConnection {
    private final ChatUI ui;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread receiveThread;
    private volatile boolean isConnected = false;
    private String nickname;
    private static final int READ_TIMEOUT = 120000;
    private static final int MAX_NICKNAME_LENGTH = 16;
    private boolean nicknameTaken = false;
    private MessageHandler messageHandler;
    private MessageSender messageSender;

    public ChatConnection(ChatUI ui) {
        this.ui = ui;
        this.messageHandler = new MessageHandler(ui, this);
    }

    public void connectToServer(String host, String portText, String nickname) {
        if (isConnected) {
            disconnect();
        }

        this.nickname = nickname;

        if (host.isEmpty()) {
            ui.clearChat();
            ui.appendToChat("Error: Host cannot be empty\n", Color.RED);
            return;
        }
        if (nickname.isEmpty()) {
            ui.clearChat();
            ui.appendToChat("Error: Nickname cannot be empty\n", Color.RED);
            return;
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            ui.clearChat();
            ui.appendToChat("Error: Nickname cannot exceed " + MAX_NICKNAME_LENGTH + " characters\n", Color.RED);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
            if (port <= 0 || port > 65535) {
                ui.clearChat();
                ui.appendToChat("Error: Port must be between 1 and 65535\n", Color.RED);
                return;
            }
        } catch (NumberFormatException ex) {
            ui.clearChat();
            ui.appendToChat("Error: Invalid port number\n", Color.RED);
            return;
        }

        ui.clearChat();
        messageHandler.clearDisplayedMessages();

        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(READ_TIMEOUT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            isConnected = true;
            nicknameTaken = false;

            synchronized (out) {
                out.writeObject(new Message("JOIN", nickname, nickname));
                out.flush();
            }

            messageSender = new MessageSender(ui, this, out);
            receiveThread = new Thread(new MessageReceiver(ui, this, messageHandler, in));
            receiveThread.start();

            ui.setConnectionEnabled(false);

        } catch (IOException e) {
            String errorMsg;
            if (e instanceof ConnectException) {
                errorMsg = "Cannot connect to server - it may be down";
            } else if (e instanceof UnknownHostException) {
                errorMsg = "Unknown host: " + host;
            } else {
                errorMsg = "Connection failed";
            }
            ui.appendToChat(errorMsg + "\n", Color.RED);
            isConnected = false;
            ui.setConnectionEnabled(true);
        }
    }

    public void sendMessage(String content) {
        if (messageSender != null) {
            messageSender.sendMessage(content);
        }
    }

    public void disconnect() {
        if (!isConnected) return;

        isConnected = false;

        try {
            if (!nicknameTaken && out != null) {
                synchronized (out) {
                    out.writeObject(new Message("LEAVE", "", nickname));
                    out.flush();
                }
                ui.appendToChat("Disconnected successfully\n", Color.GREEN);
            }
        } catch (IOException e) {
            ui.appendToChat("Error during disconnect: " + e.getMessage() + "\n", Color.RED);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                ui.appendToChat("Error closing connection: " + e.getMessage() + "\n", Color.RED);
            } finally {
                ui.setConnectionEnabled(true);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isNicknameTaken() {
        return nicknameTaken;
    }

    public void setNicknameTaken(boolean taken) {
        this.nicknameTaken = taken;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }
}