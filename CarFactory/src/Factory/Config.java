package Factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private final Properties config;

    public Config(String configPath) throws IOException {
        config = new Properties();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
        }
    }

    public int getBodyStorageSize() {
        return Integer.parseInt(config.getProperty("StorageBodySize", "100"));
    }
    public int getMotorStorageSize() {
        return Integer.parseInt(config.getProperty("StorageMotorSize", "100"));
    }
    public int getAccessoryStorageSize() {
        return Integer.parseInt(config.getProperty("StorageAccessorySize", "500"));
    }
    public int getCarStorageSize() {
        return Integer.parseInt(config.getProperty("StorageAutoSize", "100"));
    }
    public int getAccessorySupplierCount() {
        return Integer.parseInt(config.getProperty("AccessorySuppliers", "5"));
    }
    public int getWorkerCount() {
        return Integer.parseInt(config.getProperty("Workers", "10"));
    }
    public int getDealerCount() {
        return Integer.parseInt(config.getProperty("Dealers", "20"));
    }
    public boolean isLogEnabled() {
        return Boolean.parseBoolean(config.getProperty("LogSale", "true"));
    }
}