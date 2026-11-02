# Word Frequency Counter

Read a paragraph of text, count how often each word appears, and print the top 10 by frequency. Also report some overall statistics about the text.

## Requirements

1. Read one line of text from the user. Empty input (or whitespace-only) is rejected and re-asked.
2. Split the text into **words** by these rules:
   - Split on any whitespace (space, tab, newline)
   - Strip punctuation characters before comparing — `"Hello!"` and `"hello"` are the same word
   - Words are case-insensitive — `"The"` and `"the"` are the same word
   - Preserve the **original casing of the first occurrence** for display
3. Build a frequency map: word → count.
4. Print an **overall summary** with these statistics:
   - Total word count (including duplicates)
   - Unique word count (number of distinct words)
   - Longest word (by character count, first occurrence wins if ties)
5. Print a **top-10 table** with three columns: `Word`, `Count`, `Percentage`. Percentage is `count / totalWords × 100`, formatted to **one decimal place**.
6. Sort the table by count **descending**, breaking ties by word **alphabetically ascending**.
7. If the text has fewer than 10 unique words, print all of them — do not pad.

## Example interaction

```
========================================
│ Word Frequency Counter
========================================
Enter text: The quick brown fox jumps over the lazy dog. The dog sleeps.

Total words:    11
Unique words:   9
Longest word:   quick

Word    Count  Percentage
------  -----  ----------
the     3      27.3%
dog     2      18.2%
brown   1      9.1%
fox     1      9.1%
jumps   1      9.1%
lazy    1      9.1%
over    1      9.1%
quick   1      9.1%
sleeps  1      9.1%
```

Note the tie-breaking — `brown`, `fox`, `jumps`, `lazy`, `over`, `quick`, `sleeps` all have count 1, and they appear in alphabetical order. This is a **stable sort with a secondary key**, a pattern you'll reuse in every leaderboard, ranking, or "top N" problem.

## Boundary cases you must check

| Input | Expected behaviour |
|---|---|
| Empty string | Re-asked |
| Whitespace only | Re-asked |
| Single word (`"hello"`) | Total 1, unique 1, table has 1 row |
| Only punctuation (`"!!!"`) | Zero words — print stats with zeros, table empty or single blank |
| Repeated same word (`"a a a a a"`) | Total 5, unique 1, one row at 100.0% |
| Mixed case (`"The the THE"`) | All three count as `the`, displayed as `The` (first occurrence) |
| Words with punctuation (`"hello, world!"`) | Split into `hello`, `world` |
| Multi-line input | Only the first line is read (that's the contract — single line) |

## The "sorted by two keys" pattern

This is the most transferable lesson in the problem. You want the top-10 list sorted by **count descending**, with **alphabetical ascending** as the tiebreak.

```java
entries.sort(Comparator
        .comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
        .thenComparing(Map.Entry::getKey));
```

Three things to note:

1. **`comparing` takes a key extractor** — here `Map.Entry::getValue`, the count.
2. **`Comparator.reverseOrder()` inside `comparing`** — makes the primary sort descending without inverting the whole comparator.
3. **`thenComparing` for the tiebreak** — this is *only* consulted when two entries have equal counts.

If you only had the count and no tiebreak, the sort would be stable but the order of equal-count entries would depend on their original iteration order (which for `HashMap` is undefined). Adding `.thenComparing(Map.Entry::getKey)` makes the output deterministic. That matters for tests, for screenshots, and for comparing two runs.

## Percentages that don't sum to 100.0

The example above shows percentages of `27.3`, `18.2`, `9.1` × 7. Sum: `27.3 + 18.2 + 63.7 = 109.2`. Wait — that's wrong.

Check the math: `3/11 = 27.2727…%`, `2/11 = 18.1818…%`, `1/11 = 9.0909…%`. Rounded to 1 decimal: `27.3 + 18.2 + 9.1 × 7 = 27.3 + 18.2 + 63.7 = 109.2`.

**The percentages don't sum to 100 because of rounding.** Each row is rounded individually, so small rounding errors accumulate. This is normal and expected — a percentage column that sums to 100.0 exactly is *wrong* (it means someone added a "fudge factor" to force the sum, hiding real rounding).

Do not try to fix this. Do not subtract the difference from the largest row. Just round each row independently and let the sum drift by a percent or two. Every real statistics tool does this, and the drift is a useful reminder that percentages are approximations.

## What you'll practice

- **Splitting text with punctuation and case** — `Text.removePunctuation` or a manual regex
- **Building a frequency map** — `map.merge(word, 1, Integer::sum)` or a group-and-count
- **Sorting with a two-level comparator** — the "primary descending, secondary ascending" pattern
- **Top-N slicing** — take the first 10 after sorting, or all if fewer
- **Formatted percentage output** — `%` literal in the format string
- **Empty-collection handling** — the table renders nothing if the map is empty

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 57 | 102 |
| Read text | `readNonEmpty("Enter text")` | hand-rolled `while` + `trim().isEmpty()` |
| Strip punctuation | `Text.removePunctuation(word)` | `word.replaceAll("[\\p{Punct}]", "")` |
| Split into words | same — `.toLowerCase().split("\\s+")` | same |
| Frequency map | same — `map.merge(word, 1, Integer::sum)` | same |
| Sort with two keys | same — `Comparator.comparing…thenComparing…` | same |
| Table render | `table(rows, "Word", "Count", "Percentage")` | hand-rolled `printTable` helper (20 lines) |
| Percentage format | `percent(value, 1)` | `String.format("%.1f%%", value)` |

The core algorithm — splitting, counting, sorting — is identical in both. The Chirp version saves the input plumbing (10 lines) and the table rendering (20 lines) and reads slightly better at the format calls. That's an honest ~45% reduction, most of which is table rendering.
