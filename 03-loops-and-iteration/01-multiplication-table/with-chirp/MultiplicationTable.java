import static chirp.Chirp.*;

public class MultiplicationTable {

    public static void main(String[] args) {
        start();
        title("Multiplication Table");

        int n = readInt("Table size", 1, 12);
        blank();

        print("    ");
        for (int c = 1; c <= n; c++) printf("%4d", c);
        println();

        print("    ");
        for (int c = 1; c <= n; c++) printf("----");
        println();

        for (int r = 1; r <= n; r++) {
            printf("%4d", r);
            for (int c = 1; c <= n; c++) printf("%4d", r * c);
            println();
        }
    }
}
