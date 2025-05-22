package service;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static utils.Constants.HEADER_STRING_MAX_LENGTH;
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
        System.out.printf("%-50s | %-10s%n", value1, value2);
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

    private String truncateIfLong(String str) {
        if (str.length() <= HEADER_STRING_MAX_LENGTH) {
            return str;
        }

        return str.substring(0, HEADER_STRING_MAX_LENGTH - 3) + "...";
    }
}
