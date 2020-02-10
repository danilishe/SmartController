package ru.isled.smartcontrol.view.cell;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.effect.RgbMode;

public class ColumnHeaderFactory {
    public static Node get(Pixel pixel) {
        ContextMenu contextMenu = new ContextMenu();

        VBox header = new VBox(2);
        header.setMinHeight(40);
        header.setAlignment(Pos.BOTTOM_CENTER);
        header.setOnContextMenuRequested(event -> contextMenu.show(header, event.getScreenX(), event.getScreenY()));

        ToggleGroup tg = new ToggleGroup();
        for (RgbMode rgbMode : RgbMode.values()) {
            Shape graphic = new Rectangle(20, 10);
            graphic.setStyle(rgbMode.getBackground().replace("background-color", "fill"));
            graphic.setStroke(Color.BLACK);
            RadioMenuItem radioMenuItem = new RadioMenuItem(rgbMode.name(), graphic);
            radioMenuItem.setToggleGroup(tg);
            radioMenuItem.setUserData(rgbMode);
            if (pixel.getRgbMode() == rgbMode) {
                contextMenu.getItems().add(new SeparatorMenuItem()); // это костыль, не охота парсить имена
                radioMenuItem.setSelected(true);
            }
            contextMenu.getItems().add(radioMenuItem);
        }
        contextMenu.setOnAction(event -> pixel.setRgbMode((RgbMode) tg.getSelectedToggle().getUserData()));

        header.setFillWidth(true);
        header.setAlignment(Pos.CENTER);
        Text pixelNumber = new Text(String.valueOf(pixel.getNumber()));
        pixelNumber.textProperty().bind(pixel.numberProperty().asString());
        Text pixelQuantifier = new Text(String.valueOf(pixel.getQuantifier()));
        pixelQuantifier.textProperty().bind(pixel.quantifierProperty().asString());
        pixelQuantifier.setFill(Color.DEEPSKYBLUE);

        Text rgbModeText = new Text(pixel.getRgbMode().name());
        rgbModeText.textProperty().bind(pixel.rgbModeProperty().asString());
        rgbModeText.setFill(Color.ROYALBLUE);

        Pane pane = new Pane();
        pane.setPrefHeight(3);
        pane.setMinHeight(10);
        pane.setPrefWidth(Double.MAX_VALUE);
        pane.styleProperty().bind(pixel.background);
        header.getChildren().addAll(pixelNumber, pixelQuantifier, rgbModeText, pane);
        return header;
    }
}
