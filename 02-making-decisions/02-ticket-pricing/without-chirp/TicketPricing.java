import java.util.Scanner;

public class TicketPricing {

    private static final String[] DAY_TYPES = {"Weekday", "Weekend"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Ticket Pricing");

        int age = readIntInRange(scanner, "Enter age:", 0, 120);
        boolean isStudent = readYesNo(scanner, "Are you a student?");
        int dayIndex = readMenuChoice(scanner, "Day type:", DAY_TYPES);
        boolean isWeekend = dayIndex == 1;

        double base = basePrice(age);
        double afterDiscount = isStudent ? base * 0.8 : base;
        double total = isWeekend ? afterDiscount + 200.0 : afterDiscount;

        System.out.println();
        System.out.println("ℹ " + ageCategory(age)
                + (isStudent ? " · Student" : "")
                + (isWeekend ? " · Weekend" : ""));
        System.out.println("ℹ Base: " + formatRupees(base));
        if (isStudent) System.out.println("ℹ Student discount: -20%");
        if (isWeekend) System.out.println("ℹ Weekend surcharge: +Rs.200.00");
        System.out.println("=".repeat(40));
        System.out.println("✓ Total: " + formatRupees(total));

        scanner.close();
    }

    private static String ageCategory(int age) {
        if (age <= 12) return "Child";
        if (age >= 65) return "Senior";
        return "Adult";
    }

    private static double basePrice(int age) {
        if (age <= 12) return 500.0;
        if (age >= 65) return 700.0;
        return 1000.0;
    }

    private static String formatRupees(double amount) {
        return "Rs." + String.format("%.2f", amount);
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            System.out.println("Please answer y or n.");
        }
    }

    private static int readMenuChoice(Scanner scanner, String title, String[] options) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ") " + options[i]);
            }
            System.out.print("Your choice [1-" + options.length + "]: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.length) return choice - 1;
                System.out.println("Please enter a number between 1 and " + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
