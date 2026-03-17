import java.util.Random;

public class TreeGenerator {

    private Random random = new Random();

    public Tree generate(int maxDepth) {
        Node root = generateNode(maxDepth);
        return new Tree(root);
    }

    private Node generateNode(int depth) {
        if (depth == 0) {
            int value = random.nextInt(10) + 1;
            return new Value(value);
        }

        boolean makeLeaf = random.nextBoolean(); 
        if (makeLeaf) {
            int value = random.nextInt(10) + 1;
            return new Value(value);
        }

        Op.Type[] types = Op.Type.values();
        Op.Type type = types[random.nextInt(types.length)];

        Op node = new Op(type);

        node.left = generateNode(depth - 1);
        node.right = generateNode(depth - 1);

        return node;
    }
}