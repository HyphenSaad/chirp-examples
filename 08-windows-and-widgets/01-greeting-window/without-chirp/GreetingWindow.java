import java.util.Scanner;

public class GreetingWindow {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Greeting Window");

        System.out.print("Enter your name: ");
        String name = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (name.isEmpty()) name = "stranger";

        System.out.println();
        System.out.println("✓ Hello, " + name + "!");
        System.out.println("ℹ Good " + timeOfDay() + "!");

        scanner.close();
    }

    private static String timeOfDay() {
        int h = java.time.LocalTime.now().getHour();
        if (h < 12) return "morning";
        if (h < 17) return "afternoon";
        return "evening";
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
