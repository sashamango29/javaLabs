class Op extends Node {

    enum Type { PLUS, MINUS, MULTIPLY, DIVIDE }

    private final Type type;
    Node left;
    Node right;

    public Op(Type type) {
        this.type = type;
    }

    //@Override
    int calculate() throws InterruptedException {

        if (left == null || right == null)
            throw new RuntimeException("Неполная операция");

        final int[] leftResult = new int[1];
        final int[] rightResult = new int[1];

        Thread leftThread = new Thread(() -> {
            try {
                leftResult[0] = left.calculate();
            } catch (InterruptedException ignored) {}
        });

        Thread rightThread = new Thread(() -> {
            try {
                rightResult[0] = right.calculate();
            } catch (InterruptedException ignored) {}
        });

        leftThread.start();
        rightThread.start();

        leftThread.join();
        rightThread.join();

        int l = leftResult[0];
        int r = rightResult[0];

        switch (type) {
            case PLUS:
                return l + r;
            case MINUS:
                return l - r;
            case MULTIPLY:
                return l * r;
            case DIVIDE:
                if (r == 0) throw new RuntimeException("Деление на ноль");
                return l / r;
        }

        throw new RuntimeException("Неизвестный оператор");
    }

    public String getTypeSymbol() {
        switch (type) {
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
        }
        return "?";
    }
}
