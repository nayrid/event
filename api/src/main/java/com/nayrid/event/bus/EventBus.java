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

import com.nayrid.common.examine.AbstractExaminable;
import com.nayrid.common.examine.reflect.Examine;
import com.nayrid.common.examine.util.StringUtils;
import com.nayrid.event.CancellableEvent;
import com.nayrid.event.Event;
import com.nayrid.event.annotation.AnnotationUtil;
import com.nayrid.event.bus.config.EventBusConfig;
import com.nayrid.event.bus.subscription.EventSubscriber;
import com.nayrid.event.bus.subscription.EventSubscription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.nayrid.common.Validate.nonNull;

/**
 * A thread-safe event bus.
 *
 * @since 1.0.0
 */
@NullMarked
public final class EventBus implements Examinable {

    private static final AtomicReference<EventBus> GLOBAL_BUS = new AtomicReference<>(create(
        EventBusConfig.eventBusConfig().build()));

    private final EventBusConfig config;
    private final ConcurrentHashMap<Key, EventRegistration<?>> registrationsByKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends Event>, EventRegistration<?>> registrationsByClass = new ConcurrentHashMap<>();

    EventBus(final EventBusConfig config) {
        this.config = config;
    }

    /**
     * Gets the global {@link EventBus}.
     *
     * @return the global event bus
     * @since 1.0.0
     */
    public static EventBus global() {
        return GLOBAL_BUS.get();
    }

    /**
     * Creates a new {@link EventBus}.
     *
     * @param config the configuration
     * @return an event bus
     * @since 1.0.0
     */
    public static EventBus create(final EventBusConfig config) {
        return new EventBus(config);
    }

    /**
     * Gets or creates an event registration for the given key and event type.
     *
     * @param eventType the event class
     * @param <T>       the event type
     * @return the corresponding event registration
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> EventRegistration<T> getOrCreateRegistration(final Class<T> eventType) {
        final Key key = AnnotationUtil.key(eventType).orElseThrow(() -> new IllegalStateException("Event %s is not annotated with AnnoKey".formatted(eventType.getCanonicalName())));

        return (EventRegistration<T>) registrationsByKey.computeIfAbsent(key, k -> {
            EventRegistration<T> reg = EventRegistration.create(key, eventType);
            registrationsByClass.putIfAbsent(eventType, reg);
            return reg;
        });
    }

    /**
     * Publishes an event to all subscribers.
     *
     * @param event the event instance
     * @param <T>   the event type
     * @since 1.0.0
     */
    public <T extends Event> void publish(final T event) {
        @SuppressWarnings("unchecked")
        final EventRegistration<T> registration = (EventRegistration<T>) this.getOrCreateRegistration(event.getClass());
        for (EventSubscription<T> subscription : registration.subscribers()) {
            if (event instanceof CancellableEvent cancellableEvent) {
               if (!cancellableEvent.cancelled() || subscription.acceptsCancelled()) {
                   subscription.subscriber().handle(event);
               }
            } else {
                subscription.subscriber().handle(event);
            }
        }
    }

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param priority   the subscription priority
     * @param acceptsCancelled if the subscription should accept cancelled events
     * @param <T>        the event type
     * @since 1.0.0
     */
    public <T extends Event> void subscribe(final Class<T> eventType, final EventSubscriber<T> subscriber, final int priority, final boolean acceptsCancelled) {
        final EventRegistration<T> registration = nonNull(getOrCreateRegistration(eventType), "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.subscribe(new EventSubscriptionImpl<>(priority, acceptsCancelled, subscriber));
    }

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param priority   the subscription priority
     * @param <T>        the event type
     * @since 1.0.0
     */
    public <T extends Event> void subscribe(final Class<T> eventType, final EventSubscriber<T> subscriber, int priority) {
        final EventRegistration<T> registration = nonNull(getOrCreateRegistration(eventType), "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.subscribe(new EventSubscriptionImpl<>(priority, this.config().acceptsCancelled(), subscriber));
    }

    /**
     * Subscribes to an event with a given event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param <T>        the event type
     * @since 1.0.0
     */
    public <T extends Event> void subscribe(final Class<T> eventType, final EventSubscriber<T> subscriber) {
        final EventRegistration<T> registration = nonNull(getOrCreateRegistration(eventType), "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.subscribe(new EventSubscriptionImpl<>(this.config().priority(), this.config().acceptsCancelled(), subscriber));
    }

    /**
     * Unsubscribes from an event using its event type.
     *
     * @param eventType  the event class
     * @param subscriber the event subscriber
     * @param <T>        the event type
     * @since 1.0.0
     */
    public <T extends Event> void unsubscribe(final Class<T> eventType, final EventSubscriber<T> subscriber) {
        final EventRegistration<T> registration = nonNull(getOrCreateRegistration(eventType), "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.unsubscribe(subscriber);
    }

    /**
     * Gets the event registration by key.
     *
     * @param key the event key
     * @param <T> the event type
     * @return the registration, or null if none exists
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> @Nullable EventRegistration<T> get(final Key key) {
        nonNull(key, "key");
        return (EventRegistration<T>) registrationsByKey.get(key);
    }

    /**
     * Gets the event registration by event type.
     *
     * @param eventType the event class
     * @param <T>       the event type
     * @return the registration, or null if none exists
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> @Nullable EventRegistration<T> get(final Class<T> eventType) {
        nonNull(eventType, "eventType");
        return (EventRegistration<T>) registrationsByClass.get(eventType);
    }

    /**
     * Returns the set of all registered event keys.
     *
     * @return set of event keys
     * @since 1.0.0
     */
    public Set<Key> keySet() {
        return registrationsByKey.keySet();
    }

    /**
     * Gets the event bus' config.
     *
     * @return the config
     * @since 1.0.0
     */
    public EventBusConfig config() {
        return config;
    }

    @Override
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("events", this.keySet().size()),
            ExaminableProperty.of("registeredEvents", this.registrationsByKey.values()));
    }

    @Override
    public String toString() {
        return StringUtils.asString(this);
    }

    /**
     * A registration for a specific event.
     *
     * @param <T> the event type
     * @since 1.0.0
     */
    @NullMarked
    public interface EventRegistration<T extends Event> extends Keyed, Examinable {

        /**
         * Creates an {@link EventRegistration}.
         *
         * @param key       the event key
         * @param eventType the event class
         * @param <T>       the event type
         * @return a new event registration
         * @since 1.0.0
         */
        static <T extends Event> EventRegistration<T> create(final Key key, final Class<T> eventType) {
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

    /**
     * Concrete implementation of {@link EventRegistration} that uses a volatile unmodifiable list
     * to hold subscriptions. Subscription and un-subscription are performed under synchronization
     * while event publishing requires only a volatile read.
     *
     * @param <T> the event type
     * @since 1.0.0
     */
    @NullMarked
    private static final class EventRegistrationImpl<T extends Event> extends AbstractExaminable implements EventRegistration<T> {
        private final @Examine Key key;
        private final Class<T> eventType;
        private volatile @Examine List<EventSubscription<T>> subscribers = Collections.emptyList();

        private EventRegistrationImpl(final Key key, final Class<T> eventType) {
            this.key = nonNull(key, "key");
            this.eventType = nonNull(eventType, "eventType");
        }

        @Override
        public Class<T> eventType() {
            return eventType;
        }

        @Override
        public List<EventSubscription<T>> subscribers() {
            return subscribers;
        }

        @Override
        public synchronized void subscribe(EventSubscription<T> subscription) {
            final List<EventSubscription<T>> current = new ArrayList<>(subscribers);
            current.add(subscription);
            current.sort(Comparator.naturalOrder());
            subscribers = Collections.unmodifiableList(current);
        }

        @Override
        public synchronized void unsubscribe(EventSubscriber<T> subscriber) {
            final List<EventSubscription<T>> current = new ArrayList<>(subscribers);
            boolean removed = current.removeIf(subscription -> subscription.subscriber().equals(subscriber));
            if (removed) {
                current.sort(Comparator.naturalOrder());
                subscribers = Collections.unmodifiableList(current);
            }
        }

        @Override
        public Key key() {
            return key;
        }

        @Override
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.concat(
                Stream.of(
                    ExaminableProperty.of("type", eventType.getCanonicalName())
                ),
                super.examinableProperties()
            );
        }

        @Override
        public String examinableName() {
            return EventRegistrationImpl.class.getSimpleName();
        }

    }

    /**
     * An event subscription, with ordering determined by priority.
     *
     * @param priority the priority
     * @param acceptsCancelled the accepts cancelled option
     * @param subscriber the handler function
     * @param <T> the event type
     * @since 1.0.0
     */
    @NullMarked
    private record EventSubscriptionImpl<T extends Event>(int priority, boolean acceptsCancelled, EventSubscriber<T> subscriber)
        implements EventSubscription<T> {

        @Override
        public int compareTo(final EventSubscription<?> that) {
            return Integer.compare(this.priority, that.priority());
        }
    }
}
