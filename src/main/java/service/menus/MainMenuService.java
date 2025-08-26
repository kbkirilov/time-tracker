package service.menus;

import service.DisplayService;
import service.InputService;
import service.LogService;
import service.ReportService;
import service.menus.reports.ReportMenuService;

import java.util.Scanner;

import static utils.Constants.GOODBYE_MESSAGE;
import static utils.Constants.INVALID_CHOICE_MESSAGE;

public class MainMenuService extends MenuBase {

    private final LogService logService;
    private final ReportService reportService;
    private final InputService inputService;
    private final DisplayService displayService;

    private final TimeLogMenuService timeLogMenuService;
    private final TimeEstimatesMenuService timeEstimatesMenuService;
    private final ReportMenuService reportMenuService;

    public MainMenuService(LogService logService, ReportService reportService, InputService inputService,
                           TimeLogMenuService timeLogMenuService, ReportMenuService reportMenuService, DisplayService displayService, Scanner scanner, TimeEstimatesMenuService timeEstimatesMenuService) {
        super(scanner);
        this.logService = logService;
        this.reportService = reportService;
        this.inputService = inputService;
        this.displayService = displayService;
        this.timeEstimatesMenuService = timeEstimatesMenuService;

        this.timeLogMenuService = new TimeLogMenuService(logService, reportService, inputService, scanner, displayService);
        this.reportMenuService = new ReportMenuService(reportService, scanner, displayService);
    }

    /**
     * Entry point to start the application menu.
     */
    public void run() {
        show();
    }

    /**
     * Displays the main menu and handles user input.
     */
    @Override
    public void show() {
        boolean isRunning = true;

        // TODO Display a message at the start of the program here

        while (isRunning) {
            display("MAIN MENU",
                    "Time tracking",
                    "Time estimates",
                    "Reports",
                    "Exit");

            int choice = getChoice();

            switch (choice) {
                case 1 -> timeLogMenuService.show();
                case 2 -> timeEstimatesMenuService.show();
                case 3 -> reportMenuService.show();
                case 4 -> {
                    System.out.println(GOODBYE_MESSAGE);
                    isRunning = false;
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }
}