package ru.isled.smartcontrol.util;

import javafx.scene.paint.Color;

public interface ColorToHex {
    default String toHex(Color color) {
        return String.format("%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
