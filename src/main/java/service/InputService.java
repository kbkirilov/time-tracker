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
