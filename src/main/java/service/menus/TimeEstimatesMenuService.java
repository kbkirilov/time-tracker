package service.menus;

import record.TimeEstimate;
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

    public TimeEstimatesMenuService(Scanner scanner, InputService inputService, LogService logService, ReportService reportService) {
        super(scanner);
        this.inputService = inputService;
        this.logService = logService;
        this.reportService = reportService;
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
//                case 2 -> editTimeEstimate();
                case 3 -> deleteTimeEstimate();
                case 4 -> {
                    isRunning = false;
                    back();
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
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
