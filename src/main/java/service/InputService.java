package service;

import record.TimeEntry;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputService {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public InputService(Scanner scanner) {
        this.scanner = scanner;

    }

    public TimeEntry getUserInput() {
        System.out.print("Enter project name or choose from the above: ");
        String projectName = scanner.nextLine().trim();

        System.out.print("Enter date (yyyy-MM-dd or 'today'): ");
        String dateInput = scanner.nextLine();
        LocalDate date = dateInput.equalsIgnoreCase("today") ? LocalDate.now() : LocalDate.parse(dateInput, dateFormatter);

        System.out.print("Enter start time (HH:mm): ");
        LocalTime start = LocalTime.parse(scanner.nextLine(), timeFormatter);

        System.out.print("Enter end time (HH:mm): ");
        LocalTime end = LocalTime.parse(scanner.nextLine(), timeFormatter);

        return new TimeEntry(projectName, date, start, end);
    }

    public TimeEntry getEditInput(TimeEntry currentEntry) {
        System.out.print("Project name [" + currentEntry.projectName() + "]: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            projectName = currentEntry.projectName();
        }

        System.out.println("Date (yyyy-MM-dd or 'today') [" + currentEntry.date() + "]: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = LocalDate.parse(String.valueOf(currentEntry.date()));
        if (!dateInput.isEmpty()) {
            if (dateInput.equalsIgnoreCase("today")) {
                date = LocalDate.now();
            } else {
                date = LocalDate.parse(dateInput);
            }
        }

        System.out.println("Start Time (HH:mm) [" + currentEntry.start() + "]:");
        String startTimeInput = scanner.nextLine().trim();
        LocalTime startTime = currentEntry.start();
        if (!startTimeInput.isEmpty()) {
            startTime = LocalTime.parse(startTimeInput, timeFormatter);
        }

        System.out.println("End Time (HH:mm) [" + currentEntry.end() + "]:");
        String endTimeInput = scanner.nextLine().trim();
        LocalTime endTime = currentEntry.end();
        if (!endTimeInput.isEmpty()) {
            endTime = LocalTime.parse(endTimeInput, timeFormatter);
        }

        return new TimeEntry(projectName, date, startTime, endTime);
    }
}
