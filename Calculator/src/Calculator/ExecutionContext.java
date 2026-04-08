package Calculator;

import java.util.*;

public class ExecutionContext {
    private final Stack<Double> stack = new Stack<>();
    private final Map<String, Double> variables = new HashMap<>();

    public Stack<Double> getStack() {
        return stack;
    }

    public Map<String, Double> getVariables() {
        return variables;
    }
}
