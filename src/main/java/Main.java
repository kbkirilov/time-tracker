import service.*;
import service.menus.MainMenuService;
import service.menus.TimeLogMenuService;
import service.menus.reports.ReportMenuService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseService dbService = new DatabaseService();
        DisplayService displayService = new DisplayService();
        ReportService reportService = new ReportService(dbService, displayService);
        Scanner scanner = new Scanner(System.in);
        MainMenuService mainMenuService = getMainMenuService(scanner, dbService, reportService);

        mainMenuService.run();
    }

    private static MainMenuService getMainMenuService(Scanner scanner, DatabaseService dbService, ReportService reportService) {
        InputService inputService = new InputService(scanner);
        DisplayService displayService = new DisplayService();
        LogService logService = new LogService(dbService, displayService);
        TimeLogMenuService timeLogMenuService = new TimeLogMenuService(logService, reportService, inputService, scanner);
        ReportMenuService reportMenuService = new ReportMenuService(reportService, scanner);

        MainMenuService mainMenuService = new MainMenuService(logService, reportService, inputService, timeLogMenuService,
                reportMenuService, scanner);
        return mainMenuService;
    }
}