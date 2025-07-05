package service;

import record.TimeEntry;
import record.TimeEstimate;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        displayService.printThreeColumnTableWithContent(PROJECT_NAME_HEADER, TOTAL_HOURS, TOTAL_DAYS, result);
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

    /**
     *
     * @param start The start date of the report
     * @param end The end date of the report
     * @implNote To get a report for a single date, once can pass the same date as the start and end
     */
    public void getTimePeriodProjectBreakdown(LocalDate start, LocalDate end) {
        Map<LocalDate, Map<String, Double>> result = db.getWeeklyProjectReport(start, end);

        displayService.printTableHeader(TIME_PERIOD_HEADER, start.toString(), end.toString());

        // For each day of the week
        LocalDate current = start;
        while (!current.isAfter(end)) {
            Map<String, Double> dayProjects = result.getOrDefault(current, Collections.emptyMap());

            displayService.printDateAndDayOfWeek(current);
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

        Map<String, Double> projectTotals = new HashMap<>();
        for (Map<String, Double> dayDate : result.values()) {
            for (Map.Entry<String, Double> entry : dayDate.entrySet()) {
                String projectName = entry.getKey();
                double hours = entry.getValue();
                projectTotals.put(projectName, projectTotals.getOrDefault(projectName, 0.00) + hours);
            }
        }

        // If the map contains information for more than 1 day
        if (!start.isEqual(end)) {
            displayService.printTotalsPerProjectHeaders();

            List<Map.Entry<String, Double>> sortedProjects = new ArrayList<>(projectTotals.entrySet());
            sortedProjects.sort(Map.Entry.<String, Double>comparingByValue().reversed());

            for (Map.Entry<String, Double> entry : sortedProjects) {
                displayService.printRow(entry.getKey(), formatHoursToHHMM(entry.getValue()));
            }

            double grandTotal = projectTotals.values().stream().mapToDouble(Double::doubleValue).sum();
            displayService.printTwoColumnHeaders(GRAND_TOTAL, formatHoursToHHMM(grandTotal),
                    ROW_DELIMITER, HEADER_DELIMITER);
        }
    }

    public void printLastFiveUniqueProjectNames() {
        List<String> list = db.getLastFiveUniqueProjectNames();
        displayService.printLastFiveUniqueProjectNames(list);
    }

    public void printLastTenTimeEntriesProjectNamesWithIds() {
        TreeMap<Integer, String> map = db.getLastTenTimeEntriesProjectNamesWithIds();
        displayService.printValuesFromSortedMap(map);
    }

    public void printTimeEntryDetails(int id) {
        Map<String, Double> map = db.getTimeEntryDetailsById(id);
        displayService.printRow(map);
    }

    public void printTimeEstimateEntryDetails(int id) {
        Map<String, Double> map = db.getTimeEstimatesEntryDetailsById(id);
        displayService.printRow(map);
    }

    public TimeEntry getTimeEntryById(int id) {
        return db.getTimeEntryById(id);
    }

    public TimeEstimate getTimeEstimateById(int id) {
        return db.getTimeEstimateById(id);
    }

    private String formatHoursToHHMM(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return String.format("%02d:%02d hours", h, m);
    }


    public void printLastTenTimeEstimateEntriesProjectNamesWithIds() {
        TreeMap<Integer, String> map = db.getLastTenTimeEstimatesProjectNamesWithId();
        displayService.printValuesFromSortedMap(map);
    }
}
