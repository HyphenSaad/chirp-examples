# Level 3 — Loops & Iteration

Programs that repeat. Every problem in this level is built around a loop, and the loop is the lesson — everything else is scaffolding around it.

## What this level covers

- **Nested `for` loops** — outer loop for rows, inner loop for columns
- **`while (true)` with early exit** — repeat until a goal is reached
- **`for` with accumulation** — mutate a running value on each iteration
- **Loop bounds and edge cases** — off-by-one errors, empty ranges, single iterations
- **Formatted loop output** — aligned columns, tabular rows

## What it doesn't cover yet

- **User-written classes** — still none. That's Level 5.
- **Files and persistence** — that's Level 6.
- **GUI** — that's Level 5.

## The three problems

| Problem | Loop shape | With Chirp | Without Chirp |
|---|---|---|---|
| [01-multiplication-table](01-multiplication-table/) | Nested `for` | ~25 lines | ~55 lines |
| [02-number-guessing-game](02-number-guessing-game/) | `while (true)` + `return` | ~25 lines | ~60 lines |
| [03-compound-interest-calculator](03-compound-interest-calculator/) | `for` with accumulation | ~45 lines | ~90 lines |

## An honest note on Chirp and loops

Chirp does **not** shorten the loops in these examples. A `for (int i = 1; i <= n; i++)` is a `for (int i = 1; i <= n; i++)` in any language or library, and no helper can improve it.

What Chirp shortens is everything *around* the loop:
- The input validation that runs before the loop
- The formatted output that runs inside or after it
- The random-number generation for the guessing game
- The table rendering for the compound-interest breakdown

In Levels 1 and 2, the with/without gap was mostly input plumbing. In Level 3, the gap widens — because table rendering is the first place where a library genuinely replaces non-trivial code (column-width computation, aligned padding, header separators). That gap is honest signal: *some* things are still worth writing yourself, and *some* things are worth using a helper for.

## Reading order

Read the problems in order. Each introduces one loop shape, and the next one assumes you've mastered the previous:

1. **Multiplication Table** teaches nested `for` — you'll write this exact pattern again in every table, grid, and matrix problem.
2. **Number Guessing Game** teaches `while (true)` with a `return` — this is how you wait for user input or a network response.
3. **Compound Interest Calculator** teaches `for` with accumulation — this is how you compute a running total, a schedule, a summary.

## The three "gotchas" that trip up beginners

Every level has its trap list. In Level 3, they are:

1. **Off-by-one** — `for (int i = 0; i <= n; i++)` runs `n+1` times, not `n`. Every loop needs a moment of "should this be `<` or `<=`?" before you commit.
2. **The empty loop** — `for (int i = 0; i < 0; i++)` runs zero times and produces no output. Usually a bug, sometimes intended. If your program prints nothing, check the loop bounds first.
3. **Modifying the counter** — `for (int i = 0; i < 10; i++) { i = i + 2; }` skips every other number, but the `i++` still fires. Almost never what you want.

Every problem README lists a "boundary cases" table with the edge cases to verify. Use it.
