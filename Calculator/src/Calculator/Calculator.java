package Calculator;

import CommandFactory.*;
import Commands.Command;

import java.io.*;
import java.util.*;

public class Calculator {
    private final ExecutionContext storage = new ExecutionContext();
    private final CommandFactory commandFactory = new CommandFactory();

    private String[] commands;                
    private final Map<String, Integer> labels = new HashMap<>(); 

    public Calculator() throws CommandException, IOException {}

    public void readFile(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> temp = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) 
                    continue;

                if (line.endsWith(":")) { 
                    String labelName = line.substring(0, line.length() - 1).toUpperCase(); 
                    labels.put(labelName, temp.size()); 
                } else {
                    temp.add(line); 
                }
            }

            commands = temp.toArray(new String[0]); 
        } catch (Exception e) {
            System.out.println("Error reading file " + file);
        }
    }

    public void executeAll() {
        int i = 0;
        while (i < commands.length) {
            String line = commands[i];
            String[] split = line.split("\\s+");
            String cmd = split[0].toUpperCase();
            String[] args = Arrays.copyOfRange(split, 1, split.length);

            try {
                if (cmd.equals("GOTO")) {
                    String label = args[0].toUpperCase(); 
                    if (!labels.containsKey(label)) {
                        System.out.println("Unknown label: " + args[0]);
                    } else {
                        double top = 1;
                        if (!storage.getStack().isEmpty()) {
                            top = storage.getStack().peek(); 
                        }

                        if (top == 0.0) {
                            i = labels.get(label);
                            continue; 
                        }
                    }
                } else {
                    Command command = commandFactory.createCommand(cmd);
                    command.execute(args, storage);
                }
            } catch (CommandException e) {
                System.out.println("Error command '" + line + "': " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error in command '" + line + "': " + e.getMessage());
            }

            i++; 
        }
    }
}
