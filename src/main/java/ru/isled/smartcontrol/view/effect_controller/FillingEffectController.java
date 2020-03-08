package ru.isled.smartcontrol.view.effect_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.model.Direction;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.util.TransparentColorFilter;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static ru.isled.smartcontrol.util.Util.fill;

public class FillingEffectController implements Initializable {
    private static final Logger log = LogManager.getLogger();
    private static FillingEffectController controller;
    Alert window;
    @FXML
    ColorPicker mainColor;
    @FXML
    CheckBox background;
    @FXML
    ColorPicker bgColor;
    @FXML
    CheckBox autoFrame;
    private Project project;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
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
    private Spinner<Integer> transitionalWidth;

    private FillingEffectController() {
        loadDialog();
        mainColor.valueProperty().addListener(new TransparentColorFilter(mainColor));
        bgColor.valueProperty().addListener(new TransparentColorFilter(bgColor));
    }

    public static FillingEffectController get(Project project, int x1, int y1, int x2, int y2) {
        if (controller == null)
            controller = new FillingEffectController();
        controller.project = project;
        controller.x1 = x1;
        controller.y1 = y1;
        controller.x2 = x2;
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
            apply(mainColor.getValue(), bgColor.getValue(),
                    background.isSelected(), transitionalWidth.getValue(), getDirection(),
                    autoFrame.isSelected());
        }
    }

    private Direction getDirection() {
        if (toRight.isSelected()) {
            return Direction.RIGHT;
        } else if (toLeft.isSelected()) {
            return Direction.LEFT;
        } else if (toCenter.isSelected()) {
            return Direction.TO_CENTER;
        } else {
            return Direction.FROM_CENTER;
        }
    }

    private void apply(Color color, Color bgColor,
                       boolean isBgOpaque, int transitionalWidth, Direction dir,
                       boolean autoFrameAdd) {

        final int width = x2 - x1;
        int half = width / 2;
        log.debug("half = " + half);
        log.debug("width = " + width);
        int programLength = y1 + width + transitionalWidth - 1;
        log.debug("programLength = " + programLength);
        if (dir == Direction.TO_CENTER || dir == Direction.FROM_CENTER)
            programLength = programLength - half;
        if (programLength >= project.framesCount())
            project.setFramesCount(programLength);
        if (programLength < y2) {
            fill(project, x1, programLength, x2, y2, color);
        }
        // reuse glare effect cause fill is glare with width == full width
        switch (dir) {
            case RIGHT:
                GlareEffectController.get(project, x1, y1, x2, programLength).apply(true,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);
                break;
            case LEFT:
                if (autoFrameAdd) project.setFramesCount(width + transitionalWidth);
                GlareEffectController.get(project, x1, y1, x2, programLength).apply(false,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);
                break;
            case TO_CENTER:
                GlareEffectController.get(project, x1, y1, x2 - half, programLength).apply(true,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);

                GlareEffectController.get(project, x1 + half, y1, x2, programLength).apply(false,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);
                break;
            case FROM_CENTER:
                GlareEffectController.get(project, x1 + half, y1, x2, programLength).apply(true,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);

                GlareEffectController.get(project, x1, y1, x2 - half, programLength).apply(false,
                        color, width, bgColor, isBgOpaque, transitionalWidth, 0,
                        false);
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transitionalWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
        mainColor.setValue(Color.WHITE);
        bgColor.setValue(Color.BLACK);
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

}
