package Factory;

public class Car {
    private final int id;
    private final Component body;
    private final Component motor;
    private final Component accessory;

    public Car(int id, Component body, Component motor, Component accessory) {
        this.id = id;
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }

    public int getId() {
        return id;
    }

    public Component getBody() {
        return body;
    }

    public Component getMotor() {
        return motor;
    }

    public Component getAccessory() {
        return accessory;
    }
}
