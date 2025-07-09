package service.menus.reports;

import service.DisplayService;
import service.ReportService;
import service.menus.MenuBase;

import java.util.Scanner;

import static utils.Constants.INVALID_CHOICE_MESSAGE;


public class ReportMenuService extends MenuBase {
    private final ReportService reportService;
    private final DailyReportMenuService dailyReportMenuService;
    private final ProjectReportMenuService projectReportMenuService;
    private final PeriodReportMenuService periodReportMenuService;

    public ReportMenuService(ReportService reportService, Scanner scanner, DisplayService displayService) {
        super(scanner);
        this.reportService = reportService;

        this.dailyReportMenuService = new DailyReportMenuService(reportService, scanner);
        this.projectReportMenuService = new ProjectReportMenuService(reportService, scanner);
        this.periodReportMenuService = new PeriodReportMenuService(reportService, scanner);
    }

    /**
     * Displays the reports menu and handles user input.
     */
    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("REPORTS",
                    "Daily Reports",
                    "Project Reports",
                    "Custom Period Reports",
                    "Back to main menu");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    dailyReportMenuService.show();
                    break;
                case 2:
                    projectReportMenuService.show();
                    break;
                case 3:
                    periodReportMenuService.show();
                    break;
                case 4:
                    isRunning = false;
                    back();
                    break;
                default:
                    System.out.println(INVALID_CHOICE_MESSAGE);
                    break;
            }
        }
    }
}
