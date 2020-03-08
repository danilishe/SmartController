package ru.isled.smartcontrol.model;

import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;

public enum Direction {
    LEFT, RIGHT, TO_CENTER, FROM_CENTER;

    public static Direction of(ToggleGroup direction) {
        String s = ((Labeled) direction.getSelectedToggle()).getText().toLowerCase();
        switch (s) {
            case "влево":
                return LEFT;
            case "вправо":
                return RIGHT;
            case "из центра":
                return FROM_CENTER;
            default:
                return TO_CENTER;
        }
    }
}
