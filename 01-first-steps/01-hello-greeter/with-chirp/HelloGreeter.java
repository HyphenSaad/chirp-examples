import static chirp.Chirp.*;

import chirp.text.Check;

public class HelloGreeter {

    public static void main(String[] args) {
        start();
        title("Hello Greeter");

        String name = readString()
            .label("What is your name?")
            .atLeast(3)
            .atMost(10)
            .notBlank()
            .skipFormatWarning()
            .custom(Check::isAlpha, "Only Alphabets Allowed!")
            .read();

        int age = readInt();

        blank();
        success("Hello, " + name + "! You are " + age + " years old.");

        if (isAdult(age)) {
            info("You're an adult.");
        } else {
            info("You're a minor.");
        }

        line();
        done("Goodbye, " + name + "!");
    }
}
