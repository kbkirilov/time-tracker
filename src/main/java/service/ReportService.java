package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static utils.Constants.*;

public class ReportService {

    private final DatabaseService db;
    private final DisplayService displayService;

    public ReportService(DatabaseService db, DisplayService displayService) {
        this.db = db;
        this.displayService = displayService;
    }

    public void getWorkedHoursPerDay() {
        Map<String, Double> result = db.getWorkedHoursPerDay();
        displayService.printTwoColumnsTableWithContent(DATE, TOTAL_HOURS, result);
    }

    public void getWorkedHoursPerProject(String projectName) {
        Map<String, Double> result = db.getWorkedHoursPerProject(projectName);
        displayService.printTwoColumnsTableWithContent(PROJECT_NAME_HEADER, TOTAL_HOURS, result);
    }

    public void getWorkedHoursForParticularDay(LocalDate date) {
        double hours = db.getWorkedHoursForParticularDay(date);
        displayService.printTwoColumnHeaders(date.toString(), formatHoursToHHMM(hours));
    }


    public void reportWeeklyProjectHours() {
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = LocalDate.now().with(DayOfWeek.SUNDAY);

        Map<String, Double> result = db.getWeeklyWorkedHoursPerProject(start, end);

        displayService.printTableHeader(TIME_PERIOD_HEADER, start.toString(), end.toString());
        displayService.printTwoColumnsTableWithContent(PROJECT_NAME_HEADER, HOURS_HEADER, result);
    }

    public void getTimePeriodProjectBreakdown(LocalDate start, LocalDate end) {
        Map<LocalDate, Map<String, Double>> result = db.getWeeklyProjectReport(start, end);

        displayService.printTableHeader(TIME_PERIOD_HEADER, start.toString(), end.toString());

        // For each day of the week
        LocalDate current = start;
        while (!current.isAfter(end)) {
            Map<String, Double> dayProjects = result.getOrDefault(current, Collections.emptyMap());

            System.out.println("\n" + formatDateWithDayOfWeek(current.toString()));
            displayService.printTwoColumnHeaders(PROJECT_NAME_HEADER, HOURS_HEADER, ROW_DELIMITER);

            if (dayProjects.isEmpty()) {
                System.out.println("No hours recorded for this day");
            } else {
                for (Map.Entry<String, Double> entry : dayProjects.entrySet()) {
                    displayService.printRow(entry.getKey(), formatHoursToHHMM(entry.getValue()));
                }

                double dailyTotal = dayProjects.values().stream().mapToDouble(Double::doubleValue).sum();
                displayService.printTwoColumnHeaders(DAILY_TOTAL, formatHoursToHHMM(dailyTotal), ROW_DELIMITER);
            }

            current = current.plusDays(1);
        }

        // Calculate and print weekly totals per project
        System.out.println("\n" + "=".repeat(80));
        System.out.println("WEEKLY TOTALS PER PROJECT");
        displayService.printTwoColumnHeaders(PROJECT_NAME_HEADER, TOTAL_HOURS, HEADER_DELIMITER,
                ROW_DELIMITER, LARGE_DELIMITER_COUNT);

        Map<String, Double> projectTotals = new HashMap<>();
        for (Map<String, Double> dayDate : result.values()) {
            for (Map.Entry<String, Double> entry : dayDate.entrySet()) {
                String project = entry.getKey();
                double hours = entry.getValue();
                projectTotals.put(project, projectTotals.getOrDefault(project, 0.00) + hours);
            }
        }

        // Sort projects by total hours (descending)
        List<Map.Entry<String, Double>> sortedProjects = new ArrayList<>(projectTotals.entrySet());
        sortedProjects.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        // Print each project's weekly total
        for (Map.Entry<String, Double> entry : sortedProjects) {
            System.out.printf("%-50s | %-10s%n", entry.getKey(), formatHoursToHHMM(entry.getValue()));
        }

        // Print grand total
        double grandTotal = projectTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        displayService.printTwoColumnHeaders(GRAND_TOTAL, formatHoursToHHMM(grandTotal),
                ROW_DELIMITER, HEADER_DELIMITER, LARGE_DELIMITER_COUNT);
    }

    private String formatDateWithDayOfWeek(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return String.format("%s / %s", dateStr, dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()));
    }

    private String formatHoursToHHMM(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return String.format("%02d:%02d hours", h, m);
    }

    public void printLastFiveUniqueProjectNames() {
        List<String> list = db.getLastFiveUniqueProjectNames();
        System.out.println("Last 5 projects from the database:");
        System.out.println("=".repeat(65));
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println();
    }
}
