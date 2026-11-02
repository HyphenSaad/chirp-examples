# Grade Calculator

Read a score between 0 and 100 and print the letter grade it maps to. Also print a short message tied to the grade.

## Requirements

1. Ask for a score. It must be a whole number between **0 and 100** inclusive. Non-numeric input, negative numbers, or numbers above 100 must be rejected and re-asked.
2. Compute the letter grade using these ranges:

   | Score range | Grade |
   |---|---|
   | 90–100 | A |
   | 80–89 | B |
   | 70–79 | C |
   | 60–69 | D |
   | 0–59 | F |

3. Print the score and grade on one line:
   `"✓ Score: {score} — Grade: {grade}"`
4. Print a message on the next line, depending on the grade:

   | Grade | Message |
   |---|---|
   | A | `"Excellent!"` |
   | B | `"Good work."` |
   | C | `"Satisfactory."` |
   | D | `"You passed — barely."` |
   | F | `"Failing. Please retake."` |

## Example interaction

```
========================================
│ Grade Calculator
========================================
Enter score: 87

✓ Score: 87 — Grade: B
ℹ Good work.
```

## Boundary cases you must check

| Score | Expected grade | Why this case matters |
|---|---|---|
| 100 | A | Top of range, inclusive |
| 90 | A | Lower bound of A — the `>=` at work |
| 89 | B | One below A — the "just misses" case |
| 80 | B | Lower bound of B |
| 70 | C | Lower bound of C |
| 69 | D | One below C |
| 60 | D | Lower bound of D |
| 59 | F | One below D |
| 0 | F | Bottom of range, inclusive |

That table is the whole point of this problem. **Half of all grade-boundary bugs come from using `>` where `>=` was intended** (or the other way around). Writing the table first, then the code, is the habit to build.

## The "order matters" lesson

The most common bug in a grade calculator is writing the checks in the wrong order:

```java
// WRONG — every score ends up F
if (score >= 0)  return "F";
if (score >= 60) return "D";
if (score >= 70) return "C";
if (score >= 80) return "B";
if (score >= 90) return "A";
```

`score >= 0` is true for *every* valid score, so `return "F"` fires first and none of the later branches are ever reached.

The **correct** order is highest-first:

```java
if (score >= 90) return "A";
if (score >= 80) return "B";
if (score >= 70) return "C";
if (score >= 60) return "D";
return "F";
```

Or lowest-first using `else if`:

```java
if (score < 60)      return "F";
else if (score < 70) return "D";
else if (score < 80) return "C";
else if (score < 90) return "B";
else                 return "A";
```

Both are correct. The first reads as "what's the highest grade I qualify for?" The second reads as "what's the *lowest* grade tier I've cleared?". Pick one and be consistent — mixing the two conventions in the same function is how bugs happen.

## What you'll practice

- Writing an `if` / `else if` chain where **order is load-bearing**
- Boundary values — the exact edges of each range
- A second decision layered on the first (grade → message)
- Reusing a `switch` for the message instead of another `if` chain

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 35 | 63 |
| Score input with range | `readInt("Enter score", 0, 100)` | hand-rolled `while` + `try`/`catch` + range check |
| Success line | `success("Score: ...")` | manual `System.out.println("✓ ...")` |
| Grade → message | `switch (grade) { case "A" -> … }` | same — no library needed for this |

The arithmetic and the branch logic are identical in both versions, because **that is the problem**. What Chirp replaces is the input plumbing, which you've already written by hand twice in Level 1.
