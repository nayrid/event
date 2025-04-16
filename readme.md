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


| Benchmark                                        | subscriberCount | Mode  | Cnt | Score        | Error         | Units |
|--------------------------------------------------|-----------------|-------|-----|--------------|---------------|-------|
| EventBenchmark.benchmarkBaselineEventPublish     | 1               | thrpt | 25  | 17615247.430 | ±1593356.141  | ops/s |
| EventBenchmark.benchmarkBaselineEventPublish     | 10              | thrpt | 25  | 14667626.669 | ±806933.598   | ops/s |
| EventBenchmark.benchmarkBaselineEventPublish     | 100             | thrpt | 25  | 7707699.186  | ±102897.107   | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish | 1               | thrpt | 25  | 17188573.251 | ±554891.187   | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish | 10              | thrpt | 25  | 8176651.842  | ±86845.513    | ops/s |
| EventBenchmark.benchmarkCancellationEventPublish | 100             | thrpt | 25  | 1465157.276  | ±11636.695    | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish   | 1               | thrpt | 25  | 75325007.271 | ±1667600.885  | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish   | 10              | thrpt | 25  | 61745343.944 | ±1185301.021  | ops/s |
| EventBenchmark.benchmarkConcurrentEventPublish   | 100             | thrpt | 25  | 29693594.523 | ±1385912.681  | ops/s |
| EventBenchmark.benchmarkSubscribeUnsubscribe     | 1               | avgt  | 25  | 264.398      | ±3.294        | ns/op |
| EventBenchmark.benchmarkSubscribeUnsubscribe     | 10              | avgt  | 25  | 266.238      | ±5.027        | ns/op |
| EventBenchmark.benchmarkSubscribeUnsubscribe     | 100             | avgt  | 25  | 265.728      | ±4.550        | ns/op |

Benchmarks were ran on an `AMD Ryzen 5 3600X (12) @ 3.80 GHz` at commit [2b41d6d](https://github.com/nayrid/event/commit/2b41d6d3b4d917331be9521235ef061554965d11).
