import static chirp.Chirp.*;

public class TicketPricing {

    public static void main(String[] args) {
        start();
        title("Ticket Pricing");

        int age = readInt("Enter age", 0, 120);
        boolean isStudent = readBoolean()
                .label("Are you a student?")
                .read();
        String dayType = readChoice("Day type:", "Weekday", "Weekend");
        boolean isWeekend = dayType.equals("Weekend");

        double base = basePrice(age);
        double afterDiscount = isStudent ? base * 0.8 : base;
        double total = isWeekend ? afterDiscount + 200.0 : afterDiscount;

        blank();
        info(ageCategory(age)
                + (isStudent ? " · Student" : "")
                + (isWeekend ? " · Weekend" : ""));
        info("Base: " + rupees(base));
        if (isStudent) info("Student discount: -20%");
        if (isWeekend) info("Weekend surcharge: +Rs.200.00");
        line();
        success("Total: " + rupees(total));
    }

    private static String ageCategory(int age) {
        if (age <= 12) return "Child";
        if (age >= 65) return "Senior";
        return "Adult";
    }

    private static double basePrice(int age) {
        if (age <= 12) return 500.0;
        if (age >= 65) return 700.0;
        return 1000.0;
    }
}
