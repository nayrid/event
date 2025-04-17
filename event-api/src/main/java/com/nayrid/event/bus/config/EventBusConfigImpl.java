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

import org.jspecify.annotations.NullMarked;

@NullMarked
record EventBusConfigImpl(int priority, boolean acceptsCancelled) implements EventBusConfig {

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this.priority(), this.acceptsCancelled());
    }

    static final class BuilderImpl implements EventBusConfig.Builder {

        private int priority = DEFAULT_PRIORITY;
        private boolean acceptsCancelled = DEFAULT_ACCEPTS_CANCELLED;

        BuilderImpl() {
        }

        private BuilderImpl(final int priority, final boolean acceptsCancelled) {
            this.priority = priority;
            this.acceptsCancelled = acceptsCancelled;
        }

        @Override
        public Builder priority(final int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public Builder acceptsCancelled(final boolean acceptsCancelled) {
            this.acceptsCancelled = acceptsCancelled;
            return this;
        }

        @Override
        public EventBusConfig build() {
            return new EventBusConfigImpl(this.priority, this.acceptsCancelled);
        }

    }

}
