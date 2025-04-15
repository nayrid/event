# event

An events library. Each event has a key, which makes it more intuitive for end users to interact
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
compile("com.nayrid.event:event-api:(the latest version)")
```
