package ru.isled.smartcontrol.util;

import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.input.ScrollEvent;

public class SimpleValueScroller implements EventHandler<ScrollEvent> {
    private final Spinner<?> spinner;

    public SimpleValueScroller(Spinner<?> spinner) {
        this.spinner = spinner;
    }

    @Override
    public void handle(ScrollEvent event) {
        if (event.getDeltaY() > 0) spinner.increment();
        else spinner.decrement();
    }
}
