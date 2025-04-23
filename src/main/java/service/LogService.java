package service;

import record.TimeEntry;
import java.time.Duration;


public class LogService {
    private final DatabaseService db = new DatabaseService();

    public void log(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        double roundedHours = (double) Math.round(hours * 1000.0) / 1000;

        System.out.println("Logging time for project: " + entry.projectName());
        System.out.println("Date: " + entry.date() + ", Start: " + entry.start() + ", End: " + entry.end() + ", Hours: " + roundedHours);

        db.insert(entry, roundedHours);
    }
}
