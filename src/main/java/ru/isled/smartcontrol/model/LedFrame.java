package ru.isled.smartcontrol.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class LedFrame {
    private IntegerProperty frameLength = new SimpleIntegerProperty(DEFAULT_FRAME_LENGTH);
    private IntegerProperty cycles = new SimpleIntegerProperty(1);
    private List<PixelValue> pixelValues = new ArrayList<>(MAX_CHANNELS_COUNT);

    public LedFrame() {
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            pixelValues.add(new PixelValue(Color.BLACK, PixelEffect.Solid));
        }
    }

    public List<PixelValue> getPixelValues() {
        return pixelValues;
    }

    public LedFrame setPixelValues(List<PixelValue> pixelValues) {
        this.pixelValues = pixelValues;
        return this;
    }

    public int getFrameLength() {
        return frameLength.asObject().get();
    }
/* // todo возможно можно обойтись без этого
    public ObjectProperty<Integer> getFrameLength() {
        return frameLength.asObject();
    }
*/

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

    public PixelValue getPixelValue(int num) {
        return pixelValues.get(num);
    }

    public LedFrame setLength(int length) {
        frameLength.setValue(length);
        return this;
    }

    public int getFrameSubFrames() {
        return getFrameLength() / BASE_FRAME_LENGTH;
    }
}
