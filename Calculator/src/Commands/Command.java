package Commands;

import Calculator.ExecutionContext;
import CommandFactory.CommandException;

public interface Command {
    void execute(String[] args, ExecutionContext storage) throws CommandException;
}
