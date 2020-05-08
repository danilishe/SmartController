package ru.isled.smartcontrol.view.cell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.effect.RgbMode;

import static ru.isled.smartcontrol.Constants.MAX_QUANTIFIER;

public class PixelHeader extends VBox {
    public final Shape previewPixel;

    public PixelHeader(Pixel pixel) {
        super(2);
        ContextMenu contextMenu = getContextMenu(pixel);

        setMinHeight(40);
        setMinWidth(60);
        setAlignment(Pos.BOTTOM_CENTER);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        setFillWidth(true);
        setAlignment(Pos.CENTER);

        previewPixel = new Rectangle(40, 25, Color.BLACK);
        previewPixel.setStroke(Color.BLACK);
        previewPixel.setStrokeWidth(0.7);
        widthProperty().addListener((observable, oldValue, newValue) -> previewPixel.prefWidth(newValue.doubleValue()));

        Text pixelNumber = new Text(String.valueOf(pixel.getNumber()));
        pixelNumber.setFill(Color.WHITE);
        pixelNumber.setBlendMode(BlendMode.EXCLUSION);
        pixelNumber.textProperty().bind(pixel.numberProperty().asString());

        StackPane stack = new StackPane(previewPixel, pixelNumber);
        stack.setPadding(new Insets(5));

        Spinner<Integer> pixelQuantifier = new Spinner<>(1, MAX_QUANTIFIER, pixel.getQuantifier(), 1);
        pixelQuantifier.setEditable(false);
        pixelQuantifier.getValueFactory().valueProperty().bindBidirectional(pixel.quantifierProperty());
        pixelQuantifier.getValueFactory().valueProperty().addListener((ov, o, n) -> previewPixel.setScaleY(1 + ((double) n - 1) / 10));
        pixelQuantifier.setOnScroll(scroll -> {
            if (scroll.getDeltaY() > 0) pixelQuantifier.increment();
            else pixelQuantifier.decrement();
        });
        pixelQuantifier.setScaleX(.8);
        pixelQuantifier.setScaleY(.8);

        pixelQuantifier.setMinWidth(40);
        pixelQuantifier.setMaxWidth(40);

        Text rgbModeText = new Text(pixel.getRgbMode().name());
        rgbModeText.textProperty().bind(pixel.rgbModeProperty().asString());
        rgbModeText.setFill(Color.ROYALBLUE);
        rgbModeText.setFont(Font.font("Sans", FontWeight.EXTRA_BOLD, 12));

        Pane pane = new Pane();
        pane.setMinHeight(7);
        pane.setMinWidth(10);
        pane.setPrefWidth(10);
//        pane.prefWidthProperty().bind(this.prefWidthProperty());
//        pane.setPrefWidth(Double.POSITIVE_INFINITY);
        pane.styleProperty().bind(pixel.background);

        VBox button = new VBox(2, rgbModeText, pane);

        button.setPadding(Insets.EMPTY);
        button.setAlignment(Pos.CENTER);
        button.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));
        getChildren().addAll(stack, pixelQuantifier, button);

        setPrefWidth(Double.POSITIVE_INFINITY);
        setPadding(new Insets(5));
    }

    private static ContextMenu getContextMenu(Pixel pixel) {
        ContextMenu contextMenu = new ContextMenu();
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
        return contextMenu;
    }
}
