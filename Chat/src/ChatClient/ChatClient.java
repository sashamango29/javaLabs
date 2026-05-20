package ChatClient;

import ChatUI.ChatUI;

import javax.swing.*;

public class ChatClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatUI::new);
    }
}
