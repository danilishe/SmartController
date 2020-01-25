package ru.isled.smartcontrol.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;

public class LedFrame {
    private final IntegerProperty frameLength;
    private final IntegerProperty cycles;
    private final BooleanProperty visible;

    public LedFrame() {
        this(DEFAULT_FRAME_LENGTH, 1, false);
    }

    public LedFrame(int frameLength, int cycles, boolean visible) {
        this.frameLength = new SimpleIntegerProperty(frameLength);
        this.cycles = new SimpleIntegerProperty(cycles);
        this.visible = new SimpleBooleanProperty(visible);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public int getFrameLength() {
        return frameLength.asObject().get();
    }

    public LedFrame setFrameLength(int frameLength) {
        this.frameLength.set(frameLength);
        return this;
    }

    public int getCycles() {
        return cycles.asObject().get();
    }

    public LedFrame setCycles(int cycles) {
        this.cycles.set(cycles);
        return this;
    }

    public IntegerProperty frameLengthProperty() {
        return frameLength;
    }

    public IntegerProperty cyclesProperty() {
        return cycles;
    }

    public LedFrame setLength(int length) {
        frameLength.setValue(length);
        return this;
    }
}
