package ru.isled.smartcontrol.view.cell;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.Pixel;

public class ColumnHeaderFactory {
    public static Node get(Pixel pixel) {
        VBox header = new VBox(2);
        header.backgroundProperty().bind(pixel.backgroundProperty());
        Text pixelNumber = new Text(String.valueOf(pixel.getNumber()));
        pixelNumber.textProperty().bind(pixel.numberProperty().asString());
        Text pixelQuantifier = new Text(String.valueOf(pixel.getQuantifier()));
        pixelQuantifier.textProperty().bind(pixel.quantifierProperty().asString());
        header.getChildren().addAll(pixelNumber, pixelQuantifier);
        return header;
    }
}
