package com.nayrid.event.testdata;

import com.nayrid.event.CancellableEvent;
import com.nayrid.event.annotation.AnnoKey;

@AnnoKey(namespace = "benchmark", value = "cancellation")
public final class CancellationEvent implements CancellableEvent {

    private boolean cancelled = false;

    @Override
    public boolean cancelled() {
        return this.cancelled;
    }

    @Override
    public void cancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

}
