package utils;

public class TimeFormatter {
    public static String formatHoursToHHMM(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return String.format("%02d:%02d hours", h, m);
    }
}
