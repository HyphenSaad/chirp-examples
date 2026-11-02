import static chirp.Chirp.*;

public class GreetingWindow {

    public static void main(String[] args) {
        start();
        title("Greeting Window");

        String name = readLine("Enter your name");
        if (name.isEmpty()) name = "stranger";

        blank();
        success("Hello, " + name + "!");
        info("Good " + timeOfDay() + "!");
    }

    private static String timeOfDay() {
        int h = java.time.LocalTime.now().getHour();
        if (h < 12) return "morning";
        if (h < 17) return "afternoon";
        return "evening";
    }
}
