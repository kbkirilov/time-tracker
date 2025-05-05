package service.menus;

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

    private final TimeLogMenuService timeLogMenuService;
    private final ReportMenuService reportMenuService;

    public MainMenuService(LogService logService, ReportService reportService, InputService inputService,
                           TimeLogMenuService timeLogMenuService, ReportMenuService reportMenuService, Scanner scanner) {
        super(scanner);
        this.logService = logService;
        this.reportService = reportService;
        this.inputService = inputService;

        this.timeLogMenuService = new TimeLogMenuService(logService, reportService, inputService, scanner);
        this.reportMenuService = new ReportMenuService(reportService, scanner);
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

        while (isRunning) {
            display("MAIN MENU",
                    "Time tracking",
                    "Reports",
                    "Exit");

            int choice = getChoice();

            switch (choice) {
                case 1 -> timeLogMenuService.show();
                case 2 -> reportMenuService.show();
                case 3 -> {
                    System.out.println(GOODBYE_MESSAGE);
                    isRunning = false;
                }
                default -> System.out.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }
}