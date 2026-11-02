# Random Quote Fetcher

Fetch a random quote from a public HTTP API, parse the JSON response, and display the quote and author. Offer a menu to fetch another.

## Requirements

1. On start, fetch one quote from **`https://dummyjson.com/quotes/random`**. The response has the shape:
   ```json
   {"id": 42, "quote": "The only way to do great work is to love what you do.", "author": "Steve Jobs"}
   ```
2. Print the quote like this:
   ```
   ℹ "The only way to do great work is to love what you do."
     — Steve Jobs
   ```
   Print the request time in milliseconds below the quote, e.g. `"ℹ Fetched in 340 ms"`.
3. Show a menu with two options:
   ```
   Action:
     1) Fetch another quote
     2) Exit
   ```
4. **Fetch another** — issue a new request, print the new quote, stay in the menu.
5. **Exit** — print `"ℹ Goodbye."` and exit.
6. **On network failure** — if the request times out, DNS fails, or the server returns a non-2xx status, print `"⚠ Could not fetch quote — using a local fallback."` and display a hardcoded quote:
   ```
   "The best way to predict the future is to invent it."
   — Alan Kay
   ```
7. **On JSON parse failure** — if the response is not valid JSON or is missing `quote` / `author`, treat it as a network failure (fallback path).

## Example interaction — online

```
========================================
│ Random Quote Fetcher
========================================

ℹ "The only way to do great work is to love what you do."
  — Steve Jobs
ℹ Fetched in 342 ms

Action:
  1) Fetch another quote
  2) Exit
Your choice [1-2]: 1

ℹ "In the middle of difficulty lies opportunity."
  — Albert Einstein
ℹ Fetched in 218 ms

Action:
  1) Fetch another quote
  2) Exit
Your choice [1-2]: 2

ℹ Goodbye.
```

## Example interaction — offline

```
ℹ "The only way..." — the first attempt fails
⚠ Could not fetch quote — using a local fallback.

ℹ "The best way to predict the future is to invent it."
  — Alan Kay
```

## Boundary cases you must verify

| Case | Expected behaviour |
|---|---|
| Network available, API healthy | Quote fetched, elapsed time shown |
| Network available, API rate-limits (429) | Fallback path taken — treated as failure |
| No network | Fallback path taken within ~2–10 seconds |
| Firewall blocks DNS | Fallback path taken quickly |
| Server returns malformed JSON | Fallback path taken |
| Server returns JSON missing `author` field | Fallback path taken |
| Server returns JSON missing `quote` field | Fallback path taken |
| User fetches 10 quotes in a row | Each shows the request time — some vary widely |
| Response takes >30s | Timeout fires, fallback path taken |

## The response-time variation lesson

Look at the two example timings above: **342 ms** and **218 ms**. Same endpoint, consecutive requests, over 100 ms apart. That's normal:

- DNS lookup — cached after the first request, so subsequent requests are faster
- TCP handshake — variable depending on routing
- Server load — the API's response time varies
- Geographic distance — you're talking to a server that may be 5000 km away

Real production applications add **jitter** and **backoff** to retries (Chirp's `Retry` does this by default) *because* the network timing varies. A retry that fires at exactly the same interval as the failed request will hit the same server at the same bad moment. Jitter spreads the retries out.

The `Fetched in N ms` line is not decoration — it's the student's first glimpse into network latency, and it's the number they should look at every time they wonder why a program feels slow.

## The "manual JSON parsing" fragility

The `without-chirp` version parses the JSON body by hand:

```java
String quote = extractString(body, "\"quote\":");
String author = extractString(body, "\"author\":");
```

where `extractString` finds the substring between the key and the next `"`:

```java
private static String extractString(String body, String key) {
    int keyIndex = body.indexOf(key);
    if (keyIndex < 0) return "";
    int startQuote = body.indexOf('"', keyIndex + key.length());
    int endQuote = body.indexOf('"', startQuote + 1);
    return body.substring(startQuote + 1, endQuote);
}
```

That works for the exact response format `{"id":42,"quote":"...","author":"..."}`. It breaks for:

- `{ "quote" : "..." }` — space between key and colon
- `{"quote":"..."}` nested inside another object
- Values containing escaped quotes: `"quote": "He said \"hi\""`
- Unicode escapes: `"quote": "caf\u00e9"`
- Multi-line pretty-printed JSON

That's six ways to break in a single field. Manual JSON parsing is fine for a demo of a fixed API — but a library that parses the grammar correctly costs one call. That's a lesson worth building into a demo.

## GUI extension

Build a JavaFX window that:
- Shows the quote in a large `Label` (wrapped, centred)
- Shows the author below in italic
- Shows the fetch time as a small muted label
- Has a "Fetch another" button (primary) and an "Exit" button
- Disables the button while a request is in flight (`Http.get(url).sendAsync()` and `Ui.runAsync`)

Same URL, same JSON parsing, different driver.

## What you'll practice

- HTTP GET requests with a timeout
- Parsing a JSON response
- Detecting and handling network failures
- Timing a network call (`System.nanoTime()` around the request)
- Designing a fallback path so the demo works offline

## With vs without Chirp

| Concern | With Chirp | Without Chirp |
|---|---|---|
| Line count | 60 | 127 |
| Make the request | `Http.fetch(url)` | `HttpClient.newHttpClient()` + `HttpRequest.newBuilder()` + `client.send(...)` (12 lines) |
| Parse JSON | `response.asObject().getString("quote")` | `extractString(body, "\"quote\":")` (18 lines including helper) |
| Detect failure | `response.hasTransportError()` | `try`/`catch` for `IOException` and `InterruptedException`, plus `if (statusCode != 200)` |
| Elapsed time | `response.durationMillis()` | `System.nanoTime()` before and after |
| Retry | `Http.get(url).sendWithRetry(3)` | manual loop + `Thread.sleep` + `try`/`catch` |
| Fallback | `if (response.hasTransportError()) fallback()` | 5 separate failure modes to detect |

The without-chirp version is not just longer — it is *harder to get right*. Missing the `InterruptedException` catch, or checking `statusCode` before `body`, or forgetting the timeout — each of these is a bug the student would ship. The Chirp version reduces five failure modes to one check.

---
