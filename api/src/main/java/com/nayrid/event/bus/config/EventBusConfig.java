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
package com.nayrid.event.bus.config;

import com.nayrid.common.Buildable;
import com.nayrid.event.bus.EventBus;
import com.nayrid.event.bus.config.EventBusConfigImpl.BuilderImpl;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Configuration for the behaviour of {@link EventBus}.
 *
 * @since 1.0.0
 */
@NullMarked
public sealed interface EventBusConfig extends Buildable<EventBusConfig, EventBusConfig.Builder> permits
    EventBusConfigImpl {

    /**
     * Creates a new {@link EventBusConfig} builder.
     *
     * @return a builder
     * @since 1.0.0
     */
    @Contract(value = "-> new", pure = true)
    static EventBusConfig.Builder eventBusConfig() {
        return new BuilderImpl();
    }

    /**
     * Gets the default priority events are registered with.
     *
     * @return the default priority
     * @since 1.0.0
     */
    int priority();

    /**
     * Gets the default {@code acceptsCancelled} value events are registered with.
     *
     * @return the {@code acceptsCancelled} option
     * @since 1.0.0
     */
    boolean acceptsCancelled();

    /**
     * An {@link EventBusConfig} builder.
     *
     * @since 1.0.0
     */
    sealed interface Builder extends com.nayrid.common.Builder<EventBusConfig> permits
        EventBusConfigImpl.BuilderImpl {

        int DEFAULT_PRIORITY = 0;
        boolean DEFAULT_ACCEPTS_CANCELLED = false;

        /**
         * Gets the default priority events are registered with.
         *
         * @return the default priority
         * @since 1.0.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder priority(int priority);

        /**
         * Gets the default {@code acceptsCancelled} value events are registered with.
         *
         * @return the {@code acceptsCancelled} option
         * @since 1.0.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder acceptsCancelled(boolean acceptsCancelled);

    }

}
