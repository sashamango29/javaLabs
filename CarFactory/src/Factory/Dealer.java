package Factory;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Dealer implements Runnable {
    private final Storage<Car> carStorage;
    private final int dealerId;
    private volatile int delay;
    private volatile boolean running = true;
    private final PrintWriter logWriter;
    private final boolean logEnabled;
    private final AtomicInteger carsSold;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Dealer(Storage<Car> carStorage, int dealerId, int initialDelay, PrintWriter logWriter,
                  boolean logEnabled, AtomicInteger carsSold) {
        this.carStorage = carStorage;
        this.dealerId = dealerId;
        this.delay = initialDelay;
        this.logWriter = logWriter;
        this.logEnabled = logEnabled;
        this.carsSold = carsSold;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void stop() {
        running = false;
        synchronized (carStorage) {
            carStorage.notifyAll();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                Car car = carStorage.take();
                carsSold.incrementAndGet();

                if (logEnabled) {
                    String timestamp = LocalDateTime.now().format(formatter);
                    String logMessage = String.format("%s: Dealer %d: Auto %d (Body: %d, Motor: %d, Accessory: %d)",
                            timestamp, dealerId, car.getId(),
                            car.getBody().getId(), car.getMotor().getId(), car.getAccessory().getId());
                    logWriter.println(logMessage);
                    logWriter.flush();
                }
                System.out.println("Dealer " + dealerId + ": Car #" + car.getId() + " sold, total sold: " + carsSold.get());

                Thread.sleep(delay);
                synchronized (carStorage) {
                    carStorage.notifyAll(); // уведомляем о взятии машины
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}