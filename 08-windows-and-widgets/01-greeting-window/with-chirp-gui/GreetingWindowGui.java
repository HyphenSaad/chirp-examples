import chirp.ui.*;
import javafx.geometry.Pos;

public class GreetingWindowGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            TextField nameField = TextField.of().placeholder("Enter your name");
            Label greetingLabel = Label.heading("Hello, stranger!");
            Label timeLabel = Label.muted("Good " + timeOfDay() + "!");

            nameField.onChange(name -> {
                String display = name.isBlank() ? "stranger" : name.trim();
                greetingLabel.text("Hello, " + display + "!");
            });

            Button clearButton = Button.outline("Clear").onClick(() -> {
                nameField.text("");
                nameField.requestFocus();
            });

            Stack root = Stack.v(12).padding(24).align(Pos.CENTER);
            root.add(
                    Label.heading("Greeting Window"),
                    nameField.fullWidth(),
                    greetingLabel,
                    timeLabel,
                    clearButton);

            Ui.show("Greeting Window", root);
        });
    }

    private static String timeOfDay() {
        int h = java.time.LocalTime.now().getHour();
        if (h < 12) return "morning";
        if (h < 17) return "afternoon";
        return "evening";
    }
}
