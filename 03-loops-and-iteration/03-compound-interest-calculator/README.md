# Compound Interest Calculator

Read a principal amount, an annual interest rate, and a duration. Print a month-by-month breakdown of how the balance grows, then a summary of total interest earned.

## Requirements

1. Ask for the **principal amount** in rupees — a decimal greater than `0`. The user enters a plain number (not `Rs.`-prefixed); display and formatting are the program's job.
2. Ask for the **annual interest rate** as a percentage — a decimal between `0` and `100` inclusive. `12` means 12% per year, not 0.12.
3. Ask for the **duration in years** — a whole number from `1` to `5` inclusive. The five-year cap keeps the printed output to at most 60 rows; the underlying math would work for any duration.
4. Compute the **monthly compounding** schedule:
   - Monthly rate = `annualRate / 12 / 100`
   - For each month: `interest = balance × monthlyRate`, then `balance = balance + interest`
5. Print a **title bar**: `"│ Compound Interest Calculator"` between two lines of `=`.
6. Print a **monthly breakdown table** with three columns: `Month`, `Interest`, `Balance`. All monetary values formatted as `Rs.1,234.56` (rupees, thousands separator, two decimal places).
7. Print a **summary** after the table with three lines, right-aligned:
   - `"Principal:      Rs.{principal}"`
   - `"Total interest: Rs.{totalInterest}"`
   - `"Final balance:  Rs.{balance}"`
8. If the interest rate is `0`, the table still prints (every interest is `Rs.0.00`, the balance never changes) and the total interest is `Rs.0.00`.

## Example interaction — 12% annual, 1 year, principal Rs. 10,000

```
========================================
│ Compound Interest Calculator
========================================
Principal amount: 10000
Annual rate (%): 12
Number of years: 1

Month  Interest     Balance
-----  -----------  -----------
1      Rs.100.00    Rs.10,100.00
2      Rs.101.00    Rs.10,201.00
3      Rs.102.01    Rs.10,303.01
4      Rs.103.03    Rs.10,406.04
5      Rs.104.06    Rs.10,510.10
6      Rs.105.10    Rs.10,615.20
7      Rs.106.15    Rs.10,721.35
8      Rs.107.21    Rs.10,828.57
9      Rs.108.29    Rs.10,936.85
10     Rs.109.37    Rs.11,046.22
11     Rs.110.46    Rs.11,156.68
12     Rs.111.57    Rs.11,268.25

Principal:      Rs.10,000.00
Total interest: Rs.1,268.25
Final balance:  Rs.11,268.25
```

## Boundary cases you must check

| Case | Expected behaviour |
|---|---|
| Principal `10000`, rate `12`, years `1` | Final balance `Rs.11,268.25` — the standard case above |
| Rate `0` | Every interest is `Rs.0.00`, balance never changes, total interest is `Rs.0.00` |
| Years `1` | 12 rows in the table |
| Years `5` | 60 rows in the table — largest allowed |
| Years `0` or `6` | Rejected at input |
| Principal `0` | Rejected — must be > 0 |
| Principal `-500` | Rejected |
| Rate `-1` or `101` | Rejected |

## The compound-interest formula (and why the loop exists)

The closed-form formula for compound interest with monthly compounding is:

```
final = principal × (1 + r/12) ^ months
```

where `r` is the annual rate as a decimal (`12%` → `r = 0.12`) and `months = years × 12`.

With `principal = 10000`, `r = 0.12`, `months = 12`:

```
final = 10000 × (1.01) ^ 12 = 10000 × 1.126825 = 11,268.25
```

That formula gets the answer in one line. **So why use a loop?**

Two reasons:

1. **The table requires every intermediate value.** A user who wants to see the balance at month 7 needs the balance at month 7. The closed-form formula gives you month 12 and nothing in between.
2. **The loop is the honest model.** Compound interest *is* the interest earning interest, month by month. `balance = balance × (1 + r/12)` in a loop is a literal transcription of the definition. The closed-form formula is a mathematical shortcut *derived from* that loop — it's a theorem about the loop, not the definition of compounding.

So: use the loop for anything where you need to show the schedule. Use the formula for a single final value (a calculator app, a what-if tool, a quick sanity check). The rule of thumb is: **if the user asks "what will I have at the end?", use the formula; if they ask "how will it grow?", use the loop.** This problem is the second case.

## The "running accumulator" loop pattern

The core of this problem is a **`for` loop with a running accumulator**:

```java
double balance = principal;
for (int m = 1; m <= months; m++) {
    double interest = balance * monthlyRate;
    balance += interest;
    // ... record this month's row for the table ...
}
```

Three properties of this pattern:

- **Initial value matters** — `balance` starts at `principal`, not `0`. If you started at `0`, every interest would be `0` and nothing would ever grow.
- **The update uses the previous value** — `interest = balance * monthlyRate` and then `balance += interest`, so this month's `balance` depends on last month's `balance`. That's why it can't be a `while` loop that ends early — every iteration feeds the next.
- **The update is in the loop body, not the loop header** — `m++` increments the counter, but the *real* state change happens inside the body. That's fine; the loop header's job is just to count.

Contrast with the multiplication table's loops, where the loop body has no state to carry between iterations. Different shape, different pattern — both correct in their own context.

## What you'll practice

- **A `for` loop with a running accumulator** — `balance` is the accumulator
- **The month-by-month table pattern** — build a `List<String[]>` inside the loop, render it after
- **Compound interest math** — the `balance = balance × (1 + rate)` iteration
- **Formatted currency** — `Rs.` prefix, thousands separator, two decimal places
- **A loop that must complete every iteration** — this isn't a "search until found" loop; every month must be accounted for

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 40 | 135 |
| Principal input (min only) | `readDouble("Principal").min(0.01).read()` | hand-rolled `while` + `try`/`catch` + bound check |
| Rate input (min and max) | `readDouble("Annual rate (%)").min(0).max(100).read()` | hand-rolled `while` + `try`/`catch` + two bound checks |
| Years input | `readInt("Years", 1, 5)` | hand-rolled `while` + `try`/`catch` + range check |
| Currency format | `rupees(amount)` — one call | manual `"Rs." + String.format("%,.2f", amount)` per call |
| Table render | `table(rows, "Month", "Interest", "Balance")` — automatic column widths | manual column-width computation + padding + separator |
| Loop and math | identical | identical |

The `table` helper is where this example earns its name — it computes column widths from the widest cell, which for `Rs.11,268.25` is wider than the header `"Balance"`. That width computation is about twenty lines when done by hand (as the `without-chirp` version shows), and it's the kind of tedious math that nobody wants to write twice.
