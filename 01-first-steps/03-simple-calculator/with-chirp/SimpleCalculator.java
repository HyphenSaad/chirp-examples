import static chirp.Chirp.*;

public class SimpleCalculator {

    public static void main(String[] args) {
        start();
        title("Simple Calculator");

        double a = readDouble("First number");
        String op = readChoice("Operator:", "+", "-", "*", "/");
        double b = readDouble("Second number");

        if (op.equals("/") && b == 0) {
            blank();
            error("Cannot divide by zero.");
            return;
        }

        double result = switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> Double.NaN;
        };

        blank();
        success(fmt("{a} {op} {b} = {r}",
                "a", decimal(a, 2),
                "op", op,
                "b", decimal(b, 2),
                "r", decimal(result, 2)));
    }
}
