package service.menus.reports;

import service.ReportService;
import service.menus.MenuBase;

import java.util.Scanner;

import static utils.Constants.INVALID_CHOICE_MESSAGE;

public class ProjectReportMenuService extends MenuBase {
    private final ReportService reportService;

    public ProjectReportMenuService(ReportService reportService, Scanner scanner) {
        super(scanner);
        this.reportService = reportService;
    }

    /**
     * Displays the project reports menu and handles user input.
     */
    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("PROJECT REPORTS",
                    "View hours by project's name",
                    "Back to reports menu");

            int choice = getChoice();

            switch (choice) {
                case 1 -> getHoursByProjectName();
                case 2 -> {
                    isRunning = false;
                    back();
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    private void getHoursByProjectName() {
        reportService.printLastFiveUniqueProjectNames();
        displayMenuHeader("PROJECT HOURS");
        System.out.println("Enter project name: ");
        String projectName = scanner.nextLine();

        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty.");
            return;
        }

        getWorkedHoursPerProject(projectName);
    }

    /**
     * Gets worked hours for a specific project.
     *
     * @param projectName The name of the project
     */
    private void getWorkedHoursPerProject(String projectName) {
        reportService.getWorkedHoursPerProject(projectName);
    }
}
