/*
 * MIT License
 *
 * Copyright (c) nayrid.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nayrid.event;

import com.nayrid.event.annotation.AnnoKey;
import com.nayrid.event.bus.EventBus;
import com.nayrid.event.bus.config.EventBusConfig;
import com.nayrid.event.bus.subscription.EventSubscriber;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NullMarked;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

@SuppressWarnings("NotNullFieldNotInitialized")
@NullMarked
@State(Scope.Benchmark)
public class EventBenchmark {

    @Param({"1", "10", "100"}) private int subscriberCount;

    private EventBus baselineBus;
    private EventBus cancellingBus;

    @Setup(Level.Iteration)
    public void setup() {
        this.baselineBus = EventBus.create(
            EventBusConfig.eventBusConfig().acceptsCancelled(true).build());
        for (int i = 0; i < subscriberCount; i++) {
            this.baselineBus.subscribe(CountingEvent.class, event -> {
            });
        }

        this.cancellingBus = EventBus.create(
            EventBusConfig.eventBusConfig().acceptsCancelled(true).build());
        for (int i = 0; i < subscriberCount / 2; i++) {
            this.cancellingBus.subscribe(CancellationEvent.class, event -> {
            });
        }

        this.cancellingBus.subscribe(CancellationEvent.class, event -> event.cancelled(true), 1);
        for (int i = 0; i < this.subscriberCount / 2; i++) {
            this.cancellingBus.subscribe(CancellationEvent.class, event -> {
            }, 2, true);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void benchmarkBaselineEventPublish() {
        this.baselineBus.publish(new CountingEvent());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void benchmarkCancellationEventPublish() {
        this.cancellingBus.publish(new CancellationEvent());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkSubscribeUnsubscribe(final SubscribeUnsubscribeState state) {
        final EventSubscriber<CountingEvent> subscriber = event -> {
        };
        state.bus.subscribe(CountingEvent.class, subscriber);
        state.bus.unsubscribe(CountingEvent.class, subscriber);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Threads(4)
    public void benchmarkConcurrentEventPublish() {
        this.baselineBus.publish(new CountingEvent());
    }

    @State(Scope.Benchmark)
    public static class SubscribeUnsubscribeState {

        EventBus bus;

        @Setup(Level.Invocation)
        public void setup() {
            this.bus = EventBus.create(EventBusConfig.eventBusConfig().build());
        }

    }

    @AnnoKey(namespace = "benchmark", value = "counting")
    public static final class CountingEvent implements Event {

    }

    @AnnoKey(namespace = "benchmark", value = "cancellation")
    public static final class CancellationEvent implements CancellableEvent {

        private boolean cancelled = false;

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void cancelled(final boolean cancelled) {
            this.cancelled = cancelled;
        }

    }

}
