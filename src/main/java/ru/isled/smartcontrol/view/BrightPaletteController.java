package ru.isled.smartcontrol.view;

import javafx.geometry.Pos;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static ru.isled.smartcontrol.Constants.COLOR_PALETTE_GRAYS;
import static ru.isled.smartcontrol.Constants.PALETTE_COLOR_SIZE;

public class BrightPaletteController {
    private final MainController mainController;
    private final HBox brightPalette;

    public BrightPaletteController(MainController mainController, HBox brightPalette) {
        this.mainController = mainController;
        this.brightPalette = brightPalette;

        for (int i = 0; i <= COLOR_PALETTE_GRAYS; i++) {
            double bright = (double) i / COLOR_PALETTE_GRAYS;
            Text text = new Text(String.format("%.0f", bright * 100));
            text.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            text.setFill(Color.WHITE);
            text.setBlendMode(BlendMode.DIFFERENCE);
            Shape shape = new Rectangle(PALETTE_COLOR_SIZE, PALETTE_COLOR_SIZE, Color.gray(bright));
            shape.setStroke(Color.RED);
            StackPane stack = new StackPane(shape, text);
            stack.setAlignment(Pos.CENTER);
            stack.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() > 1)
                        mainController.setBright(bright, bright);
                    else
                        mainController.setBright(bright, null);
                } else if (event.getButton() == MouseButton.MIDDLE)
                    mainController.setBright(bright, bright);
                else
                    mainController.setBright(null, bright);
            });
            brightPalette.getChildren().add(stack);
        }
    }
}
