import chirp.ui.*;
import javafx.geometry.Pos;
import static chirp.Chirp.*;

public class TemperatureConverterGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            TextField celsiusField = TextField.of().placeholder("Celsius");
            Label resultLabel = Label.heading("—");
            Label hintLabel = Label.muted("Type a Celsius temperature");

            celsiusField.onChange(text -> {
                try {
                    double c = Double.parseDouble(text);
                    double f = c * 9.0 / 5.0 + 32.0;
                    resultLabel.text(decimal(f, 2) + " °F");
                    resultLabel.color("#09090b");
                } catch (NumberFormatException e) {
                    resultLabel.text("Enter a number");
                    resultLabel.color("#dc2626");
                }
            });

            Stack root = Stack.v(12).padding(24).align(Pos.CENTER);
            root.add(
                    Label.heading("Temperature Converter"),
                    hintLabel,
                    celsiusField.fullWidth(),
                    resultLabel);

            Ui.show("Temperature Converter", root);
        });
    }
}
