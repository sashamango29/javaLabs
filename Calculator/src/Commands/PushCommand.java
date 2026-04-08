package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class PushCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("PUSH requires one argument");
        }

        Double number = storage.getVariables().get(args[0]);
        if (number == null) {
            try {
                number = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                throw new CommandException("Invalid number or undefined variable: " + args[0]);
            }
        }

        storage.getStack().push(number);
    }
}
