import static chirp.Chirp.*;

public class TemperatureConverter {

    private static final String[] UNITS = {"Celsius", "Fahrenheit", "Kelvin"};
    private static final String[] SYMBOLS = {"°C", "°F", "K"};

    public static void main(String[] args) {
        start();
        title("Temperature Converter");

        String sourceUnit = readChoice("Pick source unit:", UNITS);
        int sourceIndex = indexOf(UNITS, sourceUnit);
        double bound = absoluteZeroFor(sourceIndex);

        double value = readDouble("Enter temperature")
                .min(bound)
                .read();

        String targetUnit = readChoice("Pick target unit:", UNITS);
        int targetIndex = indexOf(UNITS, targetUnit);

        double converted = convert(value, sourceIndex, targetIndex);

        blank();
        success(fmt("{v} {su} = {c} {tu}",
                "v", decimal(value, 2), "su", SYMBOLS[sourceIndex],
                "c", decimal(converted, 2), "tu", SYMBOLS[targetIndex]));
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

    private static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}
