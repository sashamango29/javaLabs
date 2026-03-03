package task2;

import java.io.*;
import java.util.*;

public class WordGraphBuilder {

    public Map<String, Set<String>> buildGraph(String filename) throws IOException {

        Map<String, Set<String>> graph = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));

        StringBuilder currentWord = new StringBuilder();
        String prevWord = null;

        int ch;
        while ((ch = reader.read()) != -1) {

            char c = (char) ch;

            if (Character.isLetter(c)) {
                currentWord.append(Character.toLowerCase(c));
            }
            else {

                if (currentWord.length() > 0) {

                    String word = currentWord.toString();

                    if (prevWord != null) {
                        graph.putIfAbsent(prevWord, new HashSet<>());
                        graph.get(prevWord).add(word);
                    }

                    prevWord = word;
                    currentWord.setLength(0);
                }

                if (c == '.') {
                    prevWord = null;
                }
            }
        }

        if (currentWord.length() > 0) {
            String word = currentWord.toString();

            if (prevWord != null) {
                graph.putIfAbsent(prevWord, new HashSet<>());
                graph.get(prevWord).add(word);
            }
        }

        reader.close();

        return graph;
    }
}
