package CommandFactory;

import Commands.*;

import java.io.*;
import java.util.*;

public class CommandFactory {
    private final Map<String, String> commandClassMap = new HashMap<>();

    public CommandFactory() throws IOException {
        loadCommands();
    }

    private void loadCommands() throws IOException {
        try {
            InputStream input = getClass().getResourceAsStream("commands.properties");
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                commandClassMap.put(key.toUpperCase(), props.getProperty(key));
            }
        } catch (IOException e) {
            throw new IOException("Error loading commands.properties: " + e.getMessage());
        }
    }

    public Command createCommand(String cmd) throws Exception {
        String className = commandClassMap.get(cmd.toUpperCase());
        if (className == null) {
            throw new CommandException("Unknown command: " + cmd);
        }

        Class<?> clazz = Class.forName(className);
        if (!Command.class.isAssignableFrom(clazz)) {
            throw new CommandException("Class " + className + " does not implement Command");
        }

        return (Command) clazz.getDeclaredConstructor().newInstance();
    }
}