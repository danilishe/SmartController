package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;

public class LedFrame {
    private final IntegerProperty number;
    private final IntegerProperty frameLength;
    private final IntegerProperty cycles;
    private final BooleanProperty visible;

    public LedFrame(int number) {
        this(number, DEFAULT_FRAME_LENGTH, 1, false);
    }

    public LedFrame(int number, int frameLength, int cycles, boolean visible) {
        this.number = new SimpleIntegerProperty(number);
        this.frameLength = new SimpleIntegerProperty(frameLength);
        this.cycles = new SimpleIntegerProperty(cycles);
        this.visible = new SimpleBooleanProperty(visible);
    }

    public int getNumber() {
        return number.get();
    }

    public LedFrame setNumber(int number) {
        this.number.set(number);
        return this;
    }

    public ObservableValue<Integer> numberProperty() {
        return number.asObject();
    }

    public LedFrame setVisible(boolean visible) {
        this.visible.set(visible);
        return this;
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

    public ObjectProperty<Integer> getCyclesProperty() {
        return cycles.asObject();
    }

    public ObjectProperty<Integer> frameLengthProperty() {
        return frameLength.asObject();
    }

    public ObjectProperty<Integer> cyclesProperty() {
        return cycles.asObject();
    }

    public LedFrame setLength(int length) {
        frameLength.setValue(length);
        return this;
    }
}
