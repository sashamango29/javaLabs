package task3;

public class TimePoint {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public TimePoint() {
        this.year = 2000;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
    }

    public void from_str(String isoDate) {
        if (isoDate == null || !isoDate.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Строка не соответствует ISO формату yyyy-MM-ddTHH:mm:ss");
        }

        String[] parts = isoDate.split("T");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");

        this.year = Integer.parseInt(dateParts[0]);
        this.month = Integer.parseInt(dateParts[1]);
        this.day = Integer.parseInt(dateParts[2]);

        this.hour = Integer.parseInt(timeParts[0]);
        this.minute = Integer.parseInt(timeParts[1]);
        this.second = Integer.parseInt(timeParts[2]);
    }

    public String to_str() {
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d",
                year, month, day, hour, minute, second);
    }

    private boolean isLeapYear(int y) {
        return (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0));
    }

    public void add(long secondsToAdd) {
        long totalSeconds = second + secondsToAdd;

        second = (int)(totalSeconds % 60);
        long totalMinutes = minute + totalSeconds / 60;

        if (second < 0) {
            second += 60;
            totalMinutes--;
        }

        minute = (int)(totalMinutes % 60);
        long totalHours = hour + totalMinutes / 60;

        if (minute < 0) {
            minute += 60;
            totalHours--;
        }

        hour = (int)(totalHours % 24);
        long totalDays = day + totalHours / 24;

        if (hour < 0) {
            hour += 24;
            totalDays--;
        }

        day = (int) totalDays;

        int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};

        while (true) {
            int dim = daysInMonth[month - 1];
            if (month == 2 && isLeapYear(year))
                dim = 29;

            if (day > dim) {
                day -= dim;
                month++;
                if (month > 12) {
                    month = 1;
                    year++;
                }
            } else if (day <= 0) {
                month--;
                if (month < 1) {
                    month = 12;
                    year--;
                }
                dim = daysInMonth[month - 1];
                if (month == 2 && isLeapYear(year))
                    dim = 29;
                day += dim;
            } else {
                break;
            }
        }
    }
}
