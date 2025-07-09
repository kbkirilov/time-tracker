package utils;

import record.TimeEntry;
import record.TimeEstimate;

import java.util.Scanner;

/**
 * Utility class for handling user confirmation dialogs.
 * Focuses purely on user interaction without business logic dependencies.
 */
public class ConfirmationDialogUtil {

    /**
     * Shows a deletion warning dialog with pre-displayed details.
     * Call this AFTER displaying the entry details.
     *
     * @param scanner Scanner for user input
     * @return true if user confirms deletion, false otherwise
     */
    public static boolean showDeletionWarning(Scanner scanner) {
        System.out.println("\n⚠️  WARNING: This action cannot be undone!");
        System.out.print("Are you sure you want to delete this time entry? (y/N): ");

        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }

    /**
     * Shows a generic confirmation dialog.
     *
     * @param message The confirmation message to display
     * @param scanner Scanner for user input
     * @return true if user confirms, false otherwise
     */
    public static boolean showConfirmation(String message, Scanner scanner) {
        System.out.print(message + " (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }

    /**
     * Shows a deletion warning with custom warning message.
     *
     * @param warningMessage Custom warning message
     * @param confirmMessage Confirmation prompt message
     * @param scanner Scanner for user input
     * @return true if user confirms, false otherwise
     */
    public static boolean showCustomDeletionWarning(String warningMessage, String confirmMessage, Scanner scanner) {
        System.out.println("\n⚠️  WARNING: " + warningMessage);
        return showConfirmation(confirmMessage, scanner);
    }

    public static boolean showTimeEntryEditConfirmation(TimeEntry current, TimeEntry updated, Scanner scanner) {
        System.out.println("\n--- CONFIRM CHANGES ---");
        System.out.println("Original: " + current.toString());
        System.out.println("Updated: " + updated.toString());
        System.out.print("\nSave these changes? (y/N): ");

        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }

    public static boolean showTimeEstimateEditConfirmation(TimeEstimate current, TimeEstimate updated, Scanner scanner) {
        System.out.println("\n--- CONFIRM CHANGES ---");
        System.out.println("Original: " + current.toString());
        System.out.println("Updated: " + updated.toString());
        System.out.print("\nSave these changes? (y/N): ");

        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }
}
