import static chirp.Chirp.*;

public class TemperatureConverter {

    public static void main(String[] args) {
        start();
        title("Temperature Converter");

        double c = readDouble("Celsius").read();
        double f = c * 9.0 / 5.0 + 32.0;

        blank();
        success(decimal(c, 2) + " °C = " + decimal(f, 2) + " °F");
    }
}
