package service;

import record.ProjectAnalysis;
import record.TimeEntry;
import record.TimeEstimate;

import java.time.DayOfWeek;
import java.time.Duration;
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
        double roundedHours = calculateRoundedHours(entry);

        System.out.printf("Project name: %s%n " +
                        "Project stage: %s%n" +
                        "Entry date: %s%n " +
                        "Start time: %s%n " +
                        "End time: %s%n " +
                        "Rounded Hours: %f %n",
                entry.projectName(),
                entry.projectStage(),
                entry.date(),
                entry.start(),
                entry.end(),
                roundedHours);
    }

    public void displayTimeEstimateDetails(TimeEstimate entry) {
        double totalHours = entry.cd1EstimateHours() + entry.cd2EstimateHours() + entry.pfEstimateHours();

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

    private double calculateRoundedHours(TimeEntry entry) {
        double hours = Duration.between(entry.start(), entry.end()).toMinutes() / 60.0;
        return (double) Math.round(hours * 1000.0) / 1000;
    }

    public void displayProjectAnalysis(String projectName, ProjectAnalysis analysis) {
        if (analysis == null) {
            System.out.println("Cannot analyze project '" + projectName + "' - no estimates found.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROJECT ANALYSIS: " + analysis.projectName().toUpperCase());
        System.out.println("=".repeat(60));

        System.out.printf("%-15s | %-10s | %-10s | %-10s | %-10s%n",
                "STAGE", "ACTUAL", "ESTIMATED", "VARIANCE", "% VARIANCE");
        System.out.println("-".repeat(60));

        // CD1 Analysis
        System.out.printf("%-15s | %8.1fh | %8.1fh | %8.1fh | %8.1f%%%n",
                "CD1 (Draft 1)",
                analysis.actualCD1Hours(),
                analysis.estimatedCD1Hours(),
                analysis.getCD1Variance(),
                analysis.getCD1PercentageVariance());

        // CD2 Analysis
        System.out.printf("%-15s | %8.1fh | %8.1fh | %8.1fh | %8.1f%%%n",
                "CD2 (Draft 2)",
                analysis.actualCD2Hours(),
                analysis.estimatedCD2Hours(),
                analysis.getCD2Variance(),
                analysis.getCD2PercentageVariance());

        // PF Analysis
        System.out.printf("%-15s | %8.1fh | %8.1fh | %8.1fh | %8.1f%%%n",
                "PF (Final)",
                analysis.actualPFHours(),
                analysis.estimatedPFHours(),
                analysis.getPFVariance(),
                analysis.getPFPercentageVariance());

        System.out.println("-".repeat(60));

        // Total Analysis
        System.out.printf("%-15s | %8.1fh | %8.1fh | %8.1fh | %8.1f%%%n",
                "TOTAL",
                analysis.getTotalActualHours(),
                analysis.getTotalEstimatedHours(),
                analysis.getTotalVariance(),
                analysis.getTotalEstimatedHours() > 0 ?
                        (analysis.getTotalVariance() / analysis.getTotalEstimatedHours()) * 100 : 0);

        System.out.println("=".repeat(60));

        // Summary insights
        if (analysis.getTotalVariance() > 0) {
            System.out.println("⚠️  PROJECT OVER BUDGET by " + String.format("%.1f", analysis.getTotalVariance()) + " hours");
        } else if (analysis.getTotalVariance() < 0) {
            System.out.println("✅ PROJECT UNDER BUDGET by " + String.format("%.1f", Math.abs(analysis.getTotalVariance())) + " hours");
        } else {
            System.out.println("✅ PROJECT ON BUDGET");
        }

        // Stage-specific insights
        String[] stages = {"CD1", "CD2", "PF"};
        double[] variances = {analysis.getCD1Variance(), analysis.getCD2Variance(), analysis.getPFVariance()};

        for (int i = 0; i < stages.length; i++) {
            if (variances[i] > 2) { // More than 2 hours over
                System.out.println("⚠️  " + stages[i] + " stage significantly over estimate");
            }
        }
    }

    public void displayProjectComparisonAnalysis(String projectName, ProjectAnalysis analysis) {
        if (analysis == null) {
            System.out.println("Cannot analyze project '" + projectName + "' - no estimates found.");
            return;
        }

        System.out.printf("Total hours ENTRIES so far: %.2f%n", analysis.getTotalActualHours());
        System.out.printf("Total hours ESTIMATE: %.2f%n", analysis.getTotalEstimatedHours());

        printLoadingBar(analysis.getTotalActualHours(), analysis.getTotalEstimatedHours());
    }

    public static void printLoadingBar(double actualHours, double totalHours) {
        if (totalHours <= 0) return;

        double percentage = (actualHours / totalHours) * 100;
        int filledLength = Math.min((int) (50 * percentage / 100), 50);

        String filled = "█".repeat(filledLength);
        String empty = "░".repeat(50 - filledLength);

        // Show different indicators based on progress
        if (percentage > 100) {
            System.out.printf("0%% [%s%s] 100%% - OVER BUDGET!\n", filled, empty);
            System.out.printf("Progress: %.1f/%.1f hours (%.1f%% - %.1f hours over)\n",
                    actualHours, totalHours, percentage, actualHours - totalHours);
        } else {
            System.out.printf("0%% [%s%s] 100%%\n", filled, empty);
            System.out.printf("Progress: %.1f/%.1f hours (%.1f%%)\n", actualHours, totalHours, percentage);
        }
    }
}
