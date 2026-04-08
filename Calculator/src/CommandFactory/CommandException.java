package CommandFactory;

public class CommandException extends Exception {
    public CommandException(String cmd) {
        super(cmd);
    }
}
