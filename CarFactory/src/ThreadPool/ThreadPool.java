package ThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final Thread[] threads;
    private volatile boolean isShutdown = false;

    public ThreadPool(int threadCount) {
        taskQueue = new LinkedBlockingQueue<>();
        threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                while (!taskQueue.isEmpty() || !isShutdown) {
                    try {
                        Runnable task = taskQueue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            threads[i].start();
        }
    }

    public void submit(Runnable task) {
        if (!isShutdown) {
            taskQueue.offer(task);
        }
    }

    public void shutdown() {
        isShutdown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    public int getQueueSize() {
        return taskQueue.size();
    }
}