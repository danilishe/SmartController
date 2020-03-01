package ru.isled.smartcontrol.model;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import ru.isled.smartcontrol.Constants;

public class ClickableRectangle extends Rectangle {

    public ClickableRectangle(Paint color, EventHandler<MouseEvent> eventHandler) {
        this(Constants.PALETTE_COLOR_SIZE, Constants.PALETTE_COLOR_SIZE, color, eventHandler);
    }

    public ClickableRectangle(double xSize, double ySize, Paint color, EventHandler<MouseEvent> eventHandler) {
        super(xSize, ySize, color);
        setOnMouseClicked(eventHandler);
    }
}
