package service;

import record.TimeEntry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class LogService {
    private final DatabaseService db;

    public LogService(DatabaseService db) {
        this.db = db;
    }

    public void logInsertEntry(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        double roundedHours = (double) Math.round(hours * 1000.0) / 1000;

        System.out.println("Logging time for project: " + entry.projectName());
        System.out.println("Date: " + entry.date() + ", Start: " + entry.start() + ", End: " + entry.end() + ", Hours: " + roundedHours);

        db.createEntry(entry, roundedHours);
    }

    public void logDeleteEntry(int id) {
        db.deleteEntryById(id);  // без проверка
        System.out.println("Attempted to delete entry with ID: " + id);
    }
}

