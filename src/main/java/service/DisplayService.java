package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static utils.Constants.*;
import static utils.Constants.LARGE_DELIMITER_COUNT;
import static utils.TimeFormatter.*;

public class DisplayService {


    public void printTwoColumnsTableWithContent(String header1, String header2, Map<String, Double> map) {
        printTwoColumnHeaders(header1, header2);

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.printf("%-50s | %-10s%n", truncateIfLong(entry.getKey()), formatHoursToHHMM(entry.getValue()));
            System.out.println("-".repeat(65));
        }
    }

    public void printTwoColumnHeaders(String header1, String header2, String delimiter) {
        System.out.println(delimiter.repeat(65));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println(delimiter.repeat(65));
    }

    public void printTwoColumnHeaders(String header1, String header2, String startDelimiter, String endDelimiter, int count) {
        System.out.println(startDelimiter.repeat(count));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println(endDelimiter.repeat(count));
    }

    public void printRow(String value1, String value2) {
        System.out.printf("%-50s | %-10s%n", truncateIfLong(value1), value2);
    }

    public void printTableHeader(String header, String value1, String value2) {
        System.out.println("=".repeat(65));
        System.out.printf("%s: %s / %s%n",header, value1, value2);
        System.out.println("=".repeat(65));
    }

    public void printTwoColumnHeaders(String header1, String header2) {
        System.out.println("=".repeat(65));
        System.out.printf("%-50s | %-10s%n", header1, header2);
        System.out.println("=".repeat(65));
    }

    public void printDateAndDayOfWeek(LocalDate date) {
        System.out.println("\n" + formatDateWithDayOfWeek(date.toString()));
    }

    public void printTotalsPerProjectHeaders() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOTALS PER PROJECT");
        printTwoColumnHeaders(PROJECT_NAME_HEADER, TOTAL_HOURS, HEADER_DELIMITER,
                ROW_DELIMITER, LARGE_DELIMITER_COUNT);
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
