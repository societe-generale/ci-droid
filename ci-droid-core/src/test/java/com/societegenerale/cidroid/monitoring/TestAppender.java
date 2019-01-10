package com.societegenerale.cidroid.monitoring;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class TestAppender extends AppenderBase<ILoggingEvent> {
    public static List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent e) {
        e.prepareForDeferredProcessing();
        events.add(e);
    }
}