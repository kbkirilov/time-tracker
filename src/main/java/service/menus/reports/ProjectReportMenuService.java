package service.menus.reports;

import service.ReportService;
import service.menus.MenuBase;

import java.util.Scanner;

import static utils.Constants.NUMBER_OF_PROJECTS;
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
                    "View detailed report by project name",
                    "View hours by project's name",
                    "View hours comparison by project's name",
                    "View hours comparison on last 10 projects",
                    "Back to reports menu");

            int choice = getChoice();

            switch (choice) {
                case 1 -> getDetailedReportByProjectName();
                case 2 -> getHoursByProjectName();
                case 3 -> getHoursComparisonByProjectName();
                case 4 -> getLastTenProjectProgress();
                case 5 -> {
                    isRunning = false;
                    back();
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    private void getLastTenProjectProgress() {
        displayMenuHeader("LAST 10 PROJECT PROGRESS REPORT");

        reportService.getLastTenProjectProgress();
    }

    private void getHoursComparisonByProjectName() {
        reportService.printLastXProjectNames(NUMBER_OF_PROJECTS);
        displayMenuHeader("HOURS COMPARISON REPORT");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();

        while (projectName.isEmpty()) {
            System.out.print("Project name cannot be empty. Please enter valid project name: ");
            projectName = scanner.nextLine();
        }

        reportService.getHoursComparisonByProjectName(projectName);
    }

    private void getDetailedReportByProjectName() {
        reportService.printLastXProjectNames(NUMBER_OF_PROJECTS);
        displayMenuHeader("DETAILED PROJECT REPORT");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();

        while (projectName.isEmpty()) {
            System.out.print("Project name cannot be empty. Please enter valid project name: ");
            projectName = scanner.nextLine();
        }

        reportService.getDetailedReportByProjectName(projectName);
    }

    private void getHoursByProjectName() {
        reportService.printLastXProjectNames(NUMBER_OF_PROJECTS);
        displayMenuHeader("PROJECT HOURS");
        System.out.println("Enter project name: ");
        String projectName = scanner.nextLine();

        while (projectName.isEmpty()) {
            System.out.print("Project name cannot be empty. Please enter valid project name: ");
            projectName = scanner.nextLine();
        }

        reportService.getWorkedHoursPerProject(projectName);
    }
}
