package Calculator;

public class StackCalculator {
    public static void main(String[] args) throws Exception {
        Calculator calc = new Calculator();
        try {
            calc.readFile("commands.txt");
            calc.executeAll();           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}