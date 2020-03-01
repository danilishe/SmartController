package ru.isled.smartcontrol.view.effect_controller;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.ClickableRectangle;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.util.SimpleValueScroller;
import ru.isled.smartcontrol.util.TransparentColorFilter;
import ru.isled.smartcontrol.view.CustomColorController;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static ru.isled.smartcontrol.Constants.CUSTOM_COLORS_COUNT;
import static ru.isled.smartcontrol.Constants.PALETTE_COLOR_SIZE;

public class ColorGradientController implements Initializable {
    private static final List<Gradient> gradientSamples = Arrays.asList(
            new Gradient(1, 1, Color.RED, Color.LIME, Color.BLUE),
            new Gradient(1, 1, Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, Color.SKYBLUE, Color.BLUE, Color.VIOLET),
            new Gradient(1, 0, Color.WHITE, Color.BLUE, Color.RED)
    );
    private static ColorGradientController cgc;
    @FXML
    ColorPicker customColorPicker;
    @FXML
    Rectangle customColor;
    @FXML
    FlowPane colorPalette;
    @FXML
    FlowPane gradient;
    @FXML
    HBox secondaryPalette;
    @FXML
    ToggleGroup direction;
    @FXML
    Spinner<Integer> colorWidth;
    @FXML
    Spinner<Integer> transitionWidth;
    @FXML
    CheckBox onlyColorCheckbox;
    @FXML
    CheckBox autoFrameCheckbox;
    @FXML
    VBox samples;
    @FXML
    ScrollPane scroll;
    Alert window;
    private Project project;
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    private ColorGradientController() {
        loadDialog();
    }

    public static ColorGradientController get(Project project, int x1, int y1, int x2, int y2) {
        if (cgc == null)
            cgc = new ColorGradientController();
        cgc.project = project;
        cgc.x1 = x1;
        cgc.x2 = x2;
        cgc.y1 = y1;
        cgc.y2 = y2;
        return cgc;
    }

    @FXML
    public void clearPalette() {
        gradient.getChildren().clear();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secondaryPalette.getChildren().addListener((InvalidationListener) c -> {
            if (secondaryPalette.getChildren().size() > Constants.CUSTOM_PALETTE_SIZE)
                secondaryPalette.getChildren().remove(0);
        });
        customColorPicker.valueProperty().addListener(new TransparentColorFilter(customColorPicker));
        customColor.fillProperty().bind(customColorPicker.valueProperty());
        // add old color to secondary palette when selected new one
        customColor.fillProperty()
                .addListener((observable, oldValue, newValue) -> secondaryPalette.getChildren()
                        .add(new ClickableRectangle(oldValue, event -> addColor((Color) oldValue))));
        customColor.setOnMouseClicked(event -> addColor((Color) customColor.getFill()));
        customColorPicker.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
        CustomColorController.getColorPalette(Constants.CUSTOM_COLORS_COUNT)
                .forEach(color -> {
                    Rectangle rectangle = new Rectangle(PALETTE_COLOR_SIZE, PALETTE_COLOR_SIZE, color);
                    rectangle.setOnMouseClicked(event -> addColor(color));
                    colorPalette.getChildren().add(rectangle);
                });
        colorWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        transitionWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 1));
        colorWidth.setOnScroll(new SimpleValueScroller(colorWidth));
        transitionWidth.setOnScroll(new SimpleValueScroller(transitionWidth));

        gradientSamples.forEach(g -> {
                    final ClickableRectangle rect = new ClickableRectangle(550, 24, g.asGradient(), event -> {
                        gradient.getChildren().clear();
                        g.colors.forEach(this::addColor);
                        transitionWidth.getEditor().textProperty().set(String.valueOf(g.transitionWidth));
                        colorWidth.getEditor().textProperty().set(String.valueOf(g.colorWidth));
                    });
                    samples.getChildren().add(rect);

                }
        );
    }

    private void addColor(Color fill) {
        final ObservableList<Node> children = gradient.getChildren();
        if (children.size() < CUSTOM_COLORS_COUNT)
            children.add(new RemovableRectangle(fill, gradient));
    }

    public void apply() {
        final Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get()))
            apply();
    }

    private void loadDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("colorGradient.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Цветовой перелив\"");
        window.setHeaderText("Выберите параметры эффекта");
        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
    }

    private static class RemovableRectangle extends Rectangle {
        public RemovableRectangle(Color fill, Pane parent) {
            super(PALETTE_COLOR_SIZE, PALETTE_COLOR_SIZE, fill);
            onMouseClickedProperty().set(event -> parent.getChildren().remove(RemovableRectangle.this));
            final ObservableList<String> styleClasses = RemovableRectangle.this.getStyleClass();
            styleClasses.clear();
            styleClasses.add("onClickRemovable");
        }
    }

    private static class Gradient {
        public final int colorWidth;
        public final int transitionWidth;
        public final List<Color> colors;

        private Gradient(int colorWidth, int transitionWidth, List<Color> colors) {
            this.colorWidth = colorWidth;
            this.transitionWidth = transitionWidth;
            this.colors = colors;
        }

        private Gradient(int colorWidth, int transitionWidth, Color... colors) {
            this.colorWidth = colorWidth;
            this.transitionWidth = transitionWidth;
            this.colors = new ArrayList<>(Arrays.asList(colors));
        }

        public LinearGradient asGradient() {
            final List<Stop> stops = new ArrayList<>();
            for (int i = 0; i < colors.size(); i++) {
                stops.add(new Stop((double) i / (colors.size() - 1), colors.get(i)));
                if (transitionWidth == 0 && i < colors.size() - 1) { // for sharp transition
                    final double middle = ((double) i + 1) / colors.size();
                    stops.add(new Stop(middle - .001, colors.get(i)));
                    stops.add(new Stop(middle, colors.get(i + 1)));
                }
            }
            return new LinearGradient(0, 0, 1, 0, true, null, stops);
        }
    }

}
