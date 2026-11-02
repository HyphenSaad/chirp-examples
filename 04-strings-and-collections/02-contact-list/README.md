# Contact List

Let the user build a contact list (name + phone), then offer three ways to look at it: full list sorted by name, substring search, and prefix filter.

## Requirements

1. Repeatedly read contacts. For each contact:
   - Ask for a **name**. Blank name signals "done" — exit the input loop.
   - Ask for a **phone number**. Must be non-blank; invalid entries are re-asked without exiting the loop.
2. After input is finished, print a separator and the **full list**, sorted by name (case-insensitive, alphabetical). Each row shows `Name` and `Phone`.
3. Ask for a **search query** (case-insensitive substring). Print matching contacts, or a message `"ℹ No matches."` if nothing matches.
4. Ask for a **prefix**. Print contacts whose name starts with that prefix (case-insensitive), or `"ℹ No matches."` if nothing matches.
5. Every printed list uses the aligned column format from Level 3's multiplication table — see the example.

## Example interaction

```
========================================
│ Contact List
========================================
Name (blank to finish): Saad
Phone: +92 300 1234567

Name (blank to finish): Alice
Phone: +92 321 7654321

Name (blank to finish): Bob
Phone: +92 333 1111111

Name (blank to finish):

All contacts (3):

Name   Phone
-----  ---------------
Alice  +92 321 7654321
Bob    +92 333 1111111
Saad   +92 300 1234567

Search query: sa

Matches (1):

Name  Phone
----  --------------
Saad  +92 300 1234567

Prefix: a

Matches (1):

Name   Phone
-----  ---------------
Alice  +92 321 7654321
```

## Boundary cases you must check

| Case | Expected behaviour |
|---|---|
| User enters name then blank | Name required before phone — a blank name *ends the loop*, not a single contact |
| User enters a blank phone | Re-asked — phone is required per contact |
| Zero contacts entered (blank name immediately) | Print `"ℹ No contacts."` and skip the remaining reports |
| Search query matches everything | Print all contacts |
| Search query matches nothing | Print `"ℹ No matches."` |
| Prefix matches nothing | Print `"ℹ No matches."` |
| Search query mixed case (`"AL"`) | Case-insensitive — matches `Alice` |
| Prefix is a single character | Every contact whose name starts with it |
| Duplicate names | Both kept — the program does not deduplicate |
| Empty prefix | Every contact matches, since every string starts with `""` — decide whether to allow this or reject empty input |

The last row is a real design choice. **The example above treats empty prefix as "everything matches".** You can also decide to reject empty prefix and re-ask. Both are valid — just document your choice.

## The "build, then report" pattern

This is the standard shape for **any** interactive data-collection program:

1. **Collect** — read until a sentinel, keep valid entries in a list
2. **Report** — after collection is done, offer multiple ways to look at the data

You'll use this pattern for shopping carts (build the cart, then checkout), for expense trackers (add expenses, then report), for todo lists (add tasks, then filter), and for anything else where the user provides data incrementally.

The key discipline is: **do not mix collecting with reporting.** If you try to print a "running list" after each add, you end up with a UI that scrolls off the screen and a data structure that's hard to test. Collect first, report second, always.

## Search vs filter — the difference

The two operations look similar but teach different things:

- **Search** is *substring* matching: `"sa"` matches `Saad`, `Visa`, `messa`. Uses `String.contains` (case-insensitive).
- **Filter** is *prefix* matching: `"sa"` matches only `Saad` and `Sam`, not `Visa`. Uses `String.startsWith` (case-insensitive).

Both are one-line operations with a library and three-line operations without. But the **semantic** difference matters: search is for "I half-remember the name", filter is for "show me everything starting with A". Real contact apps use both — Android's dialer has prefix filter as you type, and its search box does substring match.

## What you'll practice

- **Building a list incrementally** — the "read until sentinel" loop
- **Sorting a list of tuples** — `Comparator.comparing(row -> row[0].toLowerCase())`
- **Filtering a list with a predicate** — the core `where` operation
- **Case-insensitive comparison** — the lowercase-both-sides trick
- **Rendering to a table** — reusing the pattern from Level 3
- **Handling empty results** — a `"No matches"` message instead of an empty table

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 54 | 117 |
| Name input (blank to finish) | `readLine("Name (blank to finish)")` — check `.isEmpty()` to exit | hand-rolled `while` + `hasNextLine` |
| Phone input (required) | `readNonEmpty("Phone")` | hand-rolled `while` + `trim().isEmpty()` |
| Sorting the list | `list.sort(Comparator.comparing(row -> row[0].toLowerCase()))` | same |
| Search filter | `Filter.where(contacts, row -> Search.contains(row[0], query))` | `.stream().filter(row -> row[0].toLowerCase().contains(query.toLowerCase())).toList()` |
| Prefix filter | `Filter.where(contacts, row -> Search.startsWith(row[0], prefix))` | `.stream().filter(row -> row[0].toLowerCase().startsWith(prefix.toLowerCase())).toList()` |
| Table render | `table(rows, "Name", "Phone")` | hand-rolled `printTable` helper |

The `Search.contains` and `Search.startsWith` calls handle the lowercase-both-sides detail internally — that's a small but real cleanup. The bigger savings are still the table and the input plumbing.
