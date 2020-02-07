package ru.isled.smartcontrol.util;

import javafx.scene.paint.Color;

public class Convert {
    public static String toHex(Color color) {
        return String.format("%02x%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }
}
