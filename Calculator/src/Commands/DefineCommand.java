package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class DefineCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("DEFINE requires name and value");
        }
        try {
            double value = Double.parseDouble(args[1]);
            storage.getVariables().put(args[0], value);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid number format for value: " + args[1]);
        }
    }
}
