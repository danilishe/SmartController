package ru.isled.smartcontrol.model;

import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;
import static ru.isled.smartcontrol.Constants.MAX_PIXELS_COUNT;

public class LedFrame {
    private IntegerProperty frameLength = new SimpleIntegerProperty(DEFAULT_FRAME_LENGTH);
    private IntegerProperty cycles = new SimpleIntegerProperty(1);
    private List<IntegerProperty> pixels = new ArrayList<>(MAX_PIXELS_COUNT);

    public LedFrame() {
        for (int i = 0; i < MAX_PIXELS_COUNT; i++) {
            pixels.add(new SimpleIntegerProperty(0));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LedFrame)) return false;

        LedFrame ledFrame = (LedFrame) o;

        if (getFrameLength() != null ? !getFrameLength().equals(ledFrame.getFrameLength()) : ledFrame.getFrameLength() != null)
            return false;
        if (getCycles() != null ? !getCycles().equals(ledFrame.getCycles()) : ledFrame.getCycles() != null)
            return false;
        return getPixels() != null ? getPixels().equals(ledFrame.getPixels()) : ledFrame.getPixels() == null;
    }

    public List<Integer> getPixels() {
        return pixels.stream()
                .map(IntegerExpression::getValue)
                .collect(Collectors.toList());
    }

    public LedFrame setPixels(List<Integer> pixels) {
        for (int i = 0; i < this.pixels.size(); i++) {
            this.pixels.get(i).set(pixels.get(i));
        }
        return this;
    }

    public ObjectProperty<Integer> getFrameLength() {
        return frameLength.asObject();
    }

    public LedFrame setFrameLength(int frameLength) {
        this.frameLength.set(frameLength);
        return this;
    }

    public ObjectProperty<Integer> getCycles() {
        return cycles.asObject();
    }

    public LedFrame setCycles(int cycles) {
        this.cycles.set(cycles);
        return this;
    }

    public int getInt(int num) {
        return pixels.get(num).getValue();
    }

    public ObjectProperty<Integer> get(int num) {
        return pixels.get(num).asObject();
    }

    public IntegerProperty getProperty(int num) {
        return pixels.get(num);
    }

    public LedFrame setLength(int length) {
        frameLength.setValue(length);
        return this;
    }

    public LedFrame setPixel(int num, int value) {
        pixels.get(num).setValue(value);
        return this;
    }

    public LedFrame set(int pixel, int val) {
        pixels.get(pixel).setValue(val);
        return this;
    }
}
