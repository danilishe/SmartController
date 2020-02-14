package ru.isled.smartcontrol.view;

import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import static ru.isled.smartcontrol.Constants.CUSTOM_COLORS_COUNT;


public class CustomColorController {
    private final MainController mainController;
    private final Shape customColor;
    private final ColorPicker colorPicker;

    public CustomColorController(MainController mainController, Shape customColor, ColorPicker colorPicker) {
        this.mainController = mainController;
        this.customColor = customColor;
        this.colorPicker = colorPicker;

        customColor.fillProperty().bind(colorPicker.valueProperty());
        customColor.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Color color = (Color) customColor.getFill();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() > 1) {
                    mainController.setColor(color, color);
                } else mainController.setColor(color, null);
            } else if (event.getButton() == MouseButton.MIDDLE) {
                mainController.setColor(color, color);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                mainController.setColor(null, color);
            }
        });

        for (int i = 0; i < CUSTOM_COLORS_COUNT; i++) {
            colorPicker.getCustomColors().add(Color.hsb(360d / CUSTOM_COLORS_COUNT * i, 1, 1));
        }

    }
}
