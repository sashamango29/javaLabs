package Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "8080"));
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(properties.getProperty("server.logging", "true"));
    }

    public static String getServerHost() {
        return properties.getProperty("server.host", "localhost");
    }
}
