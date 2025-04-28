package service;

import record.TimeEntry;

import java.time.LocalDate;
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
                        --- MAIN MENU ---
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
                        --- REPORTS MENU ---
                        1. Daily reports
                        2. Total worked hours per project
                        3. Total worked hours per project for the current week
                        4. Weekly project breakdown
                        5. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showHoursPerDaySubmenu();
                case "2" -> showProjectReportsSubmenu();
                case "3" -> reportWeeklyProjectHours();
                case "4" -> getWeeklyProjectBreakdown();
                case "5" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showHoursPerDaySubmenu() {
        while (true) {
            System.out.println("""
                        --- PROJECT REPORTS ---
                        1. Get all hours for each day
                        2. Get hours for a given day
                        3. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    getAllWorkedHoursPerDay();
                    break;
                case "2":
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(scanner.nextLine());
                    getWorkedHoursForParticularDay(date);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showProjectReportsSubmenu() {
        while (true) {
            System.out.println("""
                        --- PROJECT REPORTS ---
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

    private void getWorkedHoursForParticularDay(LocalDate date) {
        reportService.getWorkedHoursForParticularDay(date);
    }

    private void getWeeklyProjectBreakdown() {
        reportService.getWeeklyProjectBreakdown();
    }

    private void getAllWorkedHoursPerDay() {
        reportService.getWorkedHoursPerDay();
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
        reportService.getWorkedHoursPerProject(projectName);
    }
}
