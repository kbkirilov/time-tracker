package service;

import record.ProjectAnalysis;
import record.TimeEntry;
import record.TimeEstimate;

import java.security.PublicKey;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static utils.Calculations.calculateRoundedHours;
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

    public void printTableHeader(String header) {
        System.out.println("=".repeat(DELIMITER_COUNT_65));
        System.out.printf("%s: %n",header);
        System.out.println("=".repeat(DELIMITER_COUNT_65));
    }

    public void printSubHeader(String header, int delimiter) {
        System.out.printf("%s%n",header);
        System.out.println("-".repeat(delimiter));
    }

    public void printDateAndDayOfWeek(LocalDate date) {
        System.out.println("\n\n" + formatDateWithDayOfWeek(date.toString()));
    }

    public void printTotalsPerProjectHeaders() {
        System.out.println("\n" + "=".repeat(DELIMITER_COUNT_65));
        System.out.println("TOTALS PER PROJECT");
        printTwoColumnHeaders(PROJECT_NAME_HEADER, TOTAL_HOURS, HEADER_DELIMITER,
                DASH_DELIMITER);
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
                        "Rounded Hours: %f %n" +
                        "Time variance: %d%n",
                entry.projectName(),
                entry.projectStage(),
                entry.date(),
                entry.start(),
                entry.end(),
                roundedHours,
                entry.timeVariance());
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

    public void displayDetailedProjectAnalysis(String projectName, ProjectAnalysis analysis) {
        if (analysis == null) {
            System.out.println("Cannot analyze project '" + projectName + "' - no estimates found.");
            return;
        }

        System.out.println("\n" + "=".repeat(DELIMITER_COUNT_70));
        System.out.println("DETAILED ANALYSIS: " + analysis.projectName().toUpperCase());
        System.out.println("=".repeat(DELIMITER_COUNT_70));

        System.out.printf(FIVE_COLUMN_TABLE_HEADER,
                "STAGE", "ACTUAL", "ESTIMATED", "VARIANCE(H)", "VARIANCE(%)");
        System.out.println("-".repeat(DELIMITER_COUNT_70));

        // CD1 Analysis
        System.out.printf(FIVE_COLUMN_TABLE_ROW,
                "CD1 (Draft 1)",
                analysis.actualCD1Hours(),
                analysis.estimatedCD1Hours(),
                analysis.getCD1Variance(),
                analysis.getCD1PercentageVariance());

        // CD2 Analysis
        System.out.printf(FIVE_COLUMN_TABLE_ROW,
                "CD2 (Draft 2)",
                analysis.actualCD2Hours(),
                analysis.estimatedCD2Hours(),
                analysis.getCD2Variance(),
                analysis.getCD2PercentageVariance());

        // PF Analysis
        System.out.printf(FIVE_COLUMN_TABLE_ROW,
                "PF (Final)",
                analysis.actualPFHours(),
                analysis.estimatedPFHours(),
                analysis.getPFVariance(),
                analysis.getPFPercentageVariance());

        System.out.println("-".repeat(DELIMITER_COUNT_70));

        // Total Analysis
        System.out.printf(FIVE_COLUMN_TABLE_ROW,
                "TOTAL",
                analysis.getTotalActualHours(),
                analysis.getTotalEstimatedHours(),
                analysis.getTotalVariance(),
                analysis.getTotalEstimatedHours() > 0 ?
                        (analysis.getTotalVariance() / analysis.getTotalEstimatedHours()) * 100 : 0);

        System.out.println("=".repeat(DELIMITER_COUNT_70));

        printSubHeader("INSIGHTS: ", DELIMITER_COUNT_70);
        printInsights(analysis);

        printSubHeader("PROGRESS BAR VIEW: ", DELIMITER_COUNT_70);
        printSubHeader("CD1 HOURS PROGRESS BAR: ", DELIMITER_COUNT_70);
        printProgressBar(analysis.actualCD1Hours(), analysis.estimatedCD1Hours());
        printSubHeader("CD2 HOURS PROGRESS BAR: ", DELIMITER_COUNT_70);
        printProgressBar(analysis.actualCD2Hours(), analysis.estimatedCD2Hours());
        printSubHeader("PF HOURS PROGRESS BAR: ", DELIMITER_COUNT_70);
        printProgressBar(analysis.actualPFHours(), analysis.estimatedPFHours());
        printSubHeader("TOTAL HOURS PROGRESS BAR: ", DELIMITER_COUNT_70);
        printProgressBar(analysis.getTotalActualHours(), analysis.getTotalEstimatedHours());
    }

    private void printInsights(ProjectAnalysis analysis) {
        String[] stages = {"CD1", "CD2", "PF", "TOTAL"};
        double[] variances = {analysis.getCD1Variance(), analysis.getCD2Variance(), analysis.getPFVariance(), analysis.getTotalVariance()};

        for (int i = 0; i < stages.length; i++) {
            double currVariance = variances[i];
            String currStage = stages[i];

            if (variances[i] > 0) { // If there are more ours left of the estimate
                getRemainingHoursToEstimate(currVariance, currStage);
            } else if (variances[i] < 0) {
                System.out.println("⚠️  " + currStage + " stage is over estimate  by " + String.format("[%.1f] hours", currVariance) + " hours.");
            } else {
                System.out.println("✅  " + currStage + " is on budget");
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

        printProgressBar(analysis.getTotalActualHours(), analysis.getTotalEstimatedHours());
    }

    public void displayProjectComparisonAnalysis(TreeMap<String, ProjectAnalysis> map) {
        for (Map.Entry<String, ProjectAnalysis> entry : map.entrySet()) {
            System.out.printf("Project name: %s%n", entry.getKey());
            displayProjectComparisonAnalysis(entry.getKey(), entry.getValue());
        }
    }

    public static void printProgressBar(double actualHours, double estimateHours) {
        if (estimateHours <= 0) return;

        double percentage = (actualHours / estimateHours) * 100;
        int filledLength = Math.min((int) (50 * percentage / 100), 50);

        String filled = "█".repeat(filledLength);
        String empty = "░".repeat(50 - filledLength);

        // Show different indicators based on progress
        if (percentage > 100) {
            System.out.printf("0%% [%s%s] 100%% - OVER BUDGET!\n", filled, empty);
            System.out.printf("Progress: %.1f/%.1f hours (%.1f%% - %.1f hours over)\n\n",
                    actualHours, estimateHours, percentage, actualHours - estimateHours);
        } else {
            System.out.printf("0%% [%s%s] 100%%\n", filled, empty);
            System.out.printf("Progress: %.1f/%.1f hours | (%.1f%%) used | [%.1fh] left\n\n",
                    actualHours, estimateHours, percentage, estimateHours-actualHours);
        }
    }

    private void getRemainingHoursToEstimate(double hoursVariance, String projectStage) {
        System.out.println("✨️  There are " + String.format("[%.1f]", hoursVariance) + " more hours left till the " +
                "(" + projectStage + ")" + " estimate hours are reached.");
    }

    public void printProjectCodeHoursForWorkflowMax(Map<String, Double> projectCodeHours) {
        for (Map.Entry<String, Double> projectCodeHour : projectCodeHours.entrySet()) {
            String code = projectCodeHour.getKey();
            Double hours = projectCodeHour.getValue();
            while (hours > WORKFLOW_MAX_MAX_HOURS_PER_ENTRY) {
                printRow(code, formatHoursToHHMM(WORKFLOW_MAX_MAX_HOURS_PER_ENTRY));
                hours -= WORKFLOW_MAX_MAX_HOURS_PER_ENTRY;
            }
            printRow(code, formatHoursToHHMM(hours));
        }
    }

    public void printProjectCodeHours(Map<String, Double> projectCodeHours) {
        for (Map.Entry<String, Double> projectCodeHour : projectCodeHours.entrySet()) {
            String code = projectCodeHour.getKey();
            Double hours = projectCodeHour.getValue();
            printRow(code, formatHoursToHHMM(hours));
        }
    }

    public void printCurrentEarnings(double earningsGBP) {
        printTwoColumnHeaders(CURRENT_PERIOD_EARNINGS, String.format("£ %.2f", earningsGBP));
    }
}
