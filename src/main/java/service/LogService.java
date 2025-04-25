package service;

import record.TimeEntry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class LogService {
    private final Scanner scanner;
    private final DatabaseService db;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public LogService(Scanner scanner, DatabaseService db) {
        this.scanner = scanner;
        this.db = db;
    }

    public void insertEntry(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        double roundedHours = (double) Math.round(hours * 1000.0) / 1000;

        System.out.println("Logging time for project: " + entry.projectName());
        System.out.println("Date: " + entry.date() + ", Start: " + entry.start() + ", End: " + entry.end() + ", Hours: " + roundedHours);

        db.insert(entry, roundedHours);
    }

    public void deleteEntry(int id) {
        db.deleteEntryById(id);  // без проверка
        System.out.println("Attempted to delete entry with ID: " + id);
    }

    public TimeEntry getUserInput() {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();

        System.out.print("Enter date (yyyy-MM-dd or 'today'): ");
        String dateInput = scanner.nextLine();
        LocalDate date = dateInput.equalsIgnoreCase("today") ? LocalDate.now() : LocalDate.parse(dateInput, dateFormatter);

        System.out.print("Enter start time (HH:mm): ");
        LocalTime start = LocalTime.parse(scanner.nextLine(), timeFormatter);

        System.out.print("Enter end time (HH:mm): ");
        LocalTime end = LocalTime.parse(scanner.nextLine(), timeFormatter);

        return new TimeEntry(projectName, date, start, end);
    }
}

