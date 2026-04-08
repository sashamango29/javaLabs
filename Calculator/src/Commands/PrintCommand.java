package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public class PrintCommand implements Command {
    @Override
    public void execute(String[] args, ExecutionContext storage) throws CommandException {
        if (storage.getStack().isEmpty()) {
            throw new CommandException("Stack is empty");
        }

        System.out.println(storage.getStack().peek());
    }
}