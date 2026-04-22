package controller;

import model.GameModel;
import model.HighscoreManager;
import view.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameController {
    private final GameModel model;
    private final GamePanel view;
    private Timer timer;
    private final JFrame parentFrame;
    private final int baseDelay = 500;

    public GameController(GameModel model, GamePanel view, JFrame parentFrame) {
        this.model = model;
        this.view  = view;
        this.parentFrame = parentFrame;
        startTimer();
        setupKeyBindings();
    }

    private void startTimer() {
        timer = new Timer(baseDelay, e -> update());
        timer.start();
    }

    private void setupKeyBindings() {
        InputMap  im = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = view.getActionMap();

        // TAB по умолчанию переключает фокус — отключаем
        view.setFocusTraversalKeysEnabled(false);

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.isSplitFallMode())              model.moveSplitPartLeft();
                else if (!model.isSplitSelectionMode())   model.moveLeft();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.isSplitFallMode())              model.moveSplitPartRight();
                else if (!model.isSplitSelectionMode())   model.moveRight();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("UP"), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if      (model.isSplitSelectionMode()) model.moveSplitLineUp();
                else if (!model.isSplitFallMode())     model.rotate();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        am.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if      (model.isSplitSelectionMode()) model.moveSplitLineDown();
                else if (!model.isSplitFallMode())     model.moveDown();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("SPACE"), "drop");
        am.put("drop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.isSplitFallMode()) model.dropActiveSplitPart();
                else                         model.drop();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("S"), "splitMode");
        am.put("splitMode", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.enterSplitMode();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("ENTER"), "confirmSplit");
        am.put("confirmSplit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.confirmSplit();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelSplit");
        am.put("cancelSplit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.cancelSplit();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("TAB"), "switchPart");
        am.put("switchPart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.switchControlledPart();
                view.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke("P"), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    private void togglePause() {
        model.togglePause();
        if (model.isPaused()) {
            timer.stop();
            JOptionPane.showMessageDialog(view, "Игра на паузе", "Пауза", JOptionPane.INFORMATION_MESSAGE);
            timer.start();
            model.togglePause();
        }
        view.repaint();
    }

    private void update() {
        int newDelay = Math.max(100, baseDelay - (model.getLevel() - 1) * 50);
        if (timer.getDelay() != newDelay) timer.setDelay(newDelay);

        if (!model.isGameOver() && !model.isPaused()) {
            if (model.isSplitFallMode()) {
                // обе части падают независимо
                model.updateSplitFall();
            } else {
                // обычное падение (внутри заблокировано если splitSelectionMode)
                model.moveDown();
            }
            view.repaint();
        } else if (model.isGameOver()) {
            timer.stop();
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        int score = model.getScore();
        HighscoreManager.saveScore(model.getPlayer().getName(), score);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        JLabel messageLabel = new JLabel(
                "Игра окончена! " + model.getPlayer().getName() + " набрал(а) очков: " + score);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton newGameButton = new JButton("Новая игра");
        newGameButton.addActionListener(ev -> {
            SwingUtilities.getWindowAncestor(buttonPanel).dispose();
            startNewGame();
        });
        JButton exitButton = new JButton("Выход");
        exitButton.addActionListener(ev -> System.exit(0));
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        JOptionPane pane = new JOptionPane(
                panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = pane.createDialog(parentFrame, "Игра окончена");
        dialog.setVisible(true);
    }

    private void startNewGame() {
        GameModel newModel = new GameModel(20, 10, model.getPlayer());
        GamePanel newView  = new GamePanel(newModel);
        parentFrame.getContentPane().removeAll();
        parentFrame.add(newView);
        new GameController(newModel, newView, parentFrame);
        parentFrame.revalidate();
        parentFrame.repaint();
        parentFrame.pack();
        newView.requestFocusInWindow();
    }
}
