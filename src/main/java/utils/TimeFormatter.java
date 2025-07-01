package utils;

import static utils.Constants.HOURS_IN_WORKING_DAY;

public class TimeFormatter {
    public static String formatHoursToHHMM(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return String.format("%02d:%02d hours", h, m);
    }

    public static String formatHoursToDays(double hours) {
        int h = (int) hours;
        double m = (hours - h) * 100;
        double totalHours = h + (m / 60.0);

        double days = totalHours / HOURS_IN_WORKING_DAY;
        return String.format("%.2f days", days);
    }
}
