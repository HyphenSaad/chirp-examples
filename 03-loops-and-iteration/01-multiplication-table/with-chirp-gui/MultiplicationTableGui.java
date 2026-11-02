import chirp.ui.*;
import javafx.geometry.Pos;

public class MultiplicationTableGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            Spinner sizeSpinner = Spinner.of(1, 12, 5);
            TextArea output = TextArea.of().rows(18);
            output.setStyle("-fx-font-family: 'Consolas', 'Menlo', monospace; -fx-font-size: 14px;");

            Runnable render = () -> {
                int n = sizeSpinner.value();
                StringBuilder sb = new StringBuilder();
                sb.append("     ");
                for (int c = 1; c <= n; c++) sb.append(String.format("%5d", c));
                sb.append("\n     ");
                for (int c = 1; c <= n; c++) sb.append("-----");
                sb.append('\n');
                for (int r = 1; r <= n; r++) {
                    sb.append(String.format("%3d  ", r));
                    for (int c = 1; c <= n; c++) sb.append(String.format("%5d", r * c));
                    sb.append('\n');
                }
                output.text(sb.toString());
            };

            sizeSpinner.onChange(v -> render.run());
            render.run();

            Row inputRow = Row.h(12).add(Label.of("Table size"), sizeSpinner).align(Pos.CENTER);

            Stack root = Stack.v(12).padding(20);
            root.add(
                    Label.heading("Multiplication Table"),
                    inputRow,
                    output.fullWidth());

            Ui.show("Multiplication Table", root);
        });
    }
}
