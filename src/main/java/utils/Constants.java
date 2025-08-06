package utils;

public class Constants {
    public static final String HEADER_DELIMITER = "=";
    public static final String DASH_DELIMITER = "-";
    public static final String DASH_DELIMITER_TOP_ALIGNED = "ˉ";
    public static final String DASH_DELIMITER_BOTTOM_ALIGNED = "ˍ";

    public static final String PROJECT_NAME_HEADER = "PROJECT NAME";
    public static final String HOURS_HEADER = "HOURS";
    public static final String TIME_PERIOD_HEADER = "TIME PERIOD";
    public static final String TOTAL_HOURS = "TOTAL HOURS";
    public static final String TOTAL_DAYS = "TOTAL DAYS";
    public static final String GRAND_TOTAL = "GRAND TOTAL";
    public static final String DAILY_TOTAL = "DAILY TOTAL";
    public static final String DATE = "DATE";
    public static final int HEADER_STRING_MAX_LENGTH = 50;

    public static final int HOURS_IN_WORKING_DAY = 8;
    public static final int DELIMITER_COUNT_65 = 65;
    public static final int DELIMITER_COUNT_70 = 70;
    public static final int DELIMITER_COUNT_80 = 80;
    public static final int NUMBER_OF_PROJECTS = 5;

    public static final String EMOJI_SPARKLES = "✨";

    public static final String FIVE_COLUMN_TABLE_ROW = "%-15s | %9.1fh | %9.1fh | %9.1fh | %9.1f%%%n";
    public static final String FIVE_COLUMN_TABLE_HEADER = "%-15s | %10s | %10s | %10s | %10s%n";

    public static final String DB_URL = "jdbc:sqlite:E:/Programming/TimeTracker/timelog.db";

    // Menu constants
    public static final String INVALID_CHOICE_MESSAGE = "Invalid choice. Please try again.";
    public static final String BACK_MESSAGE = "Returning to previous menu...";
    public static final String GOODBYE_MESSAGE = "Thank you for using the Time Tracker. Goodbye!";
    public static final String SUCCESSFUL_TIME_LOG = "Time logged successfully.";
    public static final String SUCCESSFUL_TIME_ESTIMATE_LOG = "Time estimate logged successfully.";

    public static final double WORKFLOW_MAX_MAX_HOURS_PER_ENTRY = 24;

}
