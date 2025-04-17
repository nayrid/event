# event

An events library made with the Minecraft modding ecosystem in mind.

Each event has a [key](https://jd.advntr.dev/key/latest/net/kyori/adventure/key/Key.html), which makes it more intuitive for end users to interact
with events in things like commands or configuration files.

## Gradle

First include our repository:

```kotlin
maven {
    url = uri("https://repo.nayrid.com/public")
}
```

And then use this library (change `(the latest version)` to the latest version):

```kotlin
implementation("com.nayrid.event:event-api:(the latest version)")
```

## Benchmarks

> [!NOTE]
> These benchmarks are **not** provided with the intention of leading you to believe this is the *fastest* event library out there, just to show it's performant.

#### Update 1 Benchmark Results

| Benchmark                                            | subscriberCount | Mode  | Cnt | Score            | Error            | Units |
|------------------------------------------------------|:---------------:|:-----:|:---:|:----------------:|:----------------:|:-----:|
| EventBenchmark.benchmarkBaselineEventPublish         |       1         | thrpt | 25  | 51,392,326.896   | ± 277,965.483    | ops/s |
| EventBenchmark.benchmarkBaselineEventPublish         |      10         | thrpt | 25  | 33,798,162.123   | ± 715,198.909    | ops/s |
| EventBenchmark.benchmarkBaselineEventPublish         |      100        | thrpt | 25  | 11,001,671.906   | ± 82,515.552     | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish     |       1         | thrpt | 25  | 53,373,607.112   | ± 254,794.504    | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish     |      10         | thrpt | 25  | 12,781,271.023   | ± 79,251.440     | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish     |      100        | thrpt | 25  |  1,729,370.344   | ± 8,333.286      | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish       |       1         | thrpt | 25  | 197,034,216.432  | ± 2,554,115.648  | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish       |      10         | thrpt | 25  | 130,922,888.571  | ± 2,005,071.726  | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish       |      100        | thrpt | 25  |  42,853,366.206  | ± 265,138.816    | ops/s |
| AnnoKeyBenchmark.benchmarkAnnotationUtilKey          |      N/A        | avgt  | 25  |       4.038      | ± 0.041          | ns/op |
| EventBenchmark.benchmarkSubscribeUnsubscribe         |       1         | avgt  | 25  |     205.447      | ± 3.347          | ns/op |
| EventBenchmark.benchmarkSubscribeUnsubscribe         |      10         | avgt  | 25  |     205.864      | ± 2.651          | ns/op |
| EventBenchmark.benchmarkSubscribeUnsubscribe         |      100        | avgt  | 25  |     209.395      | ± 2.189          | ns/op |

Benchmarks were ran on an `AMD Ryzen 5 3600X (12) @ 3.80 GHz` on OpenJDK (Coretto) 21 at commit [dcf305e](https://github.com/nayrid/event/commit/dcf305ebebca104931de7d6104620c8159797d19).
