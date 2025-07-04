package service.menus;

import record.TimeEstimate;
import service.InputService;
import service.LogService;

import java.util.Scanner;

import static utils.Constants.*;

/**
 * Menu service for managing the different time estimates for each
 * project stage of each project.
 */
public class TimeEstimatesMenuService extends MenuBase{
    private final InputService inputService;
    private final LogService logService;

    public TimeEstimatesMenuService(Scanner scanner, InputService inputService, LogService logService) {
        super(scanner);
        this.inputService = inputService;
        this.logService = logService;
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
//                case 3 -> deleteTimeEstimate();
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
}
