package service.menus.reports;

import service.ReportService;
import service.menus.MenuBase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static service.InputService.getValidDate;
import static utils.Constants.INVALID_CHOICE_MESSAGE;

public class PeriodReportMenuService extends MenuBase {
    private final ReportService reportService;

    public PeriodReportMenuService(ReportService reportService, Scanner scanner) {
        super(scanner);
        this.reportService = reportService;
    }

    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("TIME PERIOD REPORTS",
                    "Current week",
                    "Last week",
                    "Current month",
                    "Custom period",
                    "Back to reports menu");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    getCurrentWeekBreakdown();
                    break;
                case 2:
                    getLastWeekBreakdown();
                    break;
                case 3:
                    getCurrentMonthBreakdown();
                    break;
                case 4:
                    getCustomPeriodBreakdown();
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

    private void getCustomPeriodBreakdown() {
        displayMenuHeader("CUSTOM PERIOD BREAKDOWN");

        LocalDate start = getValidDate(scanner);
        LocalDate end = getValidDate(start, scanner);

        reportService.getTimePeriodProjectBreakdown(start, end);

    }

    /**
     * It creates a breakdown for each day, starting from the beginning of the month till the current day
     */
    private void getCurrentMonthBreakdown() {
        displayMenuHeader("CURRENT MONTH BREAKDOWN");
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);

        reportService.getTimePeriodProjectBreakdown(start, now);
    }

    /**
     * Gets the last week's project breakdown.
     */
    private void getLastWeekBreakdown() {
        displayMenuHeader("LAST WEEK BREAKDOWN");

        LocalDate end = LocalDate.now().with(DayOfWeek.MONDAY).minusDays(1);
        LocalDate start = end.with(DayOfWeek.MONDAY);

        reportService.getTimePeriodProjectBreakdown(start, end);
    }

    /**
     * Gets the current week's project breakdown.
     */
    private void getCurrentWeekBreakdown() {
        displayMenuHeader("CURRENT WEEK BREAKDOWN");

        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = LocalDate.now().with(DayOfWeek.SUNDAY);

        reportService.getTimePeriodProjectBreakdown(start, end);
    }




}
