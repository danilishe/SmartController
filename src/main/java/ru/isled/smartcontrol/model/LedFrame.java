package ru.isled.smartcontrol.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.DEFAULT_FRAME_LENGTH;
import static ru.isled.smartcontrol.Constants.MAX_CHANNELS_COUNT;

public class LedFrame {
    private IntegerProperty frameLength = new SimpleIntegerProperty(DEFAULT_FRAME_LENGTH);
    private IntegerProperty cycles = new SimpleIntegerProperty(1);
    private List<FramePixel> pixels = new ArrayList<>(MAX_CHANNELS_COUNT);

    public LedFrame() {
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            pixels.add(new FramePixel(Color.BLACK, PixelEffect.Solid));
        }
    }

    public List<FramePixel> getPixels() {
        return pixels;
    }

    public LedFrame setPixels(List<FramePixel> pixels) {
        this.pixels = pixels;
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

    public FramePixel getPixel(int num) {
        return pixels.get(num);
    }

    public LedFrame setLength(int length) {
        frameLength.setValue(length);
        return this;
    }
}
