package service.menus;

import java.util.Scanner;
import static utils.Constants.BACK_MESSAGE;

public abstract class MenuBase {
    protected final Scanner scanner;

    protected MenuBase(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Display the menu and handles user interactions
     */
    public abstract void show();

    /**
     * @param title   The title of the menu
     * @param options The menu options
     */
    protected void display(String title, String... options) {
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    protected void displayMenuHeader(String header) {
        System.out.printf("\n--- %s ---\n", header);
    }

    /**
     * Gets the user's choice as an integer.
     * @return The user's choice as an integer, or -1 if the input is invalid
     */
    protected int getChoice() {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected void back() {
        System.out.println(BACK_MESSAGE);
    }

}
