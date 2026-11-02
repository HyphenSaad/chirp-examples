import java.util.Scanner;

public class TemperatureConverter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Temperature Converter");

        double c = readDouble(scanner, "Celsius:");
        double f = c * 9.0 / 5.0 + 32.0;

        System.out.println();
        System.out.println("✓ " + String.format("%.2f", c) + " °C = "
                + String.format("%.2f", f) + " °F");

        scanner.close();
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
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
