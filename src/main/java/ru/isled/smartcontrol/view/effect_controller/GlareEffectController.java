package ru.isled.smartcontrol.view.effect_controller;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import ru.isled.smartcontrol.view.CustomColorController;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlareEffectController implements Initializable {
    private static List<IntegerProperty> cells;
    private static int sizeX;
    private static int sizeY;
    private static GlareEffectController controller;
    @FXML
    ToggleGroup direction;
    @FXML
    ColorPicker mainColor;
    @FXML
    CheckBox background;
    @FXML
    ColorPicker bgColor;
    @FXML
    Label backgroundBrightnessLabel;
    @FXML
    Label effectBrightnessLabel;
    @FXML
    Spinner<Integer> glareWidth;
    @FXML
    Spinner<Integer> traceAfter;
    @FXML
    Spinner<Integer> traceBefore;
    @FXML
    RadioButton rightDirection;
    @FXML
    CheckBox autoFrame;

    Alert window;

    private GlareEffectController() {
        loadDialog();
    }

    public static GlareEffectController get(List<IntegerProperty> aCells, int x, int y) {
        cells = aCells;
        sizeX = x;
        sizeY = y;

        if (controller == null)
            controller = new GlareEffectController();
        return controller;
    }


    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply(rightDirection.isSelected(),
                    mainColor.getValue(),
                    glareWidth.getValue(),
                    bgColor.getValue(),
                    background.isSelected(),
                    traceBefore.getValue(), traceAfter.getValue(),
                    autoFrame.isSelected()
            );
        }
    }

    public void apply(boolean isRightDirection,
                      Color mainColor, int glWidth,
                      Color bgColor, boolean isBgOpaque,
                      int tailBefore, int tailAfter,
                      boolean autoFrameAdd) {

        List<Integer> glare = IntStream.generate(() -> glBright).limit(glWidth).boxed().collect(Collectors.toList());

        if (isBgOpaque) {
            cells.forEach(cell -> cell.setValue(bgBright));
        }

        if (tailBefore == 1) {
//            glare.add(PixelEffect.FadeIn.index());
        } else if (tailBefore > 1) {
            int step = (glBright - bgBright) / (tailBefore + 1);
            for (int i = 1; i <= tailBefore; i++) {
                glare.add(glBright - i * step);
            }
        }

        if (tailAfter == 1) {
//            glare.add(0, PixelEffect.FadeOut.index());
        } else if (tailAfter > 1) {
            int step = (glBright - bgBright) / (tailAfter + 1);
            for (int i = 1; i <= tailAfter; i++) {
                glare.add(0, glBright - i * step);
            }
        }

        if (!isRightDirection) {
            Collections.reverse(glare);
        }

        int lastRow = Math.min(sizeY, sizeX + glare.size());
        for (int y = 0; y < lastRow; y++) {
            for (int i = 0; i < glare.size(); i++) {
                int currentGlareCellNum = y - i;
                if (currentGlareCellNum < sizeX && currentGlareCellNum >= 0) {
                    if (isRightDirection)
                        cells.get(y * sizeX + currentGlareCellNum).setValue(glare.get(glare.size() - i - 1));
                    else
                        cells.get(y * sizeX + sizeX - currentGlareCellNum - 1).setValue(glare.get(i));
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        glareWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        traceBefore.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
        traceAfter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
    }

    public void loadDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("glare.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Блик\"");
        window.setHeaderText("Выберите параметры эффекта");

        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        mainColor.setValue(Color.WHITE);
        mainColor.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
        bgColor.setValue(Color.BLACK);
        bgColor.getCustomColors().addAll(CustomColorController.getCustomColorsPalette());
    }
}
