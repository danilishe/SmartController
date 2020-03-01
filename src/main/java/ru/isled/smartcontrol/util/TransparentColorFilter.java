package ru.isled.smartcontrol.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class TransparentColorFilter implements ChangeListener<Color> {
    private final ColorPicker colorPicker;

    public TransparentColorFilter(ColorPicker cp) {
        colorPicker = cp;
    }

    @Override
    public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
        if (!newValue.isOpaque()) {
            colorPicker.setValue(Color.color(newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
        }
    }
}
