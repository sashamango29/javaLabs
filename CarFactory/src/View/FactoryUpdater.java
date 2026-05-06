package View;

import Factory.Car;
import Factory.Component;
import Factory.Storage;
import ThreadPool.ThreadPool;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class FactoryUpdater implements Runnable {
    private final FactoryView ui;
    private final Storage<Component> bodyStorage;
    private final Storage<Component> motorStorage;
    private final Storage<Component> accessoryStorage;
    private final Storage<Car> carStorage;
    private final ThreadPool threadPool;
    private final AtomicInteger bodyProduced;
    private final AtomicInteger motorProduced;
    private final AtomicInteger accessoryProduced;
    private final AtomicInteger carIdCounter;
    private final AtomicInteger carsSold;

    public FactoryUpdater(FactoryView ui, Storage<Component> bodyStorage, Storage<Component> motorStorage,
                          Storage<Component> accessoryStorage, Storage<Car> carStorage,
                          ThreadPool threadPool, AtomicInteger bodyProduced, AtomicInteger motorProduced,
                          AtomicInteger accessoryProduced, AtomicInteger carIdCounter, AtomicInteger carsSold) {
        this.ui = ui;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
        this.threadPool = threadPool;
        this.bodyProduced = bodyProduced;
        this.motorProduced = motorProduced;
        this.accessoryProduced = accessoryProduced;
        this.carIdCounter = carIdCounter;
        this.carsSold = carsSold;

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            SwingUtilities.invokeLater(() -> {
                ui.getBodyStockLabel().setText("Склад кузовов: " + bodyStorage.size());
                ui.getMotorStockLabel().setText("Склад двигателей: " + motorStorage.size());
                ui.getAccessoryStockLabel().setText("Склад аксессуаров: " + accessoryStorage.size());
                ui.getCarStockLabel().setText("Склад машин: " + carStorage.size());
                ui.getBodyProducedLabel().setText("Произведено кузовов: " + bodyProduced.get());
                ui.getMotorProducedLabel().setText("Произведено двигателей: " + motorProduced.get());
                ui.getAccessoryProducedLabel().setText("Произведено аксессуаров: " + accessoryProduced.get());
                ui.getCarsProducedLabel().setText("Произведено машин: " + carIdCounter.get());
                ui.getCarsSoldLabel().setText("Продано машин: " + carsSold.get());
                synchronized (threadPool) {
                    ui.getTasksInQueueLabel().setText("Задач в очереди: " + threadPool.getQueueSize());
                }
            });
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}