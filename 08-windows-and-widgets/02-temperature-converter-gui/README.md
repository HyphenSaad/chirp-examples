# Temperature Converter GUI

Type a Celsius temperature and see the Fahrenheit equivalent update instantly.

## Requirements

1. Display a **window** with these controls:
   - A **title label**: `"Temperature Converter"`, large and bold
   - A **hint label**: `"Type a Celsius temperature"`, muted colour
   - A **text field** with placeholder `"Celsius"`
   - A **result label** showing the Fahrenheit value in the format `"NN.NN °F"`, large
2. The result label updates **live as the user types**.
3. **Valid input** (any number) — compute `F = C × 9/5 + 32`, format to **two decimal places**, display in black.
4. **Invalid input** (empty, non-numeric, partial like `"12."`) — display `"Enter a number"` in red.
5. Negative temperatures are allowed (any real number is valid Celsius).
6. The window is approximately **400 × 300** pixels.

## Example — GUI

```
┌─ Temperature Converter ──────────────┐
│                                      │
│       Temperature Converter          │
│                                      │
│     Type a Celsius temperature       │
│                                      │
│   ┌────────────────────────────────┐ │
│   │ 100                            │ │
│   └────────────────────────────────┘ │
│                                      │
│             212.00 °F                │
│                                      │
└──────────────────────────────────────┘
```

For invalid input:

```
   ┌────────────────────────────────┐
   │ abc                            │
   └────────────────────────────────┘

           Enter a number
              (in red)
```

## Boundary cases you must verify

| Celsius input | Expected Fahrenheit |
|---|---|
| `0` | `32.00 °F` |
| `100` | `212.00 °F` |
| `-40` | `-40.00 °F` |
| `37` | `98.60 °F` |
| `36.6` | `97.88 °F` |
| `-273.15` | `-459.67 °F` |
| `""` (empty) | `"Enter a number"` in red |
| `abc` | `"Enter a number"` in red |
| `12.` (trailing dot) | `"Enter a number"` in red — `Double.parseDouble` rejects |
| `12.34.56` | `"Enter a number"` in red |
| ` 100 ` (with spaces) | `"Enter a number"` — `Double.parseDouble` rejects leading/trailing whitespace |

The `12.` case is worth highlighting: `Double.parseDouble("12.")` **throws** in Java. The result label goes red. If you want to be lenient and accept `12.`, strip trailing dots before parsing — but the demo sticks with the strict behaviour to keep the comparison clean.

## The live-validation pattern

The result label handles three states — valid number, invalid, empty — without a separate "validate" button:

```java
nameField.onChange(text -> {
    try {
        double c = Double.parseDouble(text);
        double f = c * 9.0 / 5.0 + 32.0;
        resultLabel.text(decimal(f, 2) + " °F");
        resultLabel.color("#09090b");   // black — valid
    } catch (NumberFormatException e) {
        resultLabel.text("Enter a number");
        resultLabel.color("#dc2626");   // red — error
    }
});
```

The colour change is not decoration — it's the fastest possible feedback. A red label reads as "wrong" in a fraction of a second; a black label reads as "correct". That's how form validation should feel: instant and unmissable.

The alternative — press Enter to validate — adds a keystroke for no benefit. Live validation is one `onChange` away.

## The console version drops the live aspect

The console version reads one Celsius value, prints one Fahrenheit value, exits. It cannot loop — there's no "keystroke event" in a console. That asymmetry is what this example is designed to highlight: **the *logic* is the same formula**, the *interaction* is fundamentally different.

## The four variants compared

| Concern | with-chirp console | without-chirp console | with-chirp-gui | without-chirp-gui |
|---|---|---|---|---|
| Lines | 15 | 43 | 35 | 53 |
| Read Celsius | `readDouble(prompt).read()` | `Scanner.nextDouble()` + retry loop | `Double.parseDouble(field.value())` inside `onChange` | `Double.parseDouble(field.getText())` inside `textProperty` listener |
| Compute | same formula | same formula | same formula | same formula |
| Format output | `decimal(f, 2)` | `String.format("%.2f", f)` | `decimal(f, 2)` | `String.format("%.2f", f)` |
| Colour | console symbol | console symbol | `resultLabel.color("#dc2626")` | `resultLabel.setStyle("-fx-text-fill: red;")` |
| Layout | implicit | implicit | `Stack.v(12).padding(24).align(CENTER)` | `new VBox(12, ...)` + `setAlignment` + `setPadding` |

The formula is unchanged across all four. Everything else — validation, formatting, colour, layout — is different. That's the point.
