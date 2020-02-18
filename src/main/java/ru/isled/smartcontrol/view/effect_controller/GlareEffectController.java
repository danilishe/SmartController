package ru.isled.smartcontrol.view.effect_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.view.CustomColorController;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GlareEffectController implements Initializable {
    Logger log = LogManager.getLogger();
    private Project project;
    private int x1, y1, x2, y2;
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
        mainColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isOpaque()) {
                mainColor.setValue(Color.color(newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
            }
        });
        bgColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isOpaque()) {
                bgColor.setValue(Color.color(newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
            }
        });
    }

    public static GlareEffectController get(Project project, int x1, int y1, int x2, int y2) {
        if (controller == null)
            controller = new GlareEffectController();
        controller.project = project;
        controller.x1 = x1;
        controller.x2 = x2;
        controller.y1 = y1;
        controller.y2 = y2;
        return controller;
    }

    @FXML
    public void changeColors() {
        final Color value = mainColor.getValue();
        mainColor.setValue(bgColor.getValue());
        bgColor.setValue(value);
    }

    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();


        if (ButtonType.OK.equals(button.get())) {
            apply(rightDirection.isSelected(),
                    mainColor.getValue(),
                    glareWidth.getValue(),
                    bgColor.getValue(), background.isSelected(),
                    traceBefore.getValue(), traceAfter.getValue(),
                    autoFrame.isSelected()
            );
        }
    }

    private void apply(boolean isRightDirection,
                       Color mainColor, int glWidth,
                       Color bgColor, boolean setBackground,
                       int tailBefore, int tailAfter,
                       boolean autoFrameAdd) {

        int fullLength = x2 - x1 + tailAfter + tailBefore + glWidth - 1;
        log.debug("fullLength = " + fullLength);


        if (autoFrameAdd) {
            y2 = Math.max(y1 + fullLength + 1, y2);
            if (project.framesCount() < y2) {
                project.setFramesCount(y2);
            }
        }
        if (setBackground) {
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {
                    project.getPixelFrame(y, x).setColor(bgColor);
                }
            }
        }


        List<Pair<Color, Color>> glare = new ArrayList<>(tailBefore + glWidth + tailAfter + 1);
        for (int i = 0; i < tailBefore; i++) { // tailBefore inserting from main color to end color
            glare.add(0, new Pair<>(
                    mainColor.interpolate(bgColor, ((double) i + 1) / tailBefore),
                    mainColor.interpolate(bgColor, ((double) i) / tailBefore)
            ));
        }
        for (int i = 0; i < glWidth; i++) {
            glare.add(new Pair<>(mainColor, mainColor));
        }
        for (int i = 0; i < tailAfter; i++) { // tailAfter adding at the end in that order too
            glare.add(new Pair<>(
                    mainColor.interpolate(bgColor, ((double) i) / tailAfter),
                    mainColor.interpolate(bgColor, ((double) i + 1) / tailAfter)
            ));
        }

        if (isRightDirection) {
            Collections.reverse(glare);
        }

        for (int y = y1; y < y2; y++) {
            final int shift = y - y1;
            int glarePos = isRightDirection ?
                    x1 - glare.size() + shift + 1
                    : x2 - shift - 1;
            for (int i = 0; i < glare.size(); i++) {
                int pxPos = glarePos + i;
                if (pxPos < x2 && pxPos >= x1) {
                    Pixel.Frame pixel = project.getPixelFrame(y, pxPos);
                    final Color c1 = glare.get(i).getKey();
                    final Color c2 = glare.get(i).getValue();
                    pixel.setColor( // tricky! we don't set color if bg is not applied
                            c1.equals(bgColor) && !setBackground ? null : c1,
                            c2.equals(bgColor) && !setBackground ? null : c2);
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
