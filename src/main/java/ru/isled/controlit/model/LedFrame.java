package ru.isled.controlit.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.controlit.Constants.DEFAULT_FRAME_LENGTH;
import static ru.isled.controlit.Constants.MAX_PIXELS;

public class LedFrame {
    private IntegerProperty frameLength = new SimpleIntegerProperty(DEFAULT_FRAME_LENGTH);
    private IntegerProperty cycles = new SimpleIntegerProperty(1);
    private List<IntegerProperty> pixels = new ArrayList<>(MAX_PIXELS);

    public LedFrame() {
        for (int i = 0; i < MAX_PIXELS; i++) {
            pixels.add(new SimpleIntegerProperty(0));
        }
    }

    public List<Integer> getPixels() {
        return pixels.stream()
                .map(pixel -> pixel.getValue())
                .collect(Collectors.toList());
    }

    public void setPixels(List<Integer> pixels) {
        for (int i = 0; i < this.pixels.size(); i++) {
            this.pixels.get(i).set(pixels.get(i));
        }
    }

    public ObjectProperty<Integer> getFrameLength() {
        return frameLength.asObject();
    }

    public void setFrameLength(int frameLength) {
        this.frameLength.set(frameLength);
    }

    public ObjectProperty<Integer> getCycles() {
        return cycles.asObject();
    }

    public void setCycles(int cycles) {
        this.cycles.set(cycles);
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

    public void setLength(int length) {

        frameLength.setValue(length);
    }

    public void setPixel(int num, int value) {
        pixels.get(num).setValue(value);
    }

    public void set(int pixel, int val) {
        pixels.get(pixel).setValue(val);
    }
}
