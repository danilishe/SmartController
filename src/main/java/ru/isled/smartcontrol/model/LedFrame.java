package ru.isled.smartcontrol.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.List;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;

public class LedFrame {
    private final IntegerProperty number;
    private final IntegerProperty frameLength;
    private final IntegerProperty cycles;
    private final List<StringProperty> values;

    public LedFrame(int number, List<StringProperty> values) {
        this(number, DEFAULT_FRAME_LENGTH, 1, values);
    }

    public LedFrame(int number, int frameLength, int cycles, List<StringProperty> values) {
        this.number = new SimpleIntegerProperty(number);
        this.frameLength = new SimpleIntegerProperty(frameLength);
        this.cycles = new SimpleIntegerProperty(cycles);
        this.values = values;
    }

    public StringProperty getValue(int index) {
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

    public int getLength() {
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
        if (cycles != getCycles()) {
            Project.setHasChanges(true);
            this.cycles.set(cycles);
        }
        return this;
    }

    public ObjectProperty<Integer> frameLengthProperty() {
        return frameLength.asObject();
    }

    public ObjectProperty<Integer> cyclesProperty() {
        return cycles.asObject();
    }

    public LedFrame setLength(int length) {
        if (length != getLength()) {
            Project.setHasChanges(true);
            frameLength.setValue(length);
        }
        return this;
    }
}
