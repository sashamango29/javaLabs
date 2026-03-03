package task2;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {

        WordGraphBuilder builder = new WordGraphBuilder();
        Map<String, Set<String>> graph = builder.buildGraph(args[0]);

        for (String from : graph.keySet()) {
            Set<String> toWords = graph.get(from);
            System.out.printf("%s -> %s%n", from, toWords);
        }
    }
}
