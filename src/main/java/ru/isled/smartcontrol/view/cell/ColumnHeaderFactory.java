package ru.isled.smartcontrol.view.cell;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.Pixel;

public class ColumnHeaderFactory {
    public static Node get(Pixel pixel) {
        VBox header = new VBox(4);
        header.setFillWidth(true);
        header.setAlignment(Pos.CENTER);
        Text pixelNumber = new Text(String.valueOf(pixel.getNumber()));
        pixelNumber.textProperty().bind(pixel.numberProperty().asString());
        Text pixelQuantifier = new Text(String.valueOf(pixel.getQuantifier()));
        pixelQuantifier.textProperty().bind(pixel.quantifierProperty().asString());
        Pane pane = new Pane();
        pane.setMinHeight(10);
        pane.setPrefWidth(Double.MAX_VALUE);
        pane.backgroundProperty().bind(pixel.backgroundProperty());
        header.getChildren().addAll(pixelNumber, pixelQuantifier, pane);
        return header;
    }
}
