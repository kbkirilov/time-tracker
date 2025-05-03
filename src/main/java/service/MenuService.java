package service;

import record.TimeEntry;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Scanner;

public class MenuService {
    private final LogService logService;
    private final ReportService reportService;
    private final InputService inputService;
    private final Scanner scanner = new Scanner(System.in);

    public MenuService(LogService logService, ReportService reportService, InputService inputService) {
        this.reportService = reportService;
        this.logService = logService;
        this.inputService = inputService;
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
        printLatestFiveProjectsFromDb();
        TimeEntry entry = inputService.getUserInput();
        logService.logInsertEntry(entry);
        System.out.println("Time logged successfully.");
    }

    private void printLatestFiveProjectsFromDb() {
        reportService.printLastFiveUniqueProjectNames();
    }

    private void showReportsMenu() {
        while (true) {
            System.out.println("""
                        --- REPORTS MENU ---
                        1. Daily reports
                        2. Total worked hours per project
                        3. Total worked hours per project for the current week
                        4. Time period project breakdown
                        5. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showHoursPerDaySubmenu();
                case "2" -> showProjectReportsSubmenu();
                case "3" -> reportWeeklyProjectHours();
                case "4" -> showTimePeriodBreakdownSubMenu();
                case "5" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showTimePeriodBreakdownSubMenu() {
        while (true) {
            System.out.println("""
                        --- TIME PERIOD BREAKDOWNS ---
                        1. Current week
                        2. Specific time period
                        3. Back
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    getCurrentWeekBreakdown();
                    break;
                case "2":
                    getTimePeriodBreakdown();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
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
                    System.out.print("Enter date (YYYY-MM-DD / today): ");
                    String input = scanner.nextLine().toLowerCase();
                    LocalDate date = switch (input) {
                        case "today" -> LocalDate.now();
                        case "yesterday" -> LocalDate.now().minusDays(1);
                        default -> LocalDate.parse(input);
                    };
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

    private void getCurrentWeekBreakdown() {
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = LocalDate.now().with(DayOfWeek.SUNDAY);

        reportService.getTimePeriodProjectBreakdown(start, end);
    }

    private void getTimePeriodBreakdown() {
        System.out.print("Enter the start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter the end date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        reportService.getTimePeriodProjectBreakdown(start, end);
    }

    private void getAllWorkedHoursPerDay() {
        reportService.getWorkedHoursPerDay();
    }

    private void deleteEntryMenu() {
        System.out.print("Enter the ID of the entry to delete: ");
        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);
            logService.logDeleteEntry(id);
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
