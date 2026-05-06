import Factory.Factory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new Factory("factory.conf");
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
