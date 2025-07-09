package service.menus;

import record.TimeEntry;
import service.DisplayService;
import service.InputService;
import service.LogService;
import service.ReportService;

import java.util.Scanner;

import static utils.ConfirmationDialogUtil.*;
import static utils.Constants.*;

/**
 * Menu service for time tracking operations including
 * logging time, deleting entries, and editing entries.
 */
public class TimeLogMenuService extends MenuBase {
    private final LogService logService;
    private final ReportService reportService;
    private final InputService inputService;
    private final DisplayService displayService;

    public TimeLogMenuService(LogService logService, ReportService reportService, InputService inputService, Scanner scanner, DisplayService displayService) {
        super(scanner);
        this.logService = logService;
        this.reportService = reportService;
        this.inputService = inputService;
        this.displayService = displayService;
    }

    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("TIME TRACKING",
                    "Log new time entry",
                    "Edit time entry",
                    "Delete time entry",
                    "Back to main menu");

            int choice = getChoice();

            switch (choice) {
                case 1 -> insertTimeEntry();
                case 2 -> editTimeEntry();
                case 3 -> deleteTimeEntry();
                case 4 -> {
                    isRunning = false;
                    back();
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    /**
     * Handles the process of inserting a new time entry.
     */
    private void insertTimeEntry() {
        reportService.printLastXProjectNames(FIVE);
        displayMenuHeader("LOG NEW TIME ENTRY");
        TimeEntry entry = inputService.getTimeEntryInput();
        logService.logInsertTimeEntry(entry);
        System.out.println(SUCCESSFUL_TIME_LOG);
    }

    /**
     * Handles the process of deleting a time entry.
     */
    private void deleteTimeEntry() {
        reportService.printLastTenTimeEntriesProjectNamesWithIds();
        displayMenuHeader("DELETE TIME ENTRY");
        System.out.print("Enter the ID of the entry to delete: ");

        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);

            reportService.printTimeEntryDetails(id);

            if (showDeletionWarning(scanner)) {
                logService.logDeleteTimeEntry(id);
                System.out.println("Entry deleted successfully.");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a numeric ID.");
        } catch (Exception e) {
            System.out.println("Error deleting entry: " + e.getMessage());
        }
    }

    private void editTimeEntry() {
        reportService.printLastTenTimeEntriesProjectNamesWithIds();
        displayMenuHeader("EDIT TIME ENTRY");
        System.out.print("Enter the ID of the entry to edit: ");

        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);

            TimeEntry currentEntry = reportService.getTimeEntryById(id);

            if (currentEntry == null) {
                System.out.println("Entry with ID " + id + " not found.");
                back();
            }

            System.out.println("\nCurrent entry details:");
            displayService.displayTimeEntryDetails(currentEntry);

            System.out.println("\nEnter new values (press Enter to keep current value):");

            TimeEntry updatedEntry = inputService.getTimeEntryEditInput(currentEntry);

            if (showTimeEntryEditConfirmation(currentEntry, updatedEntry, scanner)) {
                logService.logUpdateTimeEntry(id, updatedEntry);
                System.out.println("Entry updated successfully.");
            } else {
                System.out.println("Edit cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a numeric ID.");
        } catch (Exception e) {
            System.out.println("Error editing entry: " + e.getMessage());
        }
    }
}
