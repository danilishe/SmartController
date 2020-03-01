package ru.isled.smartcontrol.util;

import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.model.Project;

public class Util {
    private static final Logger log = LogManager.getLogger();

    public static String toHex(Color color) {
        return String.format("%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static boolean isOdd(int i) {
        final boolean b = (i & 1) == 1;
        log.debug(i + " is odd: " + b);
        return b;
    }

    public static Project fill(final Project project, int x1, int y1, int x2, int y2, Color color) {
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                project.getPixelFrame(y, x).setColor(color);
            }
        }
        return project;
    }

    public static Project fill(final Project project, int x1, int programLength, int x2, int y2, Color firstColor, Color secondColor) {
        for (int y = programLength; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                project.getPixelFrame(y, x).setColor(firstColor, secondColor);
            }
        }
        return project;
    }
}
