package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class ReportService {
    private static final String DB_URL = "jdbc:sqlite:timelog.db";

    private final DatabaseService db;

    public ReportService(DatabaseService db) {
        this.db = db;
    }

    public void getWorkedHoursPerDay() {
        printTwoColumnsTableHeaders("DATE", "TOTAL_HOURS");

        Map<String, Double> result = db.getWorkedHoursPerDay();

        for (Map.Entry<String, Double> entry : result.entrySet()) {
            System.out.printf("%-50s | %-10s%n", formatDateWithDayOfWeek(entry.getKey()), formatHoursToHHMM(entry.getValue()));
            System.out.println("-".repeat(65));
        }
    }

    public void getWorkedHoursPerProject(String projectName) {
        Map<String, Double> result = db.getWorkedHoursPerProject(projectName);

        printTwoColumnsTableHeaders("PROJECT NAME", "TOTAL HOURS");

        for (Map.Entry<String, Double> entry : result.entrySet()) {
            System.out.printf("%-50s | %-10s%n", entry.getKey(), formatHoursToHHMM(entry.getValue()));
            System.out.println("-".repeat(65));
        }
    }

    public void getWorkedHoursForParticularDay(LocalDate date) {
        double hours = db.getWorkedHoursForParticularDay(date);
        printTwoColumnsTableHeaders(date.toString(), formatHoursToHHMM(hours));
    }


    public void reportWeeklyProjectHours() {
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = LocalDate.now().with(DayOfWeek.SUNDAY);

        Map<String, Double> projectHours = db.getWeeklyWorkedHoursPerProject(start, end);

        System.out.println("=".repeat(65));
        System.out.printf("TIME PERIOD OF THE REPORT: %s / %s%n", start, end);
        printTwoColumnsTableHeaders("PROJECT NAME", "TOTAL HOURS");

        for (Map.Entry<String, Double> entry : projectHours.entrySet()) {
            System.out.printf("%-50s | %-10s%n", entry.getKey(), formatHoursToHHMM(entry.getValue()));
            System.out.println("-".repeat(65));
        }
    }

    public void getTimePeriodProjectBreakdown(LocalDate start, LocalDate end) {

        Map<LocalDate, Map<String, Double>> weeklyData =  db.getWeeklyProjectReport(start, end);

        System.out.println("=".repeat(65));
        System.out.printf("TIME PERIOD OF THE REPORT: %s / %s%n", start, end);
        System.out.println("=".repeat(65));

        // For each day of the week
        LocalDate current = start;
        while (!current.isAfter(end)) {
            System.out.println("\n" + formatDateWithDayOfWeek(current.toString()));
            System.out.println("-".repeat(80));
            System.out.printf("%-50s | %-10s%n", "PROJECT NAME", "HOURS");
            System.out.println("-".repeat(80));

            // Get projects for this day
            Map<String, Double> dayProjects = weeklyData.getOrDefault(current, Collections.emptyMap());

            if (dayProjects.isEmpty()) {
                System.out.println("No hours recorded for this day");
            } else {
                // Print each project's hours for this day
                for (Map.Entry<String, Double> entry : dayProjects.entrySet()) {
                    System.out.printf("%-50s | %-10s%n", entry.getKey(), formatHoursToHHMM(entry.getValue()));
                }

                double dailyTotal = dayProjects.values().stream().mapToDouble(Double::doubleValue).sum();
                System.out.println("-".repeat(80));
                System.out.printf("%-50s | %-10s%n", "DAILY TOTAL", formatHoursToHHMM(dailyTotal));
            }

            current = current.plusDays(1);
        }

        // Calculate and print weekly totals per project
        System.out.println("\n" + "=".repeat(80));
        System.out.println("WEEKLY TOTALS PER PROJECT");
        System.out.println("=".repeat(80));
        System.out.printf("%-50s | %-10s%n", "PROJECT NAME", "TOTAL HOURS");
        System.out.println("-".repeat(80));

        Map<String, Double> projectTotals = new HashMap<>();
        for(Map<String, Double> dayDate : weeklyData.values()) {
            for(Map.Entry<String, Double> entry : dayDate.entrySet()) {
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
        System.out.println("-".repeat(80));
        System.out.printf("%-50s | %-10s%n", "GRAND TOTAL", formatHoursToHHMM(grandTotal));
        System.out.println("=".repeat(80));
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

    private void printTwoColumnsTableHeaders(String column1Header, String column2Header) {
        System.out.println("=".repeat(65));
        System.out.printf("%-50s | %-10s%n", column1Header, column2Header);
        System.out.println("=".repeat(65));
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
