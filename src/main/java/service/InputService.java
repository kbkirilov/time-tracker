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
    private final DatabaseService databaseService;

    public InputService(Scanner scanner, DatabaseService databaseService) {
        this.scanner = scanner;
        this.databaseService = databaseService;
    }

    public TimeEntry getUserInput() {
        System.out.printf("Enter project name or choose from the above, or hit ENTER for the last entry (%s): ",
                databaseService.getLastEntryProjectName());
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            projectName = databaseService.getLastEntryProjectName();
        }

        System.out.print("Enter date (yyyy-MM-dd or hit ENTER for (today): ");
        String dateInput = scanner.nextLine();
        if (dateInput.isEmpty()) {
            dateInput = "today";
        }
        LocalDate date = dateInput.equalsIgnoreCase("today") ? LocalDate.now() : LocalDate.parse(dateInput, dateFormatter);

        System.out.printf("Enter start time (HH:mm) or hit ENTER for last task's end time (%s): ",
                databaseService.getLastEntryEndTime());
        String startTime = scanner.nextLine();
        if (startTime.isEmpty()) {
            startTime = databaseService.getLastEntryEndTime();
        }
        LocalTime start = LocalTime.parse(startTime, timeFormatter);

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
