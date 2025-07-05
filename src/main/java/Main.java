import service.*;
import service.menus.MainMenuService;
import service.menus.TimeEstimatesMenuService;
import service.menus.TimeLogMenuService;
import service.menus.reports.ReportMenuService;

import javax.xml.crypto.Data;
import java.util.Scanner;

import static utils.Debug.debugDatabase;
import static utils.Debug.testSimpleInsert;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        DatabaseService dbService = new DatabaseService();
        DisplayService displayService = new DisplayService();
        ReportService reportService = new ReportService(dbService, displayService);
        Scanner scanner = new Scanner(System.in);
        MainMenuService mainMenuService = getMainMenuService(scanner, dbService, reportService);

        mainMenuService.run();
    }

    private static MainMenuService getMainMenuService(Scanner scanner, DatabaseService dbService, ReportService reportService) {
        DatabaseService databaseService = new DatabaseService();
        InputService inputService = new InputService(scanner, databaseService);
        DisplayService displayService = new DisplayService();
        LogService logService = new LogService(dbService, displayService);
        TimeLogMenuService timeLogMenuService = new TimeLogMenuService(logService, reportService, inputService, scanner, displayService);
        TimeEstimatesMenuService timeEstimatesMenuService = new TimeEstimatesMenuService(scanner, inputService, logService, reportService, displayService);
        ReportMenuService reportMenuService = new ReportMenuService(reportService, scanner);

        MainMenuService mainMenuService = new MainMenuService(logService, reportService, inputService, timeLogMenuService,
                reportMenuService, displayService, scanner, timeEstimatesMenuService);
        return mainMenuService;
    }
}