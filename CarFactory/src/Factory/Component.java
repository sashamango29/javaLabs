package Factory;

public class Component {
    private final int id;

    public Component(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}