package service;

import record.TimeEntry;

import java.util.Scanner;

public class MenuService {
    private final LogService logService;
    private final ReportService reportService;
    private final Scanner scanner = new Scanner(System.in);

    public MenuService(LogService logService, ReportService reportService) {
        this.reportService = reportService;
        this.logService = logService;
    }

    public void run() {
        showMainMenu();
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("""
                        1. Log time
                        2. View reports
                        3. Delete entry
                        4. Exit
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> insertTimeMenu();
                case "2" -> showReportsMenu();
                case "3" -> deleteEntryMenu();
                case "4" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void insertTimeMenu() {
        //TODO Show the latest 5 different project names and give an option to select on of them or write your own.
        TimeEntry entry = logService.getUserInput();
        logService.insertEntry(entry);
        System.out.println("Time logged successfully.");
    }

    private void showReportsMenu() {
        while (true) {
            System.out.println("""
                        --- Reports ---
                        1. Total hours per day
                        2. Total hours per project
                        3. Total hours per project for the week
                        4. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> getWorkedHoursPerDay();
                case "2" -> showProjectReportsSubmenu();
                case "3" -> reportWeeklyProjectHours();
                case "4" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void getWorkedHoursPerDay() {
        var hoursPerDay = reportService.getWorkedHoursPerDay();
        hoursPerDay.forEach((date, hours) -> {
            System.out.println(date + ": " + hours + " hours");
        });
    }

    private void showProjectReportsSubmenu() {
        while (true) {
            System.out.println("""
                        --- Project Reports ---
                        1. View by project name
                        2. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter project name: ");
                    String projectName = scanner.nextLine();
                    getWorkedHoursPerProject(projectName);
                case "2":
                    System.out.println("Bye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void deleteEntryMenu() {
        System.out.print("Enter the ID of the entry to delete: ");
        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);
            logService.deleteEntry(id);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void reportWeeklyProjectHours() {
        reportService.reportWeeklyProjectHours();
    }

    private void getWorkedHoursPerProject(String projectName) {
        var hoursPerProject = reportService.getWorkedHoursPerProject(projectName);
        hoursPerProject.forEach((pName, hours) -> {
            System.out.println(pName + " " + hours + " hours");
        });
    }
}
