class Value extends Node {

    private final int value;

    public Value(int value) {
        this.value = value;
    }

    int calculate() {
        return value;
    }

    public int getValue() {
        return value;
    }
}