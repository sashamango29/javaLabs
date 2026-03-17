class Tree {

    private final Node root;

    public Tree(Node root) {
        this.root = root;
    }

    public int calculate() throws InterruptedException {
        if (root == null)
            throw new RuntimeException("Пустое дерево");
        return root.calculate();
    }

    public void print() {
        printNode(root, 0);
    }

    private void printNode(Node node, int level) {
        if (node == null) return;

        for (int i = 0; i < level; i++)
            System.out.printf("  ");

        if (node instanceof Value val) {
            System.out.printf("Value: %d%n", val.getValue());
        } else if (node instanceof Op op) {
            System.out.printf("Op: %s%n", op.getTypeSymbol());
            printNode(op.left, level + 1);
            printNode(op.right, level + 1);
        }
    }
}