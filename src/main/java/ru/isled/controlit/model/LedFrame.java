package ru.isled.controlit.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import ru.isled.controlit.view.MainView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LedFrame {
    private static final int MAX_PIXELS = MainView.MAX_PIXELS;
    private List<IntegerProperty> frames = new ArrayList<>(MAX_PIXELS);


    public LedFrame(List<Byte> aFrames) {
        set(aFrames);
    }

    public LedFrame() {
        set(null);
    }

    public IntegerProperty getPixel(int num) {
        return frames.get(num);
    }

    public void setPixel(int num, int value) {
        frames.get(num).setValue(value);
    }

    public int set(List<Byte> src) {
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
    }

    public List<Byte> asByteList() {
        return frames.stream()
                .map(x -> x.getValue().byteValue())
                .collect(Collectors.toList());
    }
}
