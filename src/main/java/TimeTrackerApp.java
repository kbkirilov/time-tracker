import record.TimeEntry;
import service.InputService;
import service.DatabaseService;
import service.LogService;
import service.ReportService;

import java.util.Scanner;

public class TimeTrackerApp {
    private final InputService inputService = new InputService();
    private final service.LogService logService = new LogService();
    private final ReportService reportService = new ReportService();
    private final DatabaseService db = new DatabaseService();

    public void run() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {
            System.out.println("Choose an option: ");
            System.out.println("1. Log time");
            System.out.println("2. View report");
            System.out.println("3. Delete entry by ID");
            System.out.println("4. Exit");

            choice = scanner.nextInt();
            scanner.nextLine();  // clear buffer

            switch (choice) {
                case 1:
                    logTime();
                    break;
                case 2:
                    int subChoice = Integer.MAX_VALUE;

                    while (subChoice != 0) {
                        System.out.println("Choose sub-option: ");
                        System.out.println("0. Go back");
                        System.out.println("1. Get total hours worked per day");
                        System.out.println("2. Get total hours worked per project");

                        subChoice = scanner.nextInt();
                        scanner.nextLine();

                        switch (subChoice) {
                            case 0:
                                break;
                            case 1:
                                getWorkedHoursPerDay();
                                break;
                            case 2:
                                System.out.println("Enter project name: ");
                                String project_name = scanner.next();
                                getWorkedHoursPerProject(project_name);
                                break;
                        }
                    }
                    break;
                case 3:
                    deleteEntry(scanner);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void deleteEntry(Scanner scanner) {
        System.out.println("Enter the ID of the entry to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        db.deleteEntryById(id);
    }

    private void logTime() {
        TimeEntry entry = inputService.getUserInput();
        logService.log(entry);
        System.out.println("Time logged successfully.");
    }

    private void getWorkedHoursPerDay() {
        var hoursPerDay = reportService.getWorkedHoursPerDay();
        hoursPerDay.forEach((date, hours) -> {
            System.out.println(date + ": " + hours + " hours");
        });
    }

    private void getWorkedHoursPerProject(String projectName) {
        var hoursPerProject = reportService.getWorkedHoursPerProject(projectName);
        hoursPerProject.forEach((pName, hours) -> {
            System.out.println(pName + " " + hours + " hours");
        });
    }

//    public void getTotalHoursPerDay() {
//        Map<LocalDate, Double> totals = reader.getTotalHoursPerDay();
//        totals.forEach((date, hours) -> System.out.println(date + ": " + hours + " hours"));
//    }
//
//    public void getTotalHoursPerProject(String projectName) {
//        Map<String, Double> totals = reader.getTotalHoursPerProject(projectName);
//        totals.forEach((name, hours) -> System.out.println(name + ": " + hours + " hours"));
//    }
}
