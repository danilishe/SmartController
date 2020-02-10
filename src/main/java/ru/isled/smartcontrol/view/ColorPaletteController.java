package ru.isled.smartcontrol.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import ru.isled.smartcontrol.Constants;

import static ru.isled.smartcontrol.Constants.COLOR_PALETTE_COLORS;
import static ru.isled.smartcontrol.Constants.COLOR_PALETTE_GRAYS;

public class ColorPaletteController {
    private final MainController mainController;
    private final Pane colorPalette;

    public ColorPaletteController(MainController mainController, Pane colorPalette) {
        this.mainController = mainController;
        this.colorPalette = colorPalette;

        for (int i = 0; i <= COLOR_PALETTE_GRAYS; i++) {
            Color color = Color.gray((double) i / COLOR_PALETTE_GRAYS);
            Shape shape = new Rectangle(Constants.PALETTE_COLOR_SIZE,
                    Constants.PALETTE_COLOR_SIZE,
                    color);
            shape.setStroke(Color.DARKRED);
            shape.addEventFilter(MouseEvent.MOUSE_CLICKED, getMouseEventHandler(color));

            colorPalette.getChildren().add(shape);
        }

        for (int i = 0; i < COLOR_PALETTE_COLORS; i++) {
            Color color = Color.hsb(360. * i / COLOR_PALETTE_COLORS, 1, 1);
            Shape shape = new Rectangle(Constants.PALETTE_COLOR_SIZE,
                    Constants.PALETTE_COLOR_SIZE,
                    color);
            shape.setStroke(Color.BLACK);
            shape.addEventFilter(MouseEvent.MOUSE_CLICKED, getMouseEventHandler(color));
            colorPalette.getChildren().add(shape);
        }
    }

    private EventHandler<MouseEvent> getMouseEventHandler(Color color) {
        return event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                mainController.setStartColor(color);
            else if (event.getButton() == MouseButton.MIDDLE)
                mainController.setColor(color, color);
            else if (event.getButton() == MouseButton.SECONDARY)
                mainController.setEndColor(color);
        };
    }

}
