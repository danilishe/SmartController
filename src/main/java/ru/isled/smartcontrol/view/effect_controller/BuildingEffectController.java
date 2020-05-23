package ru.isled.smartcontrol.view.effect_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import ru.isled.smartcontrol.model.Direction;
import ru.isled.smartcontrol.model.effect.EffectController;
import ru.isled.smartcontrol.model.effect.multiframe.EffectParameters;
import ru.isled.smartcontrol.util.SimpleValueScroller;
import ru.isled.smartcontrol.util.TransparentColorFilter;
import ru.isled.smartcontrol.view.CustomColorController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuildingEffectController implements Initializable, EffectController {
    private static BuildingEffectController controller;

    @FXML
    private RadioButton fromRight;

    @FXML
    private CheckBox setBgColor;

    @FXML
    private ColorPicker bgColorPicker;
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Spinner<Integer> blockWidth;

    @FXML
    private Spinner<Integer> traceAfter;

    @FXML
    private Spinner<Integer> traceBefore;

    @FXML
    private CheckBox autoFrame;

    @FXML
    private CheckBox onlyEmpty;

    Alert window;

    private BuildingEffectController() {
        loadDialog();
    }

    public static EffectController get() {
        if (controller == null)
            controller = new BuildingEffectController();
        return controller;
    }

    /**
     * только логика самого эффекта в 2х цветах
     **/
    private List<List<Pair<Color, Color>>> apply(int blockWidth, int traceBefore, int traceAfter, int width, Direction direction) {

        return new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blockWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        blockWidth.setOnScroll(new SimpleValueScroller(blockWidth));
        traceBefore.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
        traceBefore.setOnScroll(new SimpleValueScroller(traceBefore));
        traceAfter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
        traceAfter.setOnScroll(new SimpleValueScroller(traceAfter));
        bgColorPicker.setValue(Color.BLACK);
        bgColorPicker.valueProperty().addListener(new TransparentColorFilter(bgColorPicker));
        bgColorPicker.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
        colorPicker.valueProperty().addListener(new TransparentColorFilter(colorPicker));
        colorPicker.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
    }

    public void loadDialog() {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/effect/building.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Сборка\"");
        window.setHeaderText("Выберите параметры эффекта");

        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

    }

    public void apply() {

    }

    @Override
    public Optional<EffectParameters> getParameters() {
        return window.showAndWait()
                .map(button -> {
                   if (button == ButtonType.OK) {
                       return EffectParameters.builder()
                               .direction(fromRight.isSelected() ? Direction.RIGHT : Direction.LEFT)
                               .blockWidth(blockWidth.getValue())
                               .traceBefore(traceBefore.getValue())
                               .traceAfter(traceAfter.getValue())
                               .build();
                   } else return null;
                });

    }
}
