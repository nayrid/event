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
import com.nayrid.event.bus.SimpleEventBus;
import com.nayrid.event.bus.config.EventBusConfig;
import com.nayrid.event.bus.subscription.EventSubscriber;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.key.KeyPattern.Namespace;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@NullMarked
public class SimpleEventBusTests {

    public static final @Namespace String NAMESPACE = "nayrid";
    private SimpleEventBus bus;

    @BeforeEach
    public void setup() {
        this.bus = SimpleEventBus.create(EventBusConfig.eventBusConfig().acceptsCancelled(true).build());
    }

    @Test
    public void testInitialKeySetAndSubscriptionRegistration() {
        final EventSubscriber<IntegerEvent> unsubscribedHandler = event -> fail(
            "This handler should not be invoked");

        assertEquals(0, this.bus.keySet().size(), "EventBus should be empty initially");

        this.bus.subscribe(IntegerEvent.class, unsubscribedHandler);
        assertEquals(1, this.bus.keySet().size(), "EventBus should have one registered event");

        this.bus.unsubscribe(IntegerEvent.class, unsubscribedHandler);
    }

    @Test
    public void testSubscriberPrioritiesAndCancellationHandling() {
        this.bus.subscribe(IntegerEvent.class, event -> assertEquals(0, event.get(),
            "Integer value should be unchanged at priority 0"), 0);

        this.bus.subscribe(IntegerEvent.class, IntegerEvent::increment, 1);

        this.bus.subscribe(IntegerEvent.class, event -> assertEquals(1, event.get(),
            "Integer value should have been incremented by priority 1"), 2);

        this.bus.subscribe(IntegerEvent.class, event -> event.cancelled(true), 3);

        this.bus.subscribe(IntegerEvent.class,
            event -> fail("Event is cancelled, handler with priority 4 should not be invoked!"), 4,
            false);

        final AtomicBoolean cancelledSubscriptionExecuted = new AtomicBoolean(false);
        this.bus.subscribe(IntegerEvent.class, event -> cancelledSubscriptionExecuted.set(true), 5,
            true);

        this.bus.publish(new IntegerEvent(0));

        assertTrue(cancelledSubscriptionExecuted.get(),
            "Subscription that accepts cancelled events should have been executed");
    }

    @Test
    public void testCancelledEventReturnMethod() {
        final IntegerEvent cancelledEvent = new IntegerEvent(0);
        cancelledEvent.cancelled(true);

        assertFalse(this.bus.publish(cancelledEvent));
        assertTrue(this.bus.publish(new IntegerEvent(0)));
    }

    @Test
    public void testGlobalEventBusPublishing() {
        assertDoesNotThrow(() -> new IntegerEvent(1).publish(),
            "Publishing the event via its publish() method should not throw an exception");
    }

    @AnnoKey(namespace = SimpleEventBusTests.NAMESPACE, value = "integer")
    public static final class IntegerEvent implements CancellableEvent {

        private int integer;
        private boolean cancelled;

        public IntegerEvent(final int integer) {
            this.integer = integer;
            this.cancelled = false;
        }

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void cancelled(final boolean cancelled) {
            this.cancelled = cancelled;
        }

        public int get() {
            return this.integer;
        }

        public void increment() {
            this.integer++;
        }

    }

}
