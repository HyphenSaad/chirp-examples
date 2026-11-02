import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MultiplicationTableGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Multiplication Table");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label sizeCaption = new Label("Table size");
        Spinner<Integer> sizeSpinner = new Spinner<>(1, 12, 5);
        sizeSpinner.setEditable(true);
        sizeSpinner.setPrefWidth(100);

        HBox inputRow = new HBox(12, sizeCaption, sizeSpinner);
        inputRow.setAlignment(Pos.CENTER);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(18);
        output.setStyle("-fx-font-family: 'Consolas', 'Menlo', monospace; -fx-font-size: 14px;");
        VBox.setVgrow(output, Priority.ALWAYS);

        Runnable render = () -> {
            int n = sizeSpinner.getValue();
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
            output.setText(sb.toString());
        };

        sizeSpinner.valueProperty().addListener((o, old, next) -> render.run());
        render.run();

        VBox root = new VBox(12, titleLabel, inputRow, output);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 480, 500);
        stage.setTitle("Multiplication Table");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
