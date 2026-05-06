package Factory;

import View.FactoryView;
import View.FactoryUpdater;

import java.io.IOException;

public class Factory {
    public Factory(String configPath) throws IOException {
        Config config = new Config(configPath);

        Initializer initializer = new Initializer(config);

        FactoryView ui = new FactoryView(
                initializer.getBodySupplier(),
                initializer.getMotorSupplier(),
                initializer.getAccessorySuppliers(),
                initializer.getDealers(),
                initializer.getWorkers()
        );

        new FactoryUpdater(
                ui,
                initializer.getBodyStorage(),
                initializer.getMotorStorage(),
                initializer.getAccessoryStorage(),
                initializer.getCarStorage(),
                initializer.getThreadPool(),
                initializer.getBodyProduced(),
                initializer.getMotorProduced(),
                initializer.getAccessoryProduced(),
                initializer.getCarIdCounter(),
                initializer.getCarsSold()
        );

        ui.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                initializer.shutdown();
            }
        });
    }
}