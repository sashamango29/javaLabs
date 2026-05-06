package Factory;

import java.util.concurrent.atomic.AtomicInteger;

public class Supplier implements Runnable {
    private final Storage<Component> storage;
    private final String type;
    private volatile int delay;
    private final AtomicInteger idCounter;
    private final AtomicInteger producedCount;
    private volatile boolean running = true;

    public Supplier(Storage<Component> storage, String type, int initialDelay, AtomicInteger idCounter, AtomicInteger producedCount) {
        this.storage = storage;
        this.type = type;
        this.delay = initialDelay;
        this.idCounter = idCounter;
        this.producedCount = producedCount;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Component component = new Component(idCounter.incrementAndGet());
                storage.put(component);
                producedCount.incrementAndGet();

                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}