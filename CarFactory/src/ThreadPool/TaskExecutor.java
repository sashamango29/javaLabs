package ThreadPool;

import Factory.Car;
import Factory.Component;
import Factory.Storage;
import Factory.Worker;

public class TaskExecutor {
    private final Storage<Car> carStorage;
    private final Storage<Component> bodyStorage;
    private final Storage<Component> motorStorage;
    private final Storage<Component> accessoryStorage;
    private final ThreadPool threadPool;
    private final Worker workerPrototype;
    private final int workerCount;

    public TaskExecutor(Storage<Car> carStorage, Storage<Component> bodyStorage,
                        Storage<Component> motorStorage, Storage<Component> accessoryStorage,
                        ThreadPool threadPool, Worker workerPrototype, int workerCount) {
        this.carStorage = carStorage;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.threadPool = threadPool;
        this.workerPrototype = workerPrototype;
        this.workerCount = workerCount;
    }

    public void executeTask() {
        boolean canAssemble = false;
        int queueSize;
        
        synchronized (threadPool) {
            queueSize = threadPool.getQueueSize();
        }
        synchronized (bodyStorage) {
            synchronized (motorStorage) {
                synchronized (accessoryStorage) {
                    if (bodyStorage.size() > 0 && motorStorage.size() > 0 && accessoryStorage.size() > 0) {
                        canAssemble = true;
                    }
                }
            }
        }

        if (canAssemble && queueSize < workerCount) {
            threadPool.submit(() -> {
                workerPrototype.run();
                synchronized (carStorage) {
                    carStorage.notifyAll(); // уведомляем о возможном изменении
                }
            });
        }
    }

    public void waitForChange() {
        try {
            synchronized (carStorage) {
                carStorage.wait(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}