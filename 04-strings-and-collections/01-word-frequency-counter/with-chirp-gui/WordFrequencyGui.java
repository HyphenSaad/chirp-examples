import chirp.ui.*;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static chirp.Chirp.*;

public class WordFrequencyGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            TextArea input = TextArea.of().rows(6).placeholder("Paste a paragraph here");
            Label statsLabel = Label.muted("—");
            Table resultTable = Table.of("Word", "Count", "Percentage");

            Runnable analyse = () -> {
                String text = input.value();
                Map<String, String> originalCase = new LinkedHashMap<>();
                Map<String, Integer> counts = new LinkedHashMap<>();

                for (String raw : text.split("\\s+")) {
                    String cleaned = removePunctuation(raw);
                    if (cleaned.isEmpty()) continue;
                    String key = cleaned.toLowerCase();
                    originalCase.putIfAbsent(key, cleaned);
                    counts.merge(key, 1, Integer::sum);
                }

                resultTable.clearAll();
                if (counts.isEmpty()) {
                    statsLabel.text("No words to analyse.");
                    return;
                }

                int total = counts.values().stream().mapToInt(Integer::intValue).sum();
                statsLabel.text("Total: " + total + " words · Unique: " + counts.size());

                List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
                sorted.sort(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey));

                for (int i = 0; i < Math.min(10, sorted.size()); i++) {
                    var entry = sorted.get(i);
                    resultTable.row(
                            originalCase.get(entry.getKey()),
                            entry.getValue(),
                            percent(100.0 * entry.getValue() / total, 1));
                }
            };

            input.onChange(v -> analyse.run());

            Stack root = Stack.v(12).padding(20);
            root.add(
                    Label.heading("Word Frequency Counter"),
                    Label.muted("Paste a paragraph — the top 10 words appear below"),
                    input.fullWidth(),
                    statsLabel,
                    resultTable.rowHeight(28).height(340).fullWidth());

            Ui.show("Word Frequency Counter", root);
        });
    }
}
