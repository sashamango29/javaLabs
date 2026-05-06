package Factory;

import java.util.ArrayList;
import java.util.List;

public class Storage<T> {
    private final List<T> items;
    private final int capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
    }

    public synchronized void put(T item) throws InterruptedException {
        while (items.size() >= capacity) {
            wait(); 
        }
        items.add(item);
        notifyAll(); 
    }

    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) {
            wait(); 
        }
        T item = items.removeFirst();
        notifyAll(); 
        return item;
    }

    public synchronized int size() {
        return items.size();
    }
}