package service;

import record.TimeEntry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class LogService {
    private final DatabaseService db;
    private final DisplayService displayService;

    public LogService(DatabaseService db, DisplayService displayService) {
        this.db = db;
        this.displayService = displayService;
    }

    public void logInsertEntry(TimeEntry entry) {
        double roundedHours = calculateRoundedHours(entry);

        System.out.println("Logging time for project: " + entry.projectName());
        displayService.displayTimeEntryDetails(entry, roundedHours);
        //        System.out.println("Date: " + entry.date() + ", Start: " + entry.start() + ", End: " + entry.end() + ", Hours: " + roundedHours);

        db.createEntry(entry, roundedHours);
    }

    public void logDeleteEntry(int id) {
        db.deleteEntryById(id);  // без проверка
        System.out.println("Attempted to delete entry with ID: " + id);
    }


    public void logUpdateEntry(int id, TimeEntry entry) {
        double roundedHours = calculateRoundedHours(entry);
        System.out.println("Updating project with name: " + entry.projectName());
        displayService.displayTimeEntryDetails(entry);

        db.updateEntryById(id, entry, roundedHours);

    }

    private double calculateRoundedHours(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        return (double) Math.round(hours * 1000.0) / 1000;
    }
}

