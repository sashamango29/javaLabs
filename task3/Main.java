package task3;

public class Main {
    public static void main(String[] args) {
        TimePoint tp = new TimePoint();
        System.out.println(tp.to_str());

        try {
            tp.from_str("2026-04-01T14:17:35");
            System.out.println(tp.to_str());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        tp.add(3600);
        System.out.println(tp.to_str());

        tp.add(86400 + 90);
        System.out.println(tp.to_str());

        try {
            tp.from_str("2004-02-28T23:59:59");
            tp.add(1);
            System.out.println(tp.to_str());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            tp.from_str("2024-005");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        tp.from_str("2026-04-02T15:19:05");
        tp.add(31536000); // + 1 год
        System.out.println(tp.to_str());

        tp.add(-86400); // -1 день
        System.out.println(tp.to_str());
    }
}
