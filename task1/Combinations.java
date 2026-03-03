package task1;

import java.util.logging.Logger;
//import java.util.logging.Level;

public class Combinations {

    private static final Logger logger = Logger.getLogger(Combinations.class.getName());

    public static void generate(int n, int k) {
        logger.info("Generating combinations: n=" + n + ", k=" + k);
        int[] comb = new int[k];
        generateRecursive(1, 0, n, k, comb);
    }

    private static void generateRecursive(int start, int depth, int n, int k, int[] comb) {
        if (depth == k) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < k; i++) {
                sb.append(comb[i]).append(" ");
            }
            logger.info("Combination: " + sb.toString().trim());
            return;
        }

        for (int i = start; i <= n; i++) {
            comb[depth] = i;
            logger.fine("Recursion: depth=" + depth + ", start=" + start + ", comb[" + depth + "]=" + i);
            generateRecursive(i + 1, depth + 1, n, k, comb);
        }
    }
}
