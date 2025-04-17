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
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.nayrid.common.Validate.nonNull;

/**
 * An abstract {@link EventBus} implementation.
 *
 * @param <C> the config type
 * @since 1.0.0
 */
@NullMarked
public abstract class AbstractEventBus<C extends EventBusConfig> extends AbstractExaminable implements
    Examinable,
    EventBus<C> {

    protected final @Examine C config;
    protected final ConcurrentHashMap<Key, EventRegistration<?>> registrationsByKey = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<Class<? extends Event>, EventRegistration<?>> registrationsByClass = new ConcurrentHashMap<>();

    protected AbstractEventBus(final C config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> EventRegistration<T> getOrCreateRegistration(
        final Class<T> eventType) {
        final Key key = AnnotationUtil.key(eventType)
            .orElseThrow(() -> new IllegalStateException(
                "Event %s is not annotated with AnnoKey".formatted(eventType.getCanonicalName())));

        return (EventRegistration<T>) this.registrationsByKey.computeIfAbsent(key, k -> {
            EventRegistration<T> reg = EventRegistration.create(key, eventType);
            this.registrationsByClass.putIfAbsent(eventType, reg);
            return reg;
        });
    }

    @Override
    public <T extends Event> void publish(final T event) {
        @SuppressWarnings("unchecked") final EventRegistration<T> registration = (EventRegistration<T>) this.getOrCreateRegistration(
            event.getClass());
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

    @Override
    public <T extends Event> void subscribe(final Class<T> eventType,
        final EventSubscriber<T> subscriber, final int priority, final boolean acceptsCancelled) {
        final EventRegistration<T> registration = nonNull(this.getOrCreateRegistration(eventType),
            "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.subscribe(new EventSubscriptionImpl<>(priority, acceptsCancelled, subscriber));
    }

    @Override
    public <T extends Event> void subscribe(final Class<T> eventType,
        final EventSubscriber<T> subscriber, int priority) {
        this.subscribe(eventType, subscriber, priority, this.config().acceptsCancelled());
    }

    @Override
    public <T extends Event> void subscribe(final Class<T> eventType,
        final EventSubscriber<T> subscriber) {
        this.subscribe(eventType, subscriber, this.config().priority(), this.config().acceptsCancelled());
    }

    @Override
    public <T extends Event> void unsubscribe(final Class<T> eventType,
        final EventSubscriber<T> subscriber) {
        final EventRegistration<T> registration = nonNull(this.getOrCreateRegistration(eventType),
            "event registration for event type: '" + eventType.getCanonicalName() + "'");
        registration.unsubscribe(subscriber);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> @Nullable EventRegistration<T> get(final Key key) {
        nonNull(key, "key");
        return (EventRegistration<T>) this.registrationsByKey.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> @Nullable EventRegistration<T> get(final Class<T> eventType) {
        nonNull(eventType, "eventType");
        return (EventRegistration<T>) this.registrationsByClass.get(eventType);
    }

    @Override
    public Set<Key> keySet() {
        return this.registrationsByKey.keySet();
    }

    @Override
    public C config() {
        return this.config;
    }

    @Override
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("events", this.keySet().size()),
                ExaminableProperty.of("registeredEvents", this.registrationsByKey.values())),
            super.examinableProperties());
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
    static final class EventRegistrationImpl<T extends Event> extends AbstractExaminable implements
        EventRegistration<T> {

        private final @Examine Key key;
        private final Class<T> eventType;
        private volatile @Examine List<EventSubscription<T>> subscribers = Collections.emptyList();

        EventRegistrationImpl(final Key key, final Class<T> eventType) {
            this.key = nonNull(key, "key");
            this.eventType = nonNull(eventType, "eventType");
        }

        @Override
        public Class<T> eventType() {
            return this.eventType;
        }

        @Override
        public List<EventSubscription<T>> subscribers() {
            return this.subscribers;
        }

        @Override
        public synchronized void subscribe(EventSubscription<T> subscription) {
            final List<EventSubscription<T>> current = new ArrayList<>(this.subscribers);
            current.add(subscription);
            current.sort(Comparator.naturalOrder());
            this.subscribers = Collections.unmodifiableList(current);
        }

        @Override
        public synchronized void unsubscribe(EventSubscriber<T> subscriber) {
            final List<EventSubscription<T>> current = new ArrayList<>(this.subscribers);
            boolean removed = current.removeIf(
                subscription -> subscription.subscriber().equals(subscriber));
            if (removed) {
                current.sort(Comparator.naturalOrder());
                this.subscribers = Collections.unmodifiableList(current);
            }
        }

        @Override
        public Key key() {
            return this.key;
        }

        @Override
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.concat(
                Stream.of(ExaminableProperty.of("type", this.eventType.getCanonicalName())),
                super.examinableProperties());
        }

        @Override
        public String examinableName() {
            return EventRegistrationImpl.class.getSimpleName();
        }

    }

    /**
     * An event subscription, with ordering determined by priority.
     *
     * @param priority         the priority
     * @param acceptsCancelled the accepts cancelled option
     * @param subscriber       the handler function
     * @param <T>              the event type
     * @since 1.0.0
     */
    @NullMarked
    private record EventSubscriptionImpl<T extends Event>(int priority, boolean acceptsCancelled,
                                                          EventSubscriber<T> subscriber) implements
        EventSubscription<T> {

        @Override
        public int compareTo(final EventSubscription<?> that) {
            return Integer.compare(this.priority, that.priority());
        }

    }

}
