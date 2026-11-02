import chirp.ui.*;
import javafx.geometry.Pos;
import static chirp.Chirp.*;

public class SimpleCalculatorGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            TextField aField = TextField.of().placeholder("First number");
            TextField bField = TextField.of().placeholder("Second number");
            Select operator = Select.of("+", "-", "*", "/").selected("+");
            Label resultLabel = Label.heading("—");

            Runnable compute = () -> {
                try {
                    double a = Double.parseDouble(aField.value().trim());
                    double b = Double.parseDouble(bField.value().trim());
                    double result = switch (operator.value()) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        case "/" -> b == 0 ? Double.NaN : a / b;
                        default -> 0;
                    };
                    if (Double.isNaN(result)) {
                        resultLabel.text("Cannot divide by zero").color("#dc2626");
                    } else {
                        resultLabel.text(decimal(result, 2)).color("#09090b");
                    }
                } catch (NumberFormatException e) {
                    resultLabel.text("—").color("#71717a");
                }
            };

            aField.onChange(v -> compute.run());
            bField.onChange(v -> compute.run());
            operator.onChange(op -> compute.run());

            Row inputRow = Row.h(8).add(aField, bField, operator);

            Stack root = Stack.v(16).padding(24).align(Pos.CENTER);
            root.add(
                    Label.heading("Simple Calculator"),
                    inputRow,
                    Label.muted("Result"),
                    resultLabel);

            Ui.show("Simple Calculator", root);
        });
    }
}
