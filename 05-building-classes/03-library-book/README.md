# Library Book

Build a `Book` class that models the checkout / return cycle of a library book, including overdue-fine calculation. Drive it from a menu.

## Requirements — the class

Design a class `Book` with:

1. **Private fields** — `String title`, `String author`, `String isbn`, `String checkedOutBy`, `LocalDate dueDate`.
2. **Constructor** `Book(String title, String author, String isbn)`:
   - All three must be non-null and non-blank → throw `IllegalArgumentException` otherwise
   - The book starts **available** (`checkedOutBy` and `dueDate` are both `null`)
3. **State transition methods:**
   - `boolean checkout(String member, LocalDate dueDate)`
     - Returns `false` if the book is already checked out
     - Returns `false` if `member` is null/blank or `dueDate` is null or in the past
     - Sets `checkedOutBy` and `dueDate`, returns `true`
   - `boolean returnBook()`
     - Returns `false` if the book is not checked out
     - Clears `checkedOutBy` and `dueDate`, returns `true`
4. **Query methods:**
   - `boolean isAvailable()` — `true` when `checkedOutBy` is `null`
   - `String title()`, `String author()`, `String isbn()`, `String checkedOutBy()`, `LocalDate dueDate()`
   - `long daysOverdue(LocalDate today)` — returns 0 if not overdue or not checked out
   - `double fineInRupees(LocalDate today)` — `10` rupees per day overdue, `0` if not overdue
5. **`toString()`** — a one-line summary: `"Title by Author (ISBN)"`

## Requirements — the demo

1. Create one `Book` with:
   - Title: `"Clean Code"`
   - Author: `"Robert C. Martin"`
   - ISBN: `"978-0132350884"`
2. Repeatedly show a menu:
   ```
   Action:
     1) Checkout
     2) Return
     3) View status
     4) Query overdue
     5) Exit
   ```
3. **Checkout** — prompt for member name and due date (`yyyy-mm-dd`), call `checkout()`, print success or `"✗ Already checked out."`
4. **Return** — call `returnBook()`, print success or `"✗ Book is not checked out."`
5. **View status** — print `"ℹ Available"` or `"ℹ Checked out by NAME (due DATE)"`. Include the title on the next line.
6. **Query overdue** — prompt for a "today" date, call `daysOverdue()` and `fineInRupees()`, print either `"ℹ Not overdue."` or `"⚠ Overdue by N days — fine Rs.X"`
7. **Exit** — print `"ℹ Goodbye."` and exit.

## Example interaction

```
========================================
│ Library Book
========================================
Clean Code by Robert C. Martin (978-0132350884)

Action:
  1) Checkout
  2) Return
  3) View status
  4) Query overdue
  5) Exit
Your choice [1-5]: 1
Member name: Saad
Due date (yyyy-mm-dd): 2026-11-10

✓ Checked out to Saad — due 2026-11-10

Action:
  1) Checkout
  2) Return
  3) View status
  4) Query overdue
  5) Exit
Your choice [1-5]: 4
Today's date (yyyy-mm-dd): 2026-11-15

⚠ Overdue by 5 days — fine Rs.50.00
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Checkout when already checked out | Returns `false`, printed as `"✗ Already checked out."` |
| Checkout with blank member | Returns `false` |
| Checkout with due date in the past | Returns `false` |
| Checkout with valid member and future due date | Returns `true`, state changes |
| Return when not checked out | Returns `false`, printed as `"✗ Book is not checked out."` |
| Return after checkout | Returns `true`, state clears |
| `daysOverdue` on available book | Returns `0` |
| `daysOverdue` when due date is tomorrow | Returns `0` |
| `daysOverdue` on due date | Returns `0` — not overdue until the day *after* |
| `daysOverdue` one day after due | Returns `1` |
| `daysOverdue` 5 days after due | Returns `5`, fine `Rs.50.00` |
| Fine on available book | `Rs.0.00` |

## The "state machine" pattern

A book has two states, and every method respects the transitions:

```
           checkout()                returnBook()
Available ──────────────► Borrowed ──────────────► Available
    │                                                  │
    │  checkout() returns false                        │
    │  (already in this state)                         │
    └──────────────────────────────────────────────────┘
```

Every state-transition method has one job: **move from the current state to a new one if the transition is legal, otherwise refuse**. There's no "half-checked-out" state, no "checkout was in progress" — the fields always reflect one of the two valid configurations:

- **Available** → `checkedOutBy = null`, `dueDate = null`
- **Borrowed** → `checkedOutBy = "Saad"`, `dueDate = <a specific date>`

That's a class **invariant**: at any moment when you can observe the object (i.e., between public method calls), one of those two configurations must hold. The `isAvailable()` method reads it; both transition methods preserve it.

There's a subtler lesson here too: `checkout()` returning `false` is not an error — it's the model saying "you can't transition from Borrowed to Borrowed via checkout". That's the same distinction we made in the bank-account example. The model guards its invariants; the view decides how to present the refusal.

## Fine calculation — the "day after" rule

`daysOverdue` uses `ChronoUnit.DAYS.between(dueDate, today)` — which is **0 on the due date itself**, positive the day after. That matches real library behaviour: on the due date you're not overdue yet; the day after, you are.

If your implementation returns `1` on the due date, your fine is one day early. If it returns `-1`, your fine calculation is off by a whole day. Write the boundary table before the code — this is the kind of off-by-one that hides for weeks.

The same pattern applies to any "how many days late?" logic: rents, expirations, subscriptions, coupon expiry. Getting the boundary rule right once means getting it right everywhere.

## GUI extension

Build a JavaFX window that:
- Shows the book title, author, and ISBN at the top
- Shows a status badge: green "Available" or red "Borrowed by NAME"
- Has a Checkout button (opens dialog for name + due date), a Return button, and an Overdue button (opens a date picker, shows a fine dialog)
- Buttons enable/disable based on the book's state

Same `Book` model, different driver.

## What you'll practice

- **State machine design** — two states, transitions guarded by return values
- **Class invariants** — one of two configurations must always hold
- **Date arithmetic** — `LocalDate`, `ChronoUnit.DAYS.between`, why off-by-one at the boundary matters
- **Boolean-returning transitions** — the model signals refusal, the view presents it
- **String formatting of a state** — `toString()` as a one-line summary

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 125 | 198 |
| Model (`Book`) | **identical** — pure Java | **identical** |
| Menu selection | `readChoice("Action:", ...)` | hand-rolled menu loop |
| Date input | `readDate("Due date")` | hand-rolled `while` + `LocalDate.parse` + `try`/`catch` |
| Currency | `rupees(amount)` | `"Rs." + String.format("%,.2f", amount)` |
| Status output | `info(...)`, `warn(...)`, `error(...)` | manual `System.out.println("ℹ " + ...)` |

The `readDate` helper is where this example earns its Chirp pay — parsing a date with the correct pattern, retrying on invalid input, and handling the `yyyy-mm-dd` format is 15 lines hand-rolled. One call with Chirp.
