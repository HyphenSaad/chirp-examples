import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.List;

public class CompoundInterestCalculator {

    public static void main(String[] args) {
        start();
        title("Compound Interest Calculator");

        double principal = readDouble("Principal amount").min(0.01).read();
        double annualRate = readDouble("Annual rate (%)").min(0).max(100).read();
        int years = readInt("Number of years", 1, 5);

        double monthlyRate = annualRate / 1200.0;
        int months = years * 12;

        double balance = principal;
        List<String[]> rows = new ArrayList<>();
        for (int m = 1; m <= months; m++) {
            double interest = balance * monthlyRate;
            balance += interest;
            rows.add(new String[]{
                    String.valueOf(m),
                    rupees(interest),
                    rupees(balance)
            });
        }

        double totalInterest = balance - principal;

        blank();
        table(rows, "Month", "Interest", "Balance");

        blank();
        success("Principal:      " + rupees(principal));
        success("Total interest: " + rupees(totalInterest));
        success("Final balance:  " + rupees(balance));
    }
}
