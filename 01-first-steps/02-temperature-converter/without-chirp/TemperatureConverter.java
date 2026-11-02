import java.util.Scanner;

public class TemperatureConverter {

    private static final String[] UNITS = {"Celsius", "Fahrenheit", "Kelvin"};
    private static final String[] SYMBOLS = {"°C", "°F", "K"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Temperature Converter");

        int sourceIndex = readMenuChoice(scanner, "Pick source unit:", UNITS);
        double bound = absoluteZeroFor(sourceIndex);

        double value = readDoubleAtLeast(scanner, "Enter temperature:", bound);

        int targetIndex = readMenuChoice(scanner, "Pick target unit:", UNITS);

        double converted = convert(value, sourceIndex, targetIndex);

        System.out.println();
        System.out.println("✓ " + String.format("%.2f", value) + " "
                + SYMBOLS[sourceIndex] + " = "
                + String.format("%.2f", converted) + " "
                + SYMBOLS[targetIndex]);

        scanner.close();
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

    private static double readDoubleAtLeast(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= min) return value;
                System.out.println("Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    private static double absoluteZeroFor(int unitIndex) {
        return switch (unitIndex) {
            case 0 -> -273.15;
            case 1 -> -459.67;
            case 2 -> 0.0;
            default -> Double.NEGATIVE_INFINITY;
        };
    }

    private static double convert(double value, int from, int to) {
        if (from == to) return value;

        double celsius = switch (from) {
            case 0 -> value;
            case 1 -> (value - 32.0) * 5.0 / 9.0;
            case 2 -> value - 273.15;
            default -> value;
        };

        return switch (to) {
            case 0 -> celsius;
            case 1 -> celsius * 9.0 / 5.0 + 32.0;
            case 2 -> celsius + 273.15;
            default -> celsius;
        };
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
