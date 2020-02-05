package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Background;

import java.util.List;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;

public class LedFrame {
    private final IntegerProperty number;
    private final IntegerProperty frameLength;
    private final IntegerProperty cycles;
    private final List<ObservableValue<Background>> values;

    public LedFrame(int number, List<ObservableValue<Background>> values) {
        this(number, DEFAULT_FRAME_LENGTH, 1, values);
    }

    public LedFrame(int number, int frameLength, int cycles, List<ObservableValue<Background>> values) {
        this.number = new SimpleIntegerProperty(number);
        this.frameLength = new SimpleIntegerProperty(frameLength);
        this.cycles = new SimpleIntegerProperty(cycles);
        this.values = values;
    }

    public ObservableValue<Background> getValue(int index) {
        return values.get(index);
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
