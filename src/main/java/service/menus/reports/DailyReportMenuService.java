package service.menus.reports;

import service.ReportService;
import service.menus.MenuBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static utils.Constants.INVALID_CHOICE_MESSAGE;

public class DailyReportMenuService extends MenuBase {
    private final ReportService reportService;

    public DailyReportMenuService(ReportService reportService, Scanner scanner) {
        super(scanner);
        this.reportService = reportService;
    }

    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("DAILY REPORTS",
                    "All days summary",
                    "Specific day",
                    "Today's report",
                    "Yesterday's report",
                    "Back to reports menu");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    getAllDaysSummary();
                    break;
                case 2:
                    getSpecificDayReport();
                    break;
                case 3:
                    reportService.getTimePeriodProjectBreakdown(LocalDate.now(), LocalDate.now());
                    break;
                case 4:
                    reportService.getTimePeriodProjectBreakdown(
                            LocalDate.now().minusDays(1),
                            LocalDate.now().minusDays(1)
                    );
                    break;
                case 5:
                    isRunning = false;
                    back();
                    break;
                default:
                    System.out.println(INVALID_CHOICE_MESSAGE);
                    break;
            }
        }
    }

    /**
     * Gets a specific day's report from user input.
     * Note
     */
    private void getSpecificDayReport() {
        System.out.print("Enter date (YYYY-MM-DD): ");

        String input = scanner.nextLine();

        try {
            LocalDate date = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
            reportService.getTimePeriodProjectBreakdown(date, date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    /**
     * Gets the report for all worked hours per day.
     */
    private void getAllDaysSummary() {
        displayMenuHeader("ALL DAYS SUMMARY");
        reportService.getWorkedHoursPerDay();
    }

    /**
     * Gets the report for worked hours on a particular day.
     *
     * @param date The date to get the report for
     */
    private void getWorkedHoursForParticularDay(LocalDate date) {
        reportService.getTimePeriodProjectBreakdown(date, date);
    }
}
