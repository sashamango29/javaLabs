package View;

import Factory.Dealer;
import Factory.Supplier;
import Factory.Worker;
import javax.swing.*;
import java.awt.*;

public class FactoryView {
    private final JFrame frame;
    private final JLabel bodyStockLabel;
    private final JLabel motorStockLabel;
    private final JLabel accessoryStockLabel;
    private final JLabel carStockLabel;
    private final JLabel bodyProducedLabel;
    private final JLabel motorProducedLabel;
    private final JLabel accessoryProducedLabel;
    private final JLabel carsProducedLabel;
    private final JLabel carsSoldLabel;
    private final JLabel tasksInQueueLabel;
    private final JSlider bodySpeedSlider;
    private final JSlider motorSpeedSlider;
    private final JSlider accessorySpeedSlider;
    private final JSlider dealerSpeedSlider;
    private final JSlider workerSpeedSlider;

    private static final int MIN_SPEED = 100;
    private static final int MAX_DELAY = 5100;

    public FactoryView(Supplier bodySupplier, Supplier motorSupplier, Supplier[] accessorySuppliers,
                       Dealer[] dealers, Worker[] workers) {
        frame = new JFrame("Фабрика машин");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(700, 600));
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(new Color(240, 240, 240));

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Статистика"));
        statsPanel.setBackground(new Color(245, 245, 245));

        JPanel stockPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        stockPanel.setBorder(BorderFactory.createTitledBorder("Склады"));
        stockPanel.setBackground(new Color(255, 255, 255));
        bodyStockLabel = createStyledLabel("Кузова: 0");
        motorStockLabel = createStyledLabel("Двигатели: 0");
        accessoryStockLabel = createStyledLabel("Аксессуары: 0");
        carStockLabel = createStyledLabel("Машины: 0");
        stockPanel.add(bodyStockLabel);
        stockPanel.add(motorStockLabel);
        stockPanel.add(accessoryStockLabel);
        stockPanel.add(carStockLabel);

        JPanel productionPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        productionPanel.setBorder(BorderFactory.createTitledBorder("Производство"));
        productionPanel.setBackground(new Color(255, 255, 255));
        bodyProducedLabel = createStyledLabel("Произведено кузовов: 0");
        motorProducedLabel = createStyledLabel("Произведено двигателей: 0");
        accessoryProducedLabel = createStyledLabel("Произведено аксессуаров: 0");
        carsProducedLabel = createStyledLabel("Произведено машин: 0");
        carsSoldLabel = createStyledLabel("Продано машин: 0");
        productionPanel.add(bodyProducedLabel);
        productionPanel.add(motorProducedLabel);
        productionPanel.add(accessoryProducedLabel);
        productionPanel.add(carsProducedLabel);
        productionPanel.add(carsSoldLabel);

        JPanel tasksPanel = new JPanel(new FlowLayout());
        tasksPanel.setBorder(BorderFactory.createTitledBorder("Очередь задач"));
        tasksPanel.setBackground(new Color(255, 255, 255));
        tasksInQueueLabel = createStyledLabel("Задач в очереди: 0");
        tasksPanel.add(tasksInQueueLabel);

        statsPanel.add(stockPanel);
        statsPanel.add(productionPanel);
        statsPanel.add(tasksPanel);

        JPanel controlPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление скоростью"));
        controlPanel.setBackground(new Color(245, 245, 245));

        bodySpeedSlider = createStyledSlider();
        motorSpeedSlider = createStyledSlider();
        accessorySpeedSlider = createStyledSlider();
        dealerSpeedSlider = createStyledSlider();
        workerSpeedSlider = createStyledSlider();

        controlPanel.add(createStyledLabel("Поставщик кузовов:"));
        controlPanel.add(bodySpeedSlider);
        controlPanel.add(createStyledLabel("Поставщик двигателей:"));
        controlPanel.add(motorSpeedSlider);
        controlPanel.add(createStyledLabel("Поставщики аксессуаров:"));
        controlPanel.add(accessorySpeedSlider);
        controlPanel.add(createStyledLabel("Дилеры:"));
        controlPanel.add(dealerSpeedSlider);
        controlPanel.add(createStyledLabel("Сборщики:"));
        controlPanel.add(workerSpeedSlider);

        bodySpeedSlider.addChangeListener(e -> bodySupplier.setDelay(speedToDelay(bodySpeedSlider.getValue())));
        motorSpeedSlider.addChangeListener(e -> motorSupplier.setDelay(speedToDelay(motorSpeedSlider.getValue())));
        accessorySpeedSlider.addChangeListener(e -> {
            for (Supplier supplier : accessorySuppliers) {
                supplier.setDelay(speedToDelay(accessorySpeedSlider.getValue()));
            }
        });
        dealerSpeedSlider.addChangeListener(e -> {
            for (Dealer dealer : dealers) {
                dealer.setDelay(speedToDelay(dealerSpeedSlider.getValue()));
            }
        });
        workerSpeedSlider.addChangeListener(e -> {
            for (Worker worker : workers) {
                worker.setDelay(speedToDelay(workerSpeedSlider.getValue()));
            }
        });

        frame.add(statsPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));
        return label;
    }

    private JSlider createStyledSlider() {
        JSlider slider = new JSlider(100, 5100, 1100);
        slider.setMajorTickSpacing(1000);
        slider.setMinorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFont(new Font("Arial", Font.PLAIN, 12));
        return slider;
    }

    private int speedToDelay(int speed) {
        if (speed <= MIN_SPEED) return MAX_DELAY;
        return MAX_DELAY * MIN_SPEED / speed;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JLabel getBodyStockLabel() {
        return bodyStockLabel;
    }

    public JLabel getMotorStockLabel() {
        return motorStockLabel;
    }

    public JLabel getAccessoryStockLabel() {
        return accessoryStockLabel;
    }

    public JLabel getCarStockLabel() {
        return carStockLabel;
    }

    public JLabel getBodyProducedLabel() {
        return bodyProducedLabel;
    }

    public JLabel getMotorProducedLabel() {
        return motorProducedLabel;
    }

    public JLabel getAccessoryProducedLabel() {
        return accessoryProducedLabel;
    }

    public JLabel getCarsProducedLabel() {
        return carsProducedLabel;
    }

    public JLabel getCarsSoldLabel() {
        return carsSoldLabel;
    }

    public JLabel getTasksInQueueLabel() {
        return tasksInQueueLabel;
    }
}
