package ChatServer;

import java.time.LocalDateTime;

public class Logger {
    private final boolean loggingEnabled;

    public Logger(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public void log(String message) {
        if (loggingEnabled) {
            System.out.printf("[%s] %s%n", LocalDateTime.now(), message);
        }
    }
}