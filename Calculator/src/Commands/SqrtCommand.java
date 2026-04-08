package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class SqrtCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (storage.getStack().isEmpty()) {
            throw new CommandException("Stack is empty");
        }

        double val = storage.getStack().pop();
        if (val < 0) {
            storage.getStack().push(val);
            throw new CommandException("Negative number");
        }
        storage.getStack().push(Math.sqrt(val));
    }
}


