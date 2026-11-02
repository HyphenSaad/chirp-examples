# Ticket Pricing

Compute the price of a cinema ticket from three inputs: the buyer's age, whether they are a student, and whether the day is a weekday or a weekend.

## Requirements

1. Ask for the buyer's **age** — whole number, 0 to 120 inclusive.
2. Ask whether the buyer is a **student** — `y` or `n`.
3. Ask whether the day is a **weekday or weekend** — pick one from a two-option menu.
4. Compute the base price from age:

   | Age | Category | Base price |
   |---|---|---|
   | 0–12 | Child | Rs. 500.00 |
   | 13–64 | Adult | Rs. 1,000.00 |
   | 65–120 | Senior | Rs. 700.00 |

5. Apply modifiers in **this order**:

   | Modifier | Condition | Effect |
   |---|---|---|
   | Student discount | Student = yes | 20% off the base |
   | Weekend surcharge | Weekend | +Rs. 200.00 |

6. Print a summary showing:
   - The category line: `"ℹ Adult · Student · Weekend"` (only the applicable parts — `Child`, `Senior`, `Adult` + ` · Student` if student + ` · Weekend` if weekend)
   - The base price
   - Each applied modifier on its own line
   - A separator
   - The final total in the form `"✓ Total: Rs.{amount}"`

## Example interactions

**Adult, weekday, no discount:**
```
========================================
│ Ticket Pricing
========================================
Enter age: 30
Are you a student? (y/n): n
Day type:
  1) Weekday
  2) Weekend
Your choice [1-2]: 1

ℹ Adult
ℹ Base: Rs.1000.00
========================================
✓ Total: Rs.1000.00
```

**Adult, weekend, student:**
```
ℹ Adult · Student · Weekend
ℹ Base: Rs.1000.00
ℹ Student discount: -20%
ℹ Weekend surcharge: +Rs.200.00
========================================
✓ Total: Rs.1000.00
```

**Child, weekend:**
```
ℹ Child · Weekend
ℹ Base: Rs.500.00
ℹ Weekend surcharge: +Rs.200.00
========================================
✓ Total: Rs.700.00
```

## Boundary cases you must check

| Age | Category |
|---|---|
| 0 | Child |
| 12 | Child |
| 13 | Adult — **lower bound of adult, `>=`** |
| 64 | Adult |
| 65 | Senior — **lower bound of senior, `>=`** |
| 120 | Senior |

| Combination | Expected total |
|---|---|
| Adult, weekday, no discount | 1000.00 |
| Adult, weekday, student | 800.00 |
| Adult, weekend, no discount | 1200.00 |
| Adult, weekend, student | 1000.00 |
| Child, weekday, no discount | 500.00 |
| Child, weekend, student | 600.00 (500 × 0.8 + 200) |
| Senior, weekday, student | 560.00 (700 × 0.8) |
| Senior, weekend, student | 760.00 (700 × 0.8 + 200) |

The child-in-weekend-with-student case is worth pausing on: a five-year-old is unlikely to be a "student" in the cinema-ticket sense, but the program treats the flag as an input, not a fact. That's a design choice — the README says so explicitly so students don't try to special-case it.

## The "independent modifiers" lesson

The three decisions in this problem do not chain. They are independent:

- Age picks the **base** — one of three values
- Student applies a **multiplier** — 0.8 or 1.0, applied to whatever the base is
- Weekend applies an **adder** — +200 or 0, applied after the discount

You could write this as one giant `if` tree (`if (age <= 12 && isStudent && isWeekend) return 600; if …`) with 12 branches. **Don't.** Three independent decisions means three independent computations:

```java
double base = basePrice(age);
double afterDiscount = isStudent ? base * 0.8 : base;
double total = isWeekend ? afterDiscount + 200 : afterDiscount;
```

Three lines. Reads as English. Six branches collapsed into two ternaries and a lookup. This is the pattern to reach for whenever modifiers are independent.

**But note the order**: discount first, *then* surcharge. If you reverse it — add 200 first, then take 20% off — an adult weekend student pays `(1000 + 200) × 0.8 = 960`, not `1000 × 0.8 + 200 = 1000`. The README lists the modifiers in the correct order for a reason. When in doubt, ask: *what would a real cinema's POS system do?* Discounts before surcharges is the industry convention.

## What you'll practice

- Multiple independent decisions, each affecting part of the result
- The difference between a **base**, a **multiplier**, and an **adder**
- Boundary values that matter (`12` vs `13`, `64` vs `65`)
- Showing intermediate steps in the output so the final number is verifiable

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 42 | 113 |
| Age input | `readInt("Enter age", 0, 120)` | hand-rolled `while` + `try`/`catch` |
| Student flag | `readBoolean().label("Are you a student?").read()` | hand-rolled `while` loop comparing `y` / `n` |
| Day type menu | `readChoice("Day type:", "Weekday", "Weekend")` | hand-rolled menu loop + range check |
| Currency formatting | `rupees(total)` | manual `"Rs." + String.format("%.2f", total)` |

Three inputs, three validations, three messages. The `without-chirp` version's 55-line gap is almost entirely the three helper methods — the same three you wrote in Level 1, copy-pasted again.
