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
import javafx.scene.shape.Shape;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.ClickableRectangle;
import ru.isled.smartcontrol.model.Direction;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.util.SimpleValueScroller;
import ru.isled.smartcontrol.util.TransparentColorFilter;
import ru.isled.smartcontrol.util.Util;
import ru.isled.smartcontrol.view.CustomColorController;
import ru.isled.smartcontrol.view.Dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.CUSTOM_COLORS_COUNT;
import static ru.isled.smartcontrol.Constants.PALETTE_COLOR_SIZE;
import static ru.isled.smartcontrol.util.Util.isOdd;

public class ColorGradientController implements Initializable {
    private static final Logger log = LogManager.getLogger();
    private static final List<Gradient> gradientSamples = Arrays.asList(
            new Gradient(1, 1, Color.RED, Color.LIME, Color.BLUE),
            new Gradient(1, 1, Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, Color.SKYBLUE, Color.BLUE, Color.VIOLET),
            new Gradient(3, 0, Color.WHITE, Color.BLUE, Color.RED)
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
    CheckBox setBackground;
    @FXML
    ColorPicker backgroundColorPicker;
    //    @FXML
//    CheckBox cycled;
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

    private List<Color> getSelectedColors() {
        return gradient.getChildren().stream()
                .map(node -> (Color) ((Shape) node).getFill())
                .collect(Collectors.toList());
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
        backgroundColorPicker.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
        backgroundColorPicker.setValue(Color.BLACK);
        backgroundColorPicker.valueProperty().addListener(new TransparentColorFilter(backgroundColorPicker));
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
                        transitionWidth.getValueFactory().setValue(g.transitionWidth);
                        colorWidth.getValueFactory().setValue(g.colorWidth);
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
        boolean retry = false;
        do {
            final Optional<ButtonType> button = window.showAndWait();
            if (ButtonType.OK.equals(button.get())) {
                final List<Color> selectedColors = getSelectedColors();
                if (selectedColors.isEmpty()) {
                    retry = true;
                    Dialogs.showErrorAlert("Нужно выбрать несколько цветов!");
                    continue;
                } else {
                    retry = false;
                }

                apply(Direction.of(direction),
                        new Gradient(colorWidth.getValue(), transitionWidth.getValue(), selectedColors),
                        onlyColorCheckbox.isSelected(), autoFrameCheckbox.isSelected(), false /*cycled.isSelected()*/
                        , setBackground.isSelected() ? backgroundColorPicker.getValue() : null
                );
            } else break;
        } while (retry);
    }

    private void apply(Direction direction, Gradient gradient, boolean onlyColored, boolean autoFrame, boolean cycled,
                       Color bgColor) {
        log.trace("Do effect in " + direction + "!");

        final int colorsCount = gradient.colors.size();
        final int transitionWidth = gradient.transitionWidth;
        final int colorWidth = gradient.colorWidth;

        // create gradient
        final ArrayList<Pair<Color, Color>> gradientPairs = new ArrayList<>(colorsCount * colorWidth + transitionWidth * colorsCount);
        for (int i = 0; i <= colorsCount; i++) {
            for (int transCounter = 0; transCounter < transitionWidth; transCounter++) {
                if (i == colorsCount - 1 && cycled) {
                    // at last iteration there is no transition when gradient cycled
                    break;
                }

                Color prevColor = i == 0 ?
                        cycled ? gradient.colors.get(colorsCount - 1) : bgColor
                        : gradient.colors.get(i - 1);
                Color nextColor = i == colorsCount ? bgColor : gradient.colors.get(i);

                gradientPairs.add(new Pair<>(
                        Util.transparentInterpolate(prevColor, nextColor, (double) transCounter / transitionWidth)
                        , Util.transparentInterpolate(prevColor, nextColor, (double) (transCounter + 1) / transitionWidth)
                ));

            }
            if (i < colorsCount) // it just for non repeating last transition logic
                for (int colorCounter = 0; colorCounter < colorWidth; colorCounter++) {
                    gradientPairs.add(new Pair<>(gradient.colors.get(i), gradient.colors.get(i)));
                }
        }
        if (direction == Direction.RIGHT || direction == Direction.FROM_CENTER)
            Collections.reverse(gradientPairs);

        final int selectedWidth = x2 - x1;
        final int programWidth = direction == Direction.TO_CENTER || direction == Direction.FROM_CENTER ?
                selectedWidth + (isOdd(selectedWidth) ? 1 : 0) // from center & to center programs is double shorted
                : selectedWidth;
        final int programLength = programWidth + gradientPairs.size();
        final int selectedLength = y2 - y1;
        final int fullLength = autoFrame && cycled && selectedLength % programLength != 0 ?
                (selectedLength / programLength + 1) * programLength // we make full length multiple one program length
                : cycled ?
                selectedLength : // only selected frames if cycled
                programLength; // or only program length (1 cycle)
        if (y1 + fullLength > project.framesCount() && autoFrame)
            project.setFramesCount(y1 + fullLength);

        Util.fill(project, x1, y1, x2, Math.min(Math.max(y1 + fullLength, y2), project.framesCount()), bgColor);

        // apply gradient
        for (int i = 0; i < fullLength + 1; i++) {
            gradientIteration:
            for (int j = 0; j < gradientPairs.size(); j++) {
                int col;
                switch (direction) {
                    case LEFT:
                        col = cycled ? x2 - (i % gradientPairs.size()) + j : x2 - i + j;
                        if (col > x2) break gradientIteration; // micro optimization
                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
                        break;
                    case RIGHT:
                        col = x1 - gradientPairs.size() + j + (cycled ? i % gradientPairs.size() : i);
                        if (col > x2) break gradientIteration;
                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
                        break;
//                    case TO_CENTER:
//                        col = x1 - gradientPairs.size() + j + (cycled ? i % gradientPairs.size() : i);
//                        if (col > x1 + programWidth) break gradientIteration;
//                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
//                        col = cycled ? x2 - (i % gradientPairs.size()) + j : x2 - i + j;
//                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
//                        break;
//                    case FROM_CENTER:
//                        col = x1 - gradientPairs.size() + j + (cycled ? i % gradientPairs.size() : i);
//                        if (col > x2) break gradientIteration;
//                        if (col < x1 + programWidth) continue;
//                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
//                        col = cycled ? x2 - (i % gradientPairs.size()) + j : x2 - i + j;
//                        setPixel(y1 + i, col, gradientPairs.get(j), onlyColored);
//                        break;
                }
            }
        }

    }

    private void setPixel(int frame, int col, Pair<Color, Color> newColors, boolean onlyColored) {
        if (frame < project.framesCount() && frame >= y1 && col >= x1 && col < x2) {
            final Pixel.Frame pixelFrame = project.getPixelFrame(frame, col);
            if (onlyColored) {
                pixelFrame.setColor(
                        pixelFrame.getStartColor().equals(Color.BLACK) ? null : newColors.getKey(),
                        pixelFrame.getEndColor().equals(Color.BLACK) ? null : newColors.getValue()
                );
            } else {
                pixelFrame.setColor(newColors.getKey(), newColors.getValue());
            }
        }
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
