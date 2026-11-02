# Simple Calculator

Read two numbers and an operator, compute the result, and reject division by zero.

## Requirements

1. Ask for the **first number** as a decimal.
2. Ask for the **operator** using a numbered menu of exactly four options: `+`, `-`, `*`, `/`. Invalid menu choices must be rejected and re-asked.
3. Ask for the **second number** as a decimal.
4. If the operator is `/` **and** the second number is exactly `0`, print an error line `"✗ Cannot divide by zero."` and stop — do **not** print a result line.
5. Otherwise, print the result rounded to **two decimal places** in the format:
   `"✓ {a} {op} {b} = {result}"`
6. Print a title bar at the top using the same style as Level 1's other problems.

## Example interaction — normal case

```
========================================
│ Simple Calculator
========================================
First number: 10
Operator:
  1) +
  2) -
  3) *
  4) /
Your choice [1-4]: 4
Second number: 3

✓ 10.00 / 3.00 = 3.33
```

## Example interaction — error case

```
========================================
│ Simple Calculator
========================================
First number: 10
Operator:
  1) +
  2) -
  3) *
  4) /
Your choice [1-4]: 4
Second number: 0

✗ Cannot divide by zero.
```

## Edge cases to verify

| Scenario | Expected behaviour |
|---|---|
| `10 / 3` | `3.33` — rounded to 2 decimals |
| `10 / 0` | Error line only, no result line |
| `0 / 10` | `0.00` — zero numerator is fine |
| `-5 * 3` | `-15.00` — negatives work |
| Non-numeric input | Re-asked |
| Menu choice `9` | Re-asked |
| `1 / 3` | `0.33` — small decimals preserved |

## What you'll practice

- Branching on the operator value — a `switch` on strings, not on integers
- A **guard clause** — the division-by-zero check returns early, so the rest of the program doesn't have to be wrapped in an `if`
- Building a result line from five pieces: two numbers, two operators, and the result
- Handling a special case that requires the program to *end* differently, not just print differently

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 34 | 86 |
| Decimal input | `readDouble(prompt)` | hand-rolled `while` + `try`/`catch` |
| Operator menu | `readChoice(prompt, "+", "-", "*", "/")` | hand-rolled menu loop |
| Error on divide-by-zero | `error(...)` | `System.err.println(...)` |
| Result formatting | `fmt(...)` + `decimal(x, 2)` | manual `String.format` concatenation |

The extra 35 lines in the `without-chirp` version are the same three helpers every other example in this level writes by hand: a title printer, a menu reader, and a numeric reader. That duplication across three examples is the first honest signal a programmer has that the "no library" path has a cost.
