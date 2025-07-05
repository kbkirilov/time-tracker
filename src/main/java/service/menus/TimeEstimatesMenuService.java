package service.menus;

import record.TimeEntry;
import record.TimeEstimate;
import service.DisplayService;
import service.InputService;
import service.LogService;
import service.ReportService;

import java.util.Scanner;

import static utils.Constants.*;
import static utils.ConfirmationDialogUtil.*;

/**
 * Menu service for managing the different time estimates for each
 * project stage of each project.
 */
public class TimeEstimatesMenuService extends MenuBase {
    private final InputService inputService;
    private final LogService logService;
    private final ReportService reportService;
    private final DisplayService displayService;

    public TimeEstimatesMenuService(Scanner scanner, InputService inputService, LogService logService, ReportService reportService, DisplayService displayService) {
        super(scanner);
        this.inputService = inputService;
        this.logService = logService;
        this.reportService = reportService;
        this.displayService = displayService;
    }

    @Override
    public void show() {
        boolean isRunning = true;

        while (isRunning) {
            display("TIME ESTIMATES",
                    "Log new time estimate",
                    "Edit time estimate",
                    "Delete time estimate",
                    "Back to main menu");

            int choice = getChoice();

            switch (choice) {
                case 1 -> insertTimeEstimate();
                case 2 -> editTimeEstimate();
                case 3 -> deleteTimeEstimate();
                case 4 -> {
                    isRunning = false;
                    back();
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    private void editTimeEstimate() {
        reportService.printLastTenTimeEstimateEntriesProjectNamesWithIds();
        displayMenuHeader("EDIT TIME ESTIMATE");
        System.out.print("Enter the ID of the entry to edit: ");

        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);

            TimeEstimate currentEntry = reportService.getTimeEstimateById(id);

            if (currentEntry == null) {
                System.out.println("Entry with ID " + id + " not found.");
                back();
            }

            System.out.println("\nCurrent entry details:");
            displayService.displayTimeEstimateDetails(currentEntry);

            System.out.println("\nEnter new values (press Enter to keep current value):");

            TimeEstimate updatedEntry = inputService.getTimeEstimateEditInput(currentEntry);

            if (showEditConfirmation(currentEntry, updatedEntry, scanner)) {
                logService.logUpdatedTimeEstimate(id, updatedEntry);
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

    private void insertTimeEstimate() {
        displayMenuHeader("LOG NEW TIME ESTIMATE");
        TimeEstimate entry = inputService.getTimeEstimateInput();
        logService.logInsertTimeEstimate(entry);
        System.out.println(SUCCESSFUL_TIME_ESTIMATE_LOG);
    }

    private void deleteTimeEstimate() {
        reportService.printLastTenTimeEstimateEntriesProjectNamesWithIds();
        displayMenuHeader("DELETE TIME ESTIMATE");
        System.out.print("Enter the ID of the time estimate entry to delete: ");

        String input = scanner.nextLine();

        try {
            int id = Integer.parseInt(input);

            reportService.printTimeEstimateEntryDetails(id);

            if (showDeletionWarning(scanner)) {
                logService.logDeleteTimeEstimateEntry(id);
                System.out.println("Time estimate entry deleted successfully.");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a numeric ID.");
        } catch (Exception e) {
            System.out.println("Error deleting entry: " + e.getMessage());
        }
    }
}
