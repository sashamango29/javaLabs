package Factory;

import ThreadPool.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class Initializer {
    private final Storage<Component> bodyStorage;
    private final Storage<Component> motorStorage;
    private final Storage<Component> accessoryStorage;
    private final Storage<Car> carStorage;
    private final ThreadPool threadPool;
    private final AtomicInteger bodyIdCounter = new AtomicInteger();
    private final AtomicInteger motorIdCounter = new AtomicInteger();
    private final AtomicInteger accessoryIdCounter = new AtomicInteger();
    private final AtomicInteger carIdCounter = new AtomicInteger();
    private final AtomicInteger bodyProduced = new AtomicInteger();
    private final AtomicInteger motorProduced = new AtomicInteger();
    private final AtomicInteger accessoryProduced = new AtomicInteger();
    private final AtomicInteger carsSold = new AtomicInteger();
    private final Supplier bodySupplier;
    private final Supplier motorSupplier;
    private final Supplier[] accessorySuppliers;
    private final Worker[] workers;
    private final Dealer[] dealers;
    private final Controller controller;
    private final PrintWriter logWriter;
    private final boolean logEnabled;
    private final int workerCount;

    private static final int MIN_SPEED = 100;
    private static final int MAX_DELAY = 5100;

    public Initializer(Config config) throws IOException {
        // Инициализация складов
        bodyStorage = new Storage<Component>(config.getBodyStorageSize());
        motorStorage = new Storage<Component>(config.getMotorStorageSize());
        accessoryStorage = new Storage<Component>(config.getAccessoryStorageSize());
        carStorage = new Storage<Car>(config.getCarStorageSize());

        // Инициализация лога
        logEnabled = config.isLogEnabled();
        logWriter = logEnabled ? new PrintWriter(new FileWriter("logs", true)) : null;

        // Инициализация поставщиков
        bodySupplier = new Supplier(bodyStorage, "Body", speedToDelay(1100), bodyIdCounter, bodyProduced);
        motorSupplier = new Supplier(motorStorage, "Motor", speedToDelay(1100), motorIdCounter, motorProduced);
        new Thread(bodySupplier).start();
        new Thread(motorSupplier).start();

        accessorySuppliers = new Supplier[config.getAccessorySupplierCount()];
        for (int i = 0; i < config.getAccessorySupplierCount(); i++) {
            accessorySuppliers[i] = new Supplier(accessoryStorage, "Accessory", speedToDelay(1100), accessoryIdCounter, accessoryProduced);
            new Thread(accessorySuppliers[i]).start();
        }

        // Инициализация рабочих и пула потоков
        workerCount = config.getWorkerCount();
        threadPool = new ThreadPool(workerCount);
        workers = new Worker[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage, carIdCounter, speedToDelay(1100));
        }

        // Инициализация дилеров
        dealers = new Dealer[config.getDealerCount()];
        for (int i = 0; i < config.getDealerCount(); i++) {
            dealers[i] = new Dealer(carStorage, i + 1, speedToDelay(1100), logWriter, logEnabled, carsSold);
            new Thread(dealers[i]).start();
        }

        // Инициализация контроллера и TaskExecutor
        TaskExecutor taskExecutor = new TaskExecutor(carStorage, bodyStorage, motorStorage, accessoryStorage, threadPool, workers[0], workerCount);
        controller = new Controller(taskExecutor);
        new Thread(controller).start();
    }

    private int speedToDelay(int speed) {
        if (speed <= MIN_SPEED) return MAX_DELAY;
        return MAX_DELAY * MIN_SPEED / speed;
    }

    public Storage<Component> getBodyStorage() {
        return bodyStorage;
    }

    public Storage<Component> getMotorStorage() {
        return motorStorage;
    }

    public Storage<Component> getAccessoryStorage() {
        return accessoryStorage;
    }

    public Storage<Car> getCarStorage() {
        return carStorage;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public AtomicInteger getBodyIdCounter() {
        return bodyIdCounter;
    }

    public AtomicInteger getMotorIdCounter() {
        return motorIdCounter;
    }

    public AtomicInteger getAccessoryIdCounter() {
        return accessoryIdCounter;
    }

    public AtomicInteger getCarIdCounter() {
        return carIdCounter;
    }

    public AtomicInteger getBodyProduced() {
        return bodyProduced;
    }

    public AtomicInteger getMotorProduced() {
        return motorProduced;
    }

    public AtomicInteger getAccessoryProduced() {
        return accessoryProduced;
    }

    public AtomicInteger getCarsSold() {
        return carsSold;
    }

    public Supplier getBodySupplier() {
        return bodySupplier;
    }

    public Supplier getMotorSupplier() {
        return motorSupplier;
    }

    public Supplier[] getAccessorySuppliers() {
        return accessorySuppliers;
    }

    public Worker[] getWorkers() {
        return workers;
    }

    public Dealer[] getDealers() {
        return dealers;
    }

    public Controller getController() {
        return controller;
    }

    public PrintWriter getLogWriter() {
        return logWriter;
    }

    public void shutdown() {
        bodySupplier.stop();
        motorSupplier.stop();
        for (Supplier supplier : accessorySuppliers) supplier.stop();
        for (Dealer dealer : dealers) dealer.stop();
        controller.stop();
        threadPool.shutdown();
        if (logWriter != null) logWriter.close();
    }
}