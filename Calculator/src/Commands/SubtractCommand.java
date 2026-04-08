package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class SubtractCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (storage.getStack().size() < 2) {
            throw new CommandException("Not enough elements in stack");
        }

        double b = storage.getStack().pop();
        double a = storage.getStack().pop();
        storage.getStack().push(a - b);
    }
}