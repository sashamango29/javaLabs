package Factory;

import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final Storage<Component> bodyStorage;
    private final Storage<Component> motorStorage;
    private final Storage<Component> accessoryStorage;
    private final Storage<Car> carStorage;
    private final AtomicInteger carIdCounter;
    private volatile int delay;

    public Worker(Storage<Component> bodyStorage, Storage<Component> motorStorage,
                  Storage<Component> accessoryStorage, Storage<Car> carStorage,
                  AtomicInteger carIdCounter, int initialDelay) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
        this.carIdCounter = carIdCounter;
        this.delay = initialDelay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            Component body = bodyStorage.take();
            Component motor = motorStorage.take();
            Component accessory = accessoryStorage.take();

            Car car = new Car(carIdCounter.incrementAndGet(), body, motor, accessory);
            carStorage.put(car);

            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}