package Message;

import ChatClient.ChatConnection;
import ChatUI.ChatUI;

import java.awt.*;
import java.io.*;

public class MessageSender {
    private final ChatUI ui;
    private final ChatConnection connection;
    private final ObjectOutputStream out;

    public MessageSender(ChatUI ui, ChatConnection connection, ObjectOutputStream out) {
        this.ui = ui;
        this.connection = connection;
        this.out = out;
    }

    public void sendMessage(String content) {
        if (!content.isEmpty() && connection.isConnected()) {
            try {
                Message message = new Message("MESSAGE", content, connection.getNickname());
                synchronized (out) {
                    out.writeObject(message);
                    out.flush();
                }
                ui.getMessageField().setText("");
            } catch (IOException e) {
                ui.appendToChat("Error sending message: " + e.getMessage() + "\n", Color.RED);
                connection.setConnected(false);
                ui.setConnectionEnabled(true);
            }
        }
    }
}