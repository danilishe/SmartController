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
    private List<IntegerProperty> frames = new ArrayList<>(MAX_PIXELS);

    public LedFrame() {
        for (int i = 0; i < MAX_PIXELS; i++) {
            frames.add(new SimpleIntegerProperty(0));
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
        return frames.get(num).getValue();
    }

    public ObjectProperty<Integer> get(int num) {
        return frames.get(num).asObject();
    }

    public IntegerProperty getProperty(int num) {
        return frames.get(num);
    }

    public void setLength(int length) {

        frameLength.setValue(length);
    }

    public void setPixel(int num, int value) {
        frames.get(num).setValue(value);
    }

/*    public int set(List<Byte> src) {
        int lastNotNullValue = 0;
        for (int i = 0; i < MAX_PIXELS; i++) {

            // если коллекция пуста или  недостаточно данных, используем для заполнения нули
            int srcVal = (src != null) && (src.size() > i)
                    ? src.get(i) & 0XFF
                    : 0;

            if (srcVal != 0) lastNotNullValue = i + 1;
//            IntegerProperty target = frames.size() > i && frames.get(i)

            if (frames.size() > i && frames.get(i) != null) {
                frames.get(i).setValue(srcVal);
            } else {
                frames.add(new SimpleIntegerProperty(srcVal));
            }
        }
        return lastNotNullValue;
    }*/

    public List<Byte> asByteList() {
        return frames.stream()
                .map(x -> x.getValue().byteValue())
                .collect(Collectors.toList());
    }

    public void set(int pixel, int val) {
        frames.get(pixel).setValue(val);
    }
}
