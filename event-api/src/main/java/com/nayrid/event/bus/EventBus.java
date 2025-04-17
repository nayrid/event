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
package com.nayrid.event.bus;

import com.nayrid.event.CancellableEvent;
import com.nayrid.event.Event;
import com.nayrid.event.bus.AbstractEventBus.EventRegistrationImpl;
import com.nayrid.event.bus.config.EventBusConfig;
import com.nayrid.event.bus.subscription.EventSubscriber;
import com.nayrid.event.bus.subscription.EventSubscription;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.examination.Examinable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * An event bus.
 *
 * @param <C> the config type
 * @since 1.0.0
 */
public interface EventBus<C extends EventBusConfig> extends Examinable {

    /**
     * Gets or creates an event registration for the given key and event type.
     *
     * @param eventType the event class
     * @param <T>       the event type
     * @return the corresponding event registration
     * @since 1.0.0
     */
    <T extends Event> EventRegistration<T> getOrCreateRegistration(Class<T> eventType);

    /**
     * Publishes an event to all subscribers.
     *
     * @param event the event instance
     * @param <T>   the event type
     * @since 1.0.0
     */
    <T extends Event> void publish(T event);

    /**
     * Publishes a cancellable event to all subscribers and returns {@code true} if the event was
     * <strong><u>not</u></strong> cancelled.
     *
     * @param event the cancellable event
     * @param <T>   the event type
     * @return {@code true} if the event was <strong><u>not</u></strong> cancelled
     * @since 1.0.0
     */
    default <T extends CancellableEvent> boolean publish(final T event) {
        this.publish((Event) event);
        return !event.cancelled();
    }

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType        the event class
     * @param subscriber       the event subscriber
     * @param priority         the subscription priority
     * @param acceptsCancelled if the subscription should accept cancelled events
     * @param <T>              the event type
     * @since 1.0.0
     */
    <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber,
        int priority, boolean acceptsCancelled);

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param priority   the subscription priority
     * @param <T>        the event type
     * @since 1.0.0
     */
    <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber,
        int priority);

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param <T>        the event type
     * @since 1.0.0
     */
    <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber);

    /**
     * Unsubscribes from an event using its event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param <T>        the event type
     * @since 1.0.0
     */
    <T extends Event> void unsubscribe(Class<T> eventType, EventSubscriber<T> subscriber);

    /**
     * Gets the event registration by key.
     *
     * @param key the event key
     * @param <T> the event type
     * @return the registration, or null if none exists
     * @since 1.0.0
     */
    <T extends Event> @Nullable EventRegistration<T> get(Key key);

    /**
     * Gets the event registration by event type.
     *
     * @param eventType the event class
     * @param <T>       the event type
     * @return the registration, or null if none exists
     * @since 1.0.0
     */
    <T extends Event> @Nullable EventRegistration<T> get(Class<T> eventType);

    /**
     * Returns the set of all registered event keys.
     *
     * @return set of event keys
     * @since 1.0.0
     */
    Set<Key> keySet();

    /**
     * Gets the event bus' config.
     *
     * @return the config
     * @since 1.0.0
     */
    C config();

    /**
     * A registration for a specific event.
     *
     * @param <T> the event type
     * @since 1.0.0
     */
    @NullMarked
    interface EventRegistration<T extends Event> extends Keyed, Examinable {

        /**
         * Creates an {@link EventRegistration}.
         *
         * @param key       the event key
         * @param eventType the event class
         * @param <T>       the event type
         * @return a new event registration
         * @since 1.0.0
         */
        static <T extends Event> EventRegistration<T> create(final Key key,
            final Class<T> eventType) {
            return new EventRegistrationImpl<>(key, eventType);
        }

        /**
         * Returns the event's type.
         *
         * @return the event class
         * @since 1.0.0
         */
        Class<T> eventType();

        /**
         * Returns a snapshot of the subscribers.
         *
         * @return an unmodifiable list of subscribers
         * @since 1.0.0
         */
        List<EventSubscription<T>> subscribers();

        /**
         * Subscribes with the given subscription.
         *
         * @param subscription the subscription to add
         * @since 1.0.0
         */
        void subscribe(EventSubscription<T> subscription);

        /**
         * Unsubscribes the given subscriber.
         *
         * @param subscriber the subscriber to remove
         * @since 1.0.0
         */
        void unsubscribe(EventSubscriber<T> subscriber);

    }

}
