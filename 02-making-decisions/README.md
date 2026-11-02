# Level 2 — Making Decisions

Programs that branch. Every problem in this level is the same shape: read some input, run it through a decision, print a result that depends on which branch was taken.

## What this level covers

- **Chained ranges** — `if (x >= 90) … else if (x >= 80) …` and why the order matters
- **Independent modifiers** — three inputs, each adjusting the result on its own
- **Pairwise comparison** — two inputs compared against a matrix of outcomes
- **Guard clauses** — checking a special case first, returning early, so the happy path stays flat
- **The boundary problem** — the difference between `>`, `>=`, `<`, `<=` at the exact edges

## What it doesn't cover yet

- **Loops** — still none. Every problem runs once and exits. That's Level 3.
- **User-written classes** — still none. That's Level 5.
- **GUI** — still none. That's Level 5.

## The three problems

| Problem | Concept | With Chirp | Without Chirp |
|---|---|---|---|
| [01-grade-calculator](01-grade-calculator/) | Chained ranges, boundary values | 30 lines | 65 lines |
| [02-ticket-pricing](02-ticket-pricing/) | Independent modifiers that combine | 45 lines | 100 lines |
| [03-rock-paper-scissors](03-rock-paper-scissors/) | Pairwise comparison of two inputs | 40 lines | 80 lines |

The line-count gap widens slightly at this level because the `without-chirp` versions now have *two or three* inputs to validate, each with its own retry loop. That pattern repeats in every example — a strong signal that the student is writing the same helpers over and over.

## Reading order

If you're reading this level after finishing Level 1, start at **01-grade-calculator** even if it looks trivial. The boundary-value table there is the single most important thing in this level.
