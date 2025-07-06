package service;

import record.TimeEntry;
import record.TimeEstimate;

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

    public TimeEntry getTimeEntryInput() {
        System.out.printf("Enter project name or choose from the above, or hit ENTER for the last entry (%s): ",
                databaseService.getLastEntryProjectName());
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            projectName = databaseService.getLastEntryProjectName();
        }

        System.out.print("Enter project stage (CD1, CD2, PF, NA): ");
        String projectStage = scanner.nextLine().trim();
        while (!projectStage.equals("CD1") &&
                !projectStage.equals("CD2") &&
                !projectStage.equals("PF") &&
                !projectStage.equals("NA")) {
            System.out.print("Invalid project stage. Enter a valid project stage: ");
            projectStage = scanner.nextLine().trim();
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

        return new TimeEntry(projectName, projectStage, date, start, end);
    }

    public TimeEstimate getTimeEstimateInput() {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();
        while (projectName.isEmpty()) {
            System.out.print("Project name cannot be empty. Please try again: ");
            projectName = scanner.nextLine().trim();
        }

        double cd1EstimateHours = readDoubleWithPrompt("Enter the [CD1] (Client Draft 1) estimate hours (HH): ");
        double cd2EstimateHours = readDoubleWithPrompt("Enter the [CD2] (Client Draft 2) estimate hours (HH): ");
        double pfEstimateHours = readDoubleWithPrompt("Enter the [PR] (Proposed Final) estimate hours (HH): ");

        return new TimeEstimate(projectName, cd1EstimateHours, cd2EstimateHours, pfEstimateHours);
    }

    private double readDoubleWithPrompt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
                continue;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public TimeEntry getTimeEntryEditInput(TimeEntry currentEntry) {
        System.out.print("Project name [" + currentEntry.projectName() + "]: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            projectName = currentEntry.projectName();
        }

        System.out.println("Enter project stage (CD1, CD2, PF, NA): ");
        String projectStage = scanner.nextLine().trim();
        while (!projectStage.equals("CD1") &&
                !projectStage.equals("CD2") &&
                !projectStage.equals("PF") &&
                !projectStage.equals("NA")) {
            System.out.print("Invalid project stage. Enter a valid project stage: ");
            projectStage = scanner.nextLine().trim();
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

        return new TimeEntry(projectName, projectStage, date, startTime, endTime);
    }

    public TimeEstimate getTimeEstimateEditInput(TimeEstimate currentEntry) {
        double cd1EstimateHours;
        double cd2EstimateHours;
        double pfEstimateHours;

        System.out.print("Project name [" + currentEntry.projectName() + "]: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            projectName = currentEntry.projectName();
        }

        System.out.print("Enter the [CD1] (Client Draft 1) estimate hours (HH): ");
        String cd1EstimateHoursInput = scanner.nextLine().trim();
        if (cd1EstimateHoursInput.isEmpty()) {
            cd1EstimateHours = currentEntry.cd1EstimateHours();
        } else {
            cd1EstimateHours = Double.parseDouble(cd1EstimateHoursInput);
        }

        System.out.print("Enter the [CD2] (Client Draft 2) estimate hours (HH): ");
        String cd2EstimateHoursInput = scanner.nextLine().trim();
        if (cd2EstimateHoursInput.isEmpty()) {
            cd2EstimateHours = currentEntry.cd2EstimateHours();
        } else {
            cd2EstimateHours = Double.parseDouble(cd2EstimateHoursInput);
        }

        System.out.print("Enter the [PR] (Proposed Final) estimate hours (HH): ");
        String pfEstimateHoursInput = scanner.nextLine().trim();
        if (pfEstimateHoursInput.isEmpty()) {
            pfEstimateHours = currentEntry.pfEstimateHours();
        } else {
            pfEstimateHours = Double.parseDouble(pfEstimateHoursInput);
        }

        return new TimeEstimate(projectName, cd1EstimateHours, cd2EstimateHours, pfEstimateHours);
    }
}
