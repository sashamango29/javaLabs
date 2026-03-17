package parseTree;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        TreeGenerator generator = new TreeGenerator();

        Tree tree = generator.generate(3);

        System.out.printf("Случайное дерево:\n");

        tree.print();

        int result = tree.calculate();

        System.out.printf("Результат вычисления: %d\n", result);
    }
}
