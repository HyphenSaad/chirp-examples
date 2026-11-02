# Level 4 — Strings & Collections

Programs that work with data that grows at runtime. Everything in this level is about a list or a map that is built up, transformed, and printed.

## What this level covers

- **Splitting text into words** — handling punctuation, casing, and empty tokens
- **Counting occurrences** — `Map<String, Integer>` for frequency counting
- **Building lists from input** — read until a sentinel, keep only valid entries
- **Filtering** — keep or reject based on a predicate
- **Grouping** — restructure a list of records into a map of groups
- **Aggregating** — compute per-group counts, sums, averages
- **Sorting** — alphabetical, by numeric value, with tie-breaking
- **Tabular output** — column alignment for irregular data

## What it doesn't cover yet

- **User-written classes with methods** — that's Level 5. In this level, "records" are `String[]` tuples or simple records.
- **Files and persistence** — that's Level 6.
- **GUI** — that's Level 5.

## The three problems

| Problem | Data structure | What you'll learn | With Chirp | Without Chirp |
|---|---|---|---|---|
| [01-word-frequency-counter](01-word-frequency-counter/) | `Map<String, Integer>` | Split, count, sort, top-N | ~50 lines | ~95 lines |
| [02-contact-list](02-contact-list/) | `List<String[]>` | Build, sort, search, filter, table | ~75 lines | ~140 lines |
| [03-grade-book](03-grade-book/) | `List<Grade>` + `Map` groupings | Group, aggregate, sort, report | ~90 lines | ~150 lines |

## An honest calibration note

Levels 1–3 were about **input plumbing** — the same five-line retry loop appeared in every problem, and Chirp replaced it with one call. Level 4 is different. The *data operations* in this level — filtering, grouping, sorting, aggregating — are standard Java streams and collections. Both versions use them.

The savings in Level 4 come from three places:

1. **Table rendering** — `table(rows, headers)` computes column widths and pads cells; doing it by hand is 20+ lines and identical in every problem.
2. **Text utilities** — `Text.removePunctuation`, `Search.contains`, `Search.startsWith` handle the small edge cases (case-insensitivity, null handling) that are easy to get subtly wrong.
3. **Grouping shorthand** — `Filter.groupBy(items, keyFn)` is a one-liner; `stream.collect(Collectors.groupingBy(keyFn))` is also a one-liner but more verbose.

That's a smaller gap than in Level 1. If the with-chirp version is only 30% shorter, that's a fair representation of the value — Chirp is not trying to hide the algorithm, only the boilerplate.

## The three "gotchas" that trip up beginners

1. **Empty tokens after splitting** — `"hello   world".split("\\s+")` gives `["hello", "world"]` (no empties), but `"hello world!".split(" ")` after punctuation removal may give `["hello", "world", ""]`. Filter out blanks before processing.
2. **Case sensitivity in searches** — `"Saad".startsWith("sa")` is `false`. Case-insensitive comparison needs both sides lowercased (or a case-insensitive matcher). Level 4's examples make this explicit every time.
3. **Mutable tuples in lists** — `String[]` is mutable. If you add it to a list and later mutate it, the list's contents change too. Level 4's examples don't mutate after insertion, but that's a discipline to build.
