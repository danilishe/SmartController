package ru.isled.smartcontrol.view.effect_controller;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FillingEffectController implements Initializable {
    private static List<IntegerProperty> cells;
    private static int sizeX;
    private static int sizeY;
    private static FillingEffectController controller;
    Alert window;
    @FXML
    private RadioButton toLeft;
    @FXML
    private ToggleGroup direction;
    @FXML
    private RadioButton toRight;
    @FXML
    private RadioButton toCenter;
    @FXML
    private RadioButton fromCenter;
    @FXML
    private Slider effectBrightness;
    @FXML
    private Label effectBrightnessLabel;
    @FXML
    private CheckBox background;
    @FXML
    private HBox backgroundBrightnessBox;
    @FXML
    private Slider backgroundBrightness;
    @FXML
    private Label backgroundBrightnessLabel;
    @FXML
    private Spinner<Integer> transitionalWidth;

    private FillingEffectController() {
        loadDialog();
    }

    public static FillingEffectController get(List<IntegerProperty> aCells, int x, int y) {
        cells = aCells;
        sizeX = x;
        sizeY = y;

        if (controller == null)
            controller = new FillingEffectController();
        return controller;
    }


    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply((int) effectBrightness.getValue(), (int) backgroundBrightness.getValue(),
                    background.isSelected(), transitionalWidth.getValue(), getDirection());
        }
    }

    private Direction getDirection() {
        if (toRight.isSelected()) {
            return Direction.RIGHT;
        } else if (toLeft.isSelected()) {
            return Direction.LEFT;
        } else if (toCenter.isSelected()) {
            return Direction.CENTER;
        } else {
            return Direction.FROM_CENTER;
        }
    }

    public void apply(int glBright, final int bgBright,
                      boolean isBgOpaque, int transitionalWidth, Direction dir) {

        int half = (int) Math.ceil((double) sizeX / 2);
        switch (dir) {
            case RIGHT:
                GlareEffectController.get(cells, sizeX, sizeY).apply(
                        true, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );
                break;
            case LEFT:
                GlareEffectController.get(cells, sizeX, sizeY).apply(
                        false, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );
                break;
            case CENTER:
                GlareEffectController.get(getLeftHalf(), half, sizeY).apply(
                        true, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );
                GlareEffectController.get(getRightHalf(), half, sizeY).apply(
                        false, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );

                break;
            case FROM_CENTER:
                GlareEffectController.get(getLeftHalf(), half, sizeY).apply(
                        false, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );
                GlareEffectController.get(getRightHalf(), half, sizeY).apply(
                        true, glBright, 999, bgBright, isBgOpaque, transitionalWidth, 0
                );
                break;
        }
    }

    private List<IntegerProperty> getRightHalf() {
        return IntStream.range(0, cells.size())
                .filter(i -> i % sizeX >= sizeX / 2)
                .mapToObj(i -> cells.get(i))
                .collect(Collectors.toList());
    }

    // 0 1 2 3 4 5 6 7  = 4
//  0 1 2 3 4 5 6   = 3
    private List<IntegerProperty> getLeftHalf() {
        return IntStream.range(0, cells.size())
                .filter(i -> i % sizeX < (double) sizeX / 2)
                .mapToObj(i -> cells.get(i))
                .collect(Collectors.toList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transitionalWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));

        backgroundBrightness.valueProperty().addListener(((observable, oldValue, newValue) -> {
            backgroundBrightnessLabel.setText(String.valueOf(newValue.intValue()));
        }));
        effectBrightness.valueProperty().addListener(((observable, oldValue, newValue) -> {
            effectBrightnessLabel.setText(String.valueOf(newValue.intValue()));
        }));
    }

    private void loadDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fill.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Наполнение\"");
        window.setHeaderText("Выберите параметры эффекта");

        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

    }

    public enum Direction {
        LEFT, RIGHT, CENTER, FROM_CENTER
    }
}
