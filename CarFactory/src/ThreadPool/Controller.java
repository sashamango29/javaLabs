package ThreadPool;

public class Controller implements Runnable {
    private final TaskExecutor taskExecutor;
    private volatile boolean running = true;

    public Controller(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            taskExecutor.executeTask();
            taskExecutor.waitForChange(); 
        }
    }
}
