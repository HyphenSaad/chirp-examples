# Multiplication Table

Read a number N and print the N × N multiplication table, formatted with aligned columns and row/column headers.

## Requirements

1. Ask for a **table size N** — a whole number between **1 and 12** inclusive.
2. Print the table as an N×N grid where the cell at row R, column C contains `R × C`.
3. Include a **header row** across the top showing column numbers `1` through `N`.
4. Include a **header column** down the left side showing row numbers `1` through `N`.
5. Include a **separator row** between the header and the body — a line of `----` characters, one segment per column.
6. Every cell must be right-aligned in a **4-character field** so the columns line up regardless of `N`. The header cells and separator segments use the same 4-character width.
7. Print a title bar at the top: `"│ Multiplication Table"` between two lines of `=`.

## Example interaction — N = 5

```
========================================
│ Multiplication Table
========================================
Table size: 5

       1   2   3   4   5
    ----  ----  ----  ----  ----
   1   1   2   3   4   5
   2   2   4   6   8  10
   3   3   6   9  12  15
   4   4   8  12  16  20
   5   5  10  15  20  25
```

## Example interaction — N = 1 (edge case)

```
Table size: 1

       1
    ----
   1   1
```

## Boundary cases you must check

| N | Expected shape |
|---|---|
| 1 | 1×1 table — single header cell, single data row |
| 2 | 2×2 table |
| 5 | 5×5 table — the "normal" case above |
| 12 | 12×12 table — largest allowed; check that `12 × 12 = 144` fits in 4 chars |
| 0 | Rejected — re-ask |
| 13 | Rejected — re-ask |
| Non-numeric | Rejected — re-ask |

The value `144` in the 12×12 case is exactly three digits — it fits in `%4d` with one space to spare. `%3d` would *just barely* work but leaves no gap. `%4d` is the safest choice for any N up to 12, and it's why the README specifies 4 characters.

## The two things that go wrong in nested loops

### Off-by-one on either bound

```java
for (int r = 1; r <= n; r++)     // correct: 1 to n inclusive
for (int r = 1; r <  n; r++)     // WRONG: misses row n
for (int r = 0; r <  n; r++)     // WRONG: starts at 0, misses row n
for (int r = 1; r <= n + 1; r++) // WRONG: goes one past n
```

The number of rows printed is `n` when `r` goes from `1` to `n` inclusive. Any of the three wrong versions above also changes the **content** — starting at `0` would print a `0 × 1 = 0` row, which doesn't make sense for a multiplication table.

### The header/body mismatch

The header row and the body rows use different column counts unless you're careful:

```java
// Header: 5 cells (columns 1..5)
for (int c = 1; c <= n; c++) print(header[c]);

// Body: 5 cells (columns 1..5)
for (int c = 1; c <= n; c++) print(body[r][c]);
```

Both loops iterate `c` from `1` to `n`. If you accidentally write `c < n` in one and `c <= n` in the other, the header will have one fewer column than the body, and the alignment breaks.

Rule of thumb: **when two loops produce rows that must align, they must use identical bounds.** If one changes, the other must change too. That's a warning sign that the loop logic should be extracted into a helper (which is what the `without-chirp` version does, more than once).

## What you'll practice

- **Nested `for` loops** — the outer loop runs once per row, the inner loop runs once per column
- **Column alignment** — right-justify numbers so single-, double-, and triple-digit values line up
- **The header/separator/body pattern** — three row shapes, three separate passes
- **Edge cases** — N=1 produces a valid but degenerate table; N=0 must be rejected

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 26 | 55 |
| Table size input | `readInt("Table size", 1, 12)` | hand-rolled `while` + `try`/`catch` + range check |
| Title bar | `title("Multiplication Table")` | hand-written `printTitle` helper |
| Blank line | `blank()` | `System.out.println()` |
| Loops and formatting | identical — this is the lesson | identical |

The loops themselves are **byte-for-byte identical** in both versions. That is not a bug in the comparison — it is the point. Chirp does not try to make loops shorter, because a loop in a teaching library *should look like a loop*. What Chirp replaces is the twenty-nine lines of input plumbing that surround the loop.

If you read this example and think "the with-chirp version is only twice as short, that's not much" — good. That's the honest picture. For loop-centric problems, the library's value is smaller than for input-heavy problems. Use that to calibrate when it's worth adding a dependency and when it isn't.
