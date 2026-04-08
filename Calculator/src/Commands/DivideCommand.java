package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class DivideCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (storage.getStack().size() < 2) {
            throw new CommandException("Not enough elements in stack");
        }

        double b = storage.getStack().pop();
        if (b == 0) {
            storage.getStack().push(b);
            throw new CommandException("Division by zero");
        }
        double a = storage.getStack().pop();
        storage.getStack().push(a / b);
    }
}
