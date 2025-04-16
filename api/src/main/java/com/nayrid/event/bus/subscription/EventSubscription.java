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
package com.nayrid.event.bus.subscription;

import com.nayrid.event.Event;
import org.jspecify.annotations.NullMarked;

/**
 * A prioritized {@link EventSubscriber}.
 *
 * @param <T>        the event type
 * @since 1.0.0
 */
@NullMarked
public interface EventSubscription<T extends Event> extends Comparable<EventSubscription<?>> {

    /**
     * Gets the subscription's priority.
     *
     * @return the priority
     * @since 1.0.0
     */
    int priority();

    /**
     * Gets if the subscription accepts cancelled events.
     *
     * @return if the subscription accepts cancelled events
     * @since 1.0.0
     */
    boolean acceptsCancelled();

    /**
     * Gets the function to run when this event is published.
     *
     * @return the function
     * @since 1.0.0
     */
    EventSubscriber<T> subscriber();

}
