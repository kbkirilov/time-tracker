import service.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseService dbService = new DatabaseService();
        DisplayService displayService = new DisplayService();
        ReportService reportService = new ReportService(dbService, displayService);
        Scanner scanner = new Scanner(System.in);
        LogService logService = new LogService(scanner, dbService);
        MenuService menuService = new MenuService(logService, reportService);

        menuService.run();
    }
}