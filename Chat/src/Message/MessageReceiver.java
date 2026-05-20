package Message;

import ChatClient.ChatConnection;
import ChatUI.ChatUI;

import java.awt.*;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MessageReceiver implements Runnable {
    private final ChatUI ui;
    private final ChatConnection connection;
    private final MessageHandler messageHandler;
    private final ObjectInputStream in;

    public MessageReceiver(ChatUI ui, ChatConnection connection, MessageHandler messageHandler, ObjectInputStream in) {
        this.ui = ui;
        this.connection = connection;
        this.messageHandler = messageHandler;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && connection.isConnected()) {
                Object obj;
                try {
                    obj = in.readObject();
                } catch (SocketTimeoutException | EOFException e) {
                    if (connection.isConnected()) {
                        ui.appendToChat("Disconnected: Server ended the connection\n", Color.RED);
                    }
                    connection.setConnected(false);
                    ui.setConnectionEnabled(true);
                    return;
                } catch (SocketException e) {
                    if (connection.isConnected()) {
                        ui.appendToChat("Connection lost\n", Color.RED);
                    }
                    connection.setConnected(false);
                    ui.setConnectionEnabled(true);
                    return;
                }

                if (!(obj instanceof Message)) continue;

                messageHandler.handleMessage((Message) obj);
            }
        } catch (Exception e) {
            if (connection.isConnected()) {
                ui.appendToChat("Connection error: " + (e.getMessage() != null ? e.getMessage() : "unexpected error") + "\n", Color.RED);
            }
            connection.setConnected(false);
            ui.setConnectionEnabled(true);
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
}