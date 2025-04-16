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
import com.nayrid.event.annotation.AnnotationUtil;
import com.nayrid.event.bus.SimpleEventBus;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jspecify.annotations.NullMarked;

/**
 * Marker interfaces for events. Implementing classes <strong>must</strong> be annotated with
 * {@link AnnoKey}.
 *
 * @since 1.0.0
 */
@NullMarked
public interface Event extends Keyed {

    /**
     * Publishes this event on the {@link SimpleEventBus#global() global EventBus}.
     *
     * @since 1.0.0
     */
    default void publish() {
        SimpleEventBus.global().publish(this);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>You should not, and do not need to, override this method.</strong></p>
     */
    @NonExtendable
    default /* final */ Key key() {
        return AnnotationUtil.key(this.getClass())
            .orElseThrow(() -> new IllegalStateException(
                "Event %s is not annotated with AnnoKey".formatted(
                    this.getClass().getCanonicalName())));
    }

}
