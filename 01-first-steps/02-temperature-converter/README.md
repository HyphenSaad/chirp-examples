# Temperature Converter

Convert a temperature between Celsius, Fahrenheit, and Kelvin. Ask for the source unit, a value, and the target unit.

## Requirements

1. Ask for the **source unit** first, using a numbered menu of exactly three options: `Celsius`, `Fahrenheit`, `Kelvin`. Invalid menu choices must be rejected and re-asked.
2. Ask for the **temperature value** as a decimal number. The value must be **at least absolute zero** for the chosen source unit:
   - Celsius: `-273.15`
   - Fahrenheit: `-459.67`
   - Kelvin: `0`
   Invalid values (below the bound, non-numeric, empty) must be re-asked.
3. Ask for the **target unit** using the same numbered menu as the source.
4. If the source and target are the same unit, print the value unchanged.
5. Print the result rounded to **two decimal places**, in the format:
   `"✓ {value} {sourceSymbol} = {converted} {targetSymbol}"`

   The symbols are `°C`, `°F`, and `K` respectively.

## Example interaction

```
========================================
│ Temperature Converter
========================================
Pick source unit:
  1) Celsius
  2) Fahrenheit
  3) Kelvin
Your choice [1-3]: 1
Enter temperature: 100
Pick target unit:
  1) Celsius
  2) Fahrenheit
  3) Kelvin
Your choice [1-3]: 2

✓ 100.00 °C = 212.00 °F
```

## Edge cases to verify

| Scenario | Expected behaviour |
|---|---|
| Source unit = target unit | Value unchanged, printed to 2 decimals |
| `-273.15 °C → K` | `0.00 K` — absolute zero, accepted |
| `-274 °C` | Rejected — below the Celsius bound |
| `-459.67 °F → K` | `0.00 K` — Fahrenheit absolute zero, accepted |
| `0 K → °C` | `-273.15 °C` — Kelvin absolute zero |
| Non-numeric value (`"hot"`) | Re-asked |
| Menu choice `5` | Re-asked |

## Conversion formulas

Use Celsius as the intermediate for every conversion:

- Fahrenheit to Celsius: `(f - 32) × 5 / 9`
- Celsius to Fahrenheit: `c × 9 / 5 + 32`
- Kelvin to Celsius: `k - 273.15`
- Celsius to Kelvin: `c + 273.15`

Chaining through Celsius is more reliable than writing nine pairwise formulas, because there is only one place to get each of the four transformations right.

## What you'll practice

- Reading a decimal with a **lower bound that depends on an earlier answer**
- Presenting a fixed set of options as a numbered menu
- Getting back the **chosen string** (not the index), so the program never has to map `1` back to `"Celsius"`
- Composing a result line from several pieces with a template
- Formatting a decimal to a fixed number of places

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 64 | 106 |
| Decimal input with a bound | `readDouble(prompt).min(bound).read()` | hand-rolled `while` + `try`/`catch` + bound check |
| Menu selection | `readChoice(prompt, options...)` returns the string | hand-rolled `for` loop + range-checked integer read + index lookup |
| Result formatting | `fmt("{v} {su} = {c} {tu}", ...)` | manual `+` concatenation |
| Two-decimal rounding | `decimal(value, 2)` | `String.format("%.2f", value)` |

The `with-chirp` version still writes the arithmetic and the four conversion formulas — because those *are* the problem. What it skips is the input plumbing.
