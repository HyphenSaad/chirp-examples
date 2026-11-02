# Level 7 — Talking to the World

The first level where the program leaves the machine. Every problem makes a network request and handles the fact that the network can fail.

## What this level covers

- **HTTP GET requests** — build a request, send it, parse the response
- **JSON API parsing** — read structured responses into Java objects
- **Query parameters** — encode data into the URL
- **Error handling for networks** — timeouts, DNS failures, non-2xx status codes
- **Retries** — when to retry, when not to, and how long to wait
- **Async vs sync** — why every network call has to think about both
- **Email** — SMTP validation, MIME construction, dry-run previews

## What this level does NOT cover

- **Authentication** — no API keys, no OAuth. Every problem here hits public endpoints.
- **WebSockets / streaming** — Level 7 is request/response only.
- **Serialization frameworks** — Jackson, Gson, Moshi. Chirp has its own JSON; the without-chirp versions use manual parsing.
- **GUI** — that's Level 8.

## The three problems

| Problem | Endpoint | With Chirp | Without Chirp |
|---|---|---|---|
| [01-random-quote-fetcher](01-random-quote-fetcher/) | `dummyjson.com/quotes/random` | ~55 lines | ~95 lines |
| [02-weather-lookup](02-weather-lookup/) | `api.open-meteo.com` | ~90 lines | ~155 lines |
| [03-email-notifier](03-email-notifier/) | SMTP (dry-run) | ~95 lines | ~140 lines |

## Why these three problems

Each problem exercises a different shape of network interaction:

- **Random Quote** — a single GET with a small JSON response. The "hello world" of HTTP.
- **Weather** — a GET with query parameters and a larger, nested JSON response. Real-world shape.
- **Email** — a completely different protocol, showing that not all network code is HTTP.

## The network failure lesson

Every demo here includes an **offline fallback** so the example still runs when the network is unavailable. The fallback is a design choice, not a workaround:

```java
if (response.hasTransportError()) {
    warn("Could not reach the API — using a local fallback.");
    return FALLBACK;
}
```

Why include it?

1. **Teaching environments often have unreliable network.** A demo that crashes offline is unusable in a classroom.
2. **The failure path is real code.** Production systems have fallbacks for the same reason — the network *will* fail sometimes.
3. **The student learns the pattern.** `if (error) fallback` is the shape of every robust network call, whether the fallback is a cached response, a default value, or a retry.

The without-chirp versions also have fallbacks, but the check is more verbose — you have to catch `IOException`, check status codes, and handle `InterruptedException` separately.

## The retry lesson

The Random Quote Fetcher uses Chirp's `Http.get(url).sendWithRetry(3)` — three attempts with 500ms backoff. That's appropriate for a read-only request to a public API, where the retry is cheap and safe.

The Weather Lookup does **not** retry — a slow weather API is better shown as "slow" than masked by a retry that doubles the wait.

The Email Notifier **does** retry, but only for the transport-level failures (`IOException`), not for SMTP rejections (5xx codes) — retrying a rejected message won't change the rejection.

Choosing when to retry is the lesson. A blanket "always retry" is wrong; a blanket "never retry" is wrong. The right answer depends on the failure mode.

## The three "gotchas" that trip up beginners

1. **Blocking the main thread.** All of these calls are synchronous — the program waits for the response. That's fine for a demo, but a GUI application would freeze during the wait. Level 8 covers `Http.get(url).sendAsync()` and `Ui.runAsync`.
2. **Ignoring `InterruptedException`.** `HttpClient.send()` throws `InterruptedException` if the calling thread is interrupted. Every without-chirp version has to catch it and either re-interrupt the thread or handle the cancellation. The with-chirp version catches it internally and returns an error response.
3. **Parsing JSON by hand.** The without-chirp versions do manual string parsing of the response — `body.substring(body.indexOf("\"quote\":\"") + 9, ...)` — which is fragile: any change in whitespace or key order breaks it. Chirp's `Json.parse` handles the grammar correctly.
