package utils;

import record.TimeEntry;

import java.time.Duration;

public class Calculations {

    public static double calculateRoundedHours(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        return (double) Math.round(hours * 1000.0) / 1000;
    }
}
