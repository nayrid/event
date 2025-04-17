package com.nayrid.event.testdata;

import com.nayrid.event.Event;
import com.nayrid.event.annotation.AnnoKey;

@AnnoKey(namespace = "benchmark", value = "counting")
public final class CountingEvent implements Event {

}
