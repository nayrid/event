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

import com.nayrid.event.bus.SimpleEventBus;
import org.jetbrains.annotations.Contract;

/**
 * An event that can be cancelled.
 *
 * @since 1.0.0
 */
public interface CancellableEvent extends Event {

    /**
     * Publishes this event on the {@link SimpleEventBus#global() global EventBus} and returns
     * {@code true} if it was <strong><u>not</u></strong> cancelled.
     *
     * @since 1.0.0
     */
    default boolean publishAndReturn() {
        return SimpleEventBus.global().publish(this);
    }

    /**
     * Gets if the event has been cancelled.
     *
     * @return the cancellation state
     * @since 1.0.0
     */
    boolean cancelled();

    /**
     * Sets if the event is cancelled.
     *
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    void cancelled(boolean cancelled);

}
