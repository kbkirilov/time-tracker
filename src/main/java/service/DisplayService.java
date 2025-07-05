package service;

import record.TimeEntry;
import record.TimeEstimate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static utils.Constants.*;
import static utils.Constants.DELIMITER_COUNT_80;
import static utils.TimeFormatter.*;

public class DisplayService {


    public void printTwoColumnsTableWithContent(String header1, String header2, Map<String, Double> map) {
        printTwoColumnHeaders(header1, header2);

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.printf("%-50s | %-10s%n", truncateIfLong(entry.getKey()), formatHoursToHHMM(entry.getValue()));
            System.out.println("-".repeat(DELIMITER_COUNT_65));
        }
    }

    public void printThreeColumnTableWithContent(String header1, String header2, String header3, Map<String, Double> map) {
        int size = map.size();
        int counter = 0;

        printThreeColumnHeaders(header1, header2, header3);

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.printf("%-50s | %-10s | %-10s%n",
                    truncateIfLong(entry.getKey()), formatHoursToHHMM(entry.getValue()), formatHoursToDays(entry.getValue()));
            counter++;
            if (size > 1 && counter < size) {
                System.out.println("-".repeat(DELIMITER_COUNT_80));
            }
        }
    }

    /**
     *
     * @param header1 First column header
     * @param header2 Second column header
     * @param delimiter The delimiter used
     */
    public void printTwoColumnHeaders(String header1, String header2, String delimiter) {
        System.out.println(delimiter.repeat(DELIMITER_COUNT_65));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println(delimiter.repeat(DELIMITER_COUNT_65));
    }

    public void printThreeColumnHeaders(String header1, String header2, String header3) {
        System.out.println("=".repeat(DELIMITER_COUNT_80));
        System.out.printf("%-50s | %-10s | %-10s%n", header1, header2, header3);
        System.out.println("=".repeat(DELIMITER_COUNT_80));
    }

    /**
     *
     * @param header1 First column header
     * @param header2 Second column header
     */
    public void printTwoColumnHeaders(String header1, String header2) {
        System.out.println("=".repeat(DELIMITER_COUNT_65));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println("=".repeat(DELIMITER_COUNT_65));
    }

    /**
     *
     * @param header1 First column header
     * @param header2 Second column header
     * @param startDelimiter The start delimiter symbol
     * @param endDelimiter The end delimiter symbol
     */
    public void printTwoColumnHeaders(String header1, String header2, String startDelimiter, String endDelimiter) {
        System.out.println(startDelimiter.repeat(DELIMITER_COUNT_65));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println(endDelimiter.repeat(DELIMITER_COUNT_65));
    }

    /**
     *
     * @param value1 The value for the first column
     * @param value2 The value for the second column
     */
    public void printRow(String value1, String value2) {
        System.out.printf("%-50s | %-10s%n", truncateIfLong(value1), value2);
    }

    public void printRow(String value1, String value2, int distance1, int distance2) {
        System.out.printf("%-" + distance1 + "s | %-" + distance2 + "s%n", truncateIfLong(value1), value2);
    }

    public void printRow(Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            printRow(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    public void printTableHeader(String header, String value1, String value2) {
        System.out.println("=".repeat(DELIMITER_COUNT_65));
        System.out.printf("%s: %s / %s%n",header, value1, value2);
        System.out.println("=".repeat(DELIMITER_COUNT_65));
    }

    public void printDateAndDayOfWeek(LocalDate date) {
        System.out.println("\n" + formatDateWithDayOfWeek(date.toString()));
    }

    public void printTotalsPerProjectHeaders() {
        System.out.println("\n" + "=".repeat(DELIMITER_COUNT_80));
        System.out.println("TOTALS PER PROJECT");
        printTwoColumnHeaders(PROJECT_NAME_HEADER, TOTAL_HOURS, HEADER_DELIMITER,
                ROW_DELIMITER);
    }

    public void printLastFiveUniqueProjectNames(List<String> list) {
        System.out.println("Last 5 projects from the database:");
        System.out.println("=".repeat(65));
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println();
    }

    public void printValuesFromSortedMap(TreeMap<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            printRow(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), 2, 5);
        }
    }

    public void displayTimeEntryDetails(TimeEntry entry) {
        System.out.printf("Project name: %s, Entry date: %s, Start time: %s, End time: %s%n",
                entry.projectName(),
                entry.date(),
                entry.start(),
                entry.end());
    }

    public void displayTimeEntryDetails(TimeEntry entry, double roundedHours) {
        System.out.printf("Project name: %s, Entry date: %s, Start time: %s, End time: %s, Rounded Hours: %f %n",
                entry.projectName(),
                entry.date(),
                entry.start(),
                entry.end(),
                roundedHours);
    }

    public void displayTimeEstimateDetails(TimeEstimate entry, double totalHours) {
        System.out.printf("%nProject name: %s%n" +
                "CD1 time estimate: %.2f%n" +
                "CD2 time estimate: %.2f%n" +
                "PF time estimate: %.2f%n" +
                "Total estimate hours: %.2f%n",
                entry.projectName(),
                entry.cd1EstimateHours(),
                entry.cd2EstimateHours(),
                entry.pfEstimateHours(),
                totalHours);
    }

    private String truncateIfLong(String str) {
        if (str.length() <= HEADER_STRING_MAX_LENGTH) {
            return str;
        }

        return str.substring(0, HEADER_STRING_MAX_LENGTH - 3) + "...";
    }

    private String formatDateWithDayOfWeek(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return String.format("%s / %s", dateStr, dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()));
    }
}
