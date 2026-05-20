package Message;

import ChatClient.ChatConnection;
import ChatUI.ChatUI;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessageHandler {
    private final ChatUI ui;
    private final ChatConnection connection;
    private final Set<String> displayedMessages = Collections.synchronizedSet(new HashSet<>());

    public MessageHandler(ChatUI ui, ChatConnection connection) {
        this.ui = ui;
        this.connection = connection;
    }

    public void handleMessage(Message message) {
        SwingUtilities.invokeLater(() -> {

            if (message.getType().equals("ERROR")) {
                ui.appendToChat("Error: " + message.getContent(), Color.RED, false);

            } else if (message.getType().equals("USER_LIST")) {
                ui.updateUserList(message.getContent().split(","));

            } else if (message.getType().equals("PRIVATE")) {
                String key = message.getTimestamp().toString() + message.getSender() + message.getContent();
                if (!displayedMessages.contains(key)) {
                    ui.appendToChat(message.toString(), Color.ORANGE, false);
                    displayedMessages.add(key);
                }

            } else {
                String key = message.getTimestamp().toString() + message.getSender() + message.getContent();
                if (!displayedMessages.contains(key)) {

                    boolean isServer = message.getSender().equals("Server");
                    boolean isOwn = !isServer
                            && connection.getNickname() != null
                            && message.getSender().equals(connection.getNickname());

                    Color color = isServer ? Color.GRAY : Color.WHITE;
                    ui.appendToChat(message.toString(), color, isOwn);
                    displayedMessages.add(key);
                }
            }
        });
    }

    public void clearDisplayedMessages() {
        displayedMessages.clear();
    }
}