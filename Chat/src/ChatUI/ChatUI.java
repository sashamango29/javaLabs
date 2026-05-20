package ChatUI;

import ChatClient.ChatConnection;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class ChatUI {
    private JFrame frame;
    private JTextPane chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField nicknameField;
    private JTextField hostField;
    private JTextField portField;
    private ChatConnection connection;

    private static final Color BG_DARK      = new Color(38, 14, 30);
    private static final Color BG_PANEL     = new Color(58, 22, 46);
    private static final Color BG_FIELD     = new Color(72, 28, 58);
    private static final Color BG_CHAT      = new Color(48, 18, 38);
    private static final Color PINK_HOT     = new Color(255, 80, 155);
    private static final Color PINK_SOFT    = new Color(255, 160, 210);
    private static final Color PINK_BORDER  = new Color(180, 60, 130);
    private static final Color PINK_SEND    = new Color(210, 50, 120);
    private static final Color PINK_CONNECT = new Color(160, 40, 100);
    private static final Color WHITE        = new Color(255, 240, 248);
    private static final Color OWN_MSG_COLOR = new Color(255, 130, 200);

    public ChatUI() {
        initUI();
    }

    private void initUI() {
        frame = new JFrame("✿ Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(920, 560);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_DARK);

        JPanel connectPanel = new JPanel(new GridBagLayout());
        connectPanel.setBackground(BG_PANEL);
        connectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PINK_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.gridx = 0;
        connectPanel.add(makeLabel("Nickname:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.5;
        nicknameField = makeTextField(16);
        nicknameField.setDocument(new JTextFieldLimit(16));
        connectPanel.add(nicknameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        connectPanel.add(makeLabel("Host:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 2.0;
        hostField = makeTextField(15);
        connectPanel.add(hostField, gbc);

        gbc.gridx = 4; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        connectPanel.add(makeLabel("Port:"), gbc);

        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.8;
        portField = makeTextField(6);
        connectPanel.add(portField, gbc);

        gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        connectPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 4;
        connectButton = makePinkButton("🔗 Connect", PINK_CONNECT);
        connectPanel.add(connectButton, gbc);

        gbc.gridx = 5;
        disconnectButton = makePinkButton("🔕 Disconnect", new Color(100, 30, 70));
        disconnectButton.setEnabled(false);
        connectPanel.add(disconnectButton, gbc);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chatArea.setBackground(BG_CHAT);
        chatArea.setForeground(WHITE);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createLineBorder(PINK_BORDER, 2));
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(chatScroll);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userList.setBackground(BG_PANEL);
        userList.setForeground(PINK_SOFT);
        userList.setFixedCellHeight(26);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(BorderFactory.createLineBorder(PINK_BORDER, 2));
        userScroll.setPreferredSize(new Dimension(150, 0));
        styleScrollBar(userScroll);

        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? PINK_BORDER : BG_PANEL);
                c.setForeground(PINK_SOFT);
                if (value != null && connection != null
                        && value.equals(connection.getNickname())) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    c.setForeground(PINK_HOT);
                }
                c.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                return c;
            }
        });

        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selected = userList.getSelectedValue();
                    if (selected != null && !selected.equals(connection.getNickname())) {
                        messageField.setText("@" + selected + " ");
                        messageField.requestFocus();
                    }
                }
            }
        });

        JPanel messagePanel = new JPanel(new BorderLayout(8, 0));
        messagePanel.setBackground(BG_DARK);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, PINK_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 10, 12)
        ));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageField.setBackground(BG_FIELD);
        messageField.setForeground(WHITE);
        messageField.setCaretColor(PINK_HOT);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PINK_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JButton sendButton = makePinkButton("📨 Send", PINK_SEND);
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        frame.add(connectPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, userScroll);
        splitPane.setDividerLocation(740);
        splitPane.setDividerSize(6);
        splitPane.setBackground(BG_DARK);
        splitPane.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 8));
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(messagePanel, BorderLayout.SOUTH);

        connection = new ChatConnection(this);

        connectButton.addActionListener(e -> {
            connection.connectToServer(
                hostField.getText().trim(),
                portField.getText().trim(),
                nicknameField.getText().trim()
            );
            animateFlash(connectButton, new Color(100, 255, 160), PINK_CONNECT);
        });

        disconnectButton.addActionListener(e -> connection.disconnect());
        sendButton.addActionListener(e -> connection.sendMessage(messageField.getText().trim()));
        messageField.addActionListener(e -> connection.sendMessage(messageField.getText().trim()));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { connection.disconnect(); }
        });

        frame.setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(PINK_SOFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JTextField makeTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(BG_FIELD);
        tf.setForeground(WHITE);
        tf.setCaretColor(PINK_HOT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PINK_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JButton makePinkButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : bg.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(WHITE);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PINK_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(PINK_HOT); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(WHITE); }
        });
        return btn;
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setBackground(BG_PANEL);
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = PINK_BORDER;
                trackColor = BG_PANEL;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
            }
        });
    }

    private void animateFlash(JButton btn, Color flashColor, Color originalColor) {
        btn.setBackground(flashColor);
        btn.repaint();
        javax.swing.Timer timer = new javax.swing.Timer(120, e -> {
            btn.setBackground(originalColor);
            btn.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static class JTextFieldLimit extends PlainDocument {
        private final int limit;
        JTextFieldLimit(int limit) { this.limit = limit; }
        @Override
        public void insertString(int offset, String str, AttributeSet attr)
                throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit)
                super.insertString(offset, str, attr);
        }
    }

    public void appendToChat(String message, Color color) {
        appendToChat(message, color, false);
    }

    public void appendToChat(String message, Color color, boolean isOwn) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = chatArea.getStyledDocument();

            SimpleAttributeSet charAttr = new SimpleAttributeSet();
            StyleConstants.setForeground(charAttr, isOwn ? OWN_MSG_COLOR : color);
            if (isOwn) StyleConstants.setBold(charAttr, true);

            SimpleAttributeSet paraAttr = new SimpleAttributeSet();
            StyleConstants.setAlignment(paraAttr,
                    isOwn ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

            try {
                if (doc.getLength() > 0) {
                    doc.insertString(doc.getLength(), "\n", charAttr);
                }

                doc.setParagraphAttributes(doc.getLength(), 1, paraAttr, false);

                FontMetrics fm = chatArea.getFontMetrics(chatArea.getFont());
                int maxWidth = chatArea.getWidth() - 20;
                chatArea.revalidate();
                chatArea.repaint();

                String[] words = message.split(" ");
                StringBuilder line = new StringBuilder();
                int lineWidth = 0;

                for (String word : words) {
                    while (!word.isEmpty()) {
                        String subWord = word;
                        int subWordWidth = fm.stringWidth(subWord + " ");
                        int charsToFit = word.length();

                        if (subWordWidth > maxWidth) {
                            charsToFit = 0;
                            int currentWidth = 0;
                            for (int i = 0; i < word.length(); i++) {
                                currentWidth += fm.charWidth(word.charAt(i));
                                if (currentWidth > maxWidth) break;
                                charsToFit++;
                            }
                            if (charsToFit == 0) charsToFit = 1;
                            subWord = word.substring(0, charsToFit);
                            word   = word.substring(charsToFit);
                        } else {
                            word = "";
                        }

                        subWordWidth = fm.stringWidth(subWord + " ");
                        if (lineWidth + subWordWidth > maxWidth && !line.toString().isEmpty()) {
                            doc.insertString(doc.getLength(), line.toString().trim() + "\n", charAttr);
                            doc.setParagraphAttributes(doc.getLength(), 1, paraAttr, false);
                            line = new StringBuilder(subWord + " ");
                            lineWidth = subWordWidth;
                        } else {
                            line.append(subWord).append(" ");
                            lineWidth += subWordWidth;
                        }
                    }
                }

                if (!line.toString().isEmpty()) {
                    doc.insertString(doc.getLength(), line.toString().trim(), charAttr);
                }

                chatArea.setCaretPosition(doc.getLength());
                chatArea.revalidate();
                chatArea.repaint();

            } catch (BadLocationException e) {
                chatArea.setText(message);
            }
        });
    }

    public void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                if (!user.isEmpty()) userListModel.addElement(user);
            }
        });
    }

    public void clearChat()     { SwingUtilities.invokeLater(() -> chatArea.setText("")); }
    public void clearUserList() { SwingUtilities.invokeLater(() -> userListModel.clear()); }

    public void setConnectionEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            connectButton.setEnabled(enabled);
            disconnectButton.setEnabled(!enabled);
            nicknameField.setEditable(enabled);
            hostField.setEditable(enabled);
            portField.setEditable(enabled);
            if (enabled) clearUserList();
        });
    }

    public JTextField getMessageField() { return messageField; }
}
