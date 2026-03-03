package task1;

public class Main {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        Combinations.generate(n, k);
    }
    
}
