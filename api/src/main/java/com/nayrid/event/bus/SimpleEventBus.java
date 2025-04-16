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

import com.nayrid.event.bus.config.EventBusConfig;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.NullMarked;

/**
 * A simple event bus.
 *
 * @since 1.0.0
 */
@NullMarked
public class SimpleEventBus extends AbstractEventBus<EventBusConfig> {

    private static final AtomicReference<SimpleEventBus> GLOBAL_BUS = new AtomicReference<>(
        create(EventBusConfig.eventBusConfig().build()));

    SimpleEventBus(final EventBusConfig config) {
        super(config);
    }

    /**
     * Gets the global {@link SimpleEventBus}.
     *
     * @return the global event bus
     * @since 1.0.0
     */
    public static SimpleEventBus global() {
        return GLOBAL_BUS.get();
    }

    /**
     * Creates a new {@link SimpleEventBus}.
     *
     * @param config the configuration
     * @return an event bus
     * @since 1.0.0
     */
    public static SimpleEventBus create(final EventBusConfig config) {
        return new SimpleEventBus(config);
    }

    @Override
    public String examinableName() {
        return SimpleEventBus.class.getSimpleName();
    }

}
