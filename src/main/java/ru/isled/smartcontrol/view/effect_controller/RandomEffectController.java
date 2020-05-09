package ru.isled.smartcontrol.view.effect_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.model.Project;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static ru.isled.smartcontrol.util.Util.fill;

public class RandomEffectController implements Initializable {
    private static final Logger log = LogManager.getLogger();
    private static RandomEffectController controller;
    private Project project;
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    @FXML
    private Spinner<Integer> maxLength;

    @FXML
    private Spinner<Integer> minLength;
    @FXML
    private Spinner<Integer> fill;

    @FXML
    private CheckBox fillBackground;
    @FXML
    private CheckBox oneColor;

    @FXML
    private ColorPicker mainColor;

    @FXML
    private ColorPicker bgColor;

    Alert window;

    private RandomEffectController() {
        loadDialog();
    }

    public static RandomEffectController get(Project project, int x1, int y1, int x2, int y2) {
        if (controller == null) {
            controller = new RandomEffectController();
        }
        controller.project = project;
        controller.x1 = x1;
        controller.y1 = y1;
        controller.x2 = x2;
        controller.y2 = y2;
        return controller;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainColor.setValue(Color.WHITE);
        bgColor.setValue(Color.BLACK);
        final SpinnerValueFactory.IntegerSpinnerValueFactory minFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 15, 3);
        final SpinnerValueFactory.IntegerSpinnerValueFactory maxFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 15, 7);
        minLength.setValueFactory(minFactory);
        maxLength.setValueFactory(maxFactory);
        minFactory.maxProperty().bind(maxFactory.valueProperty());
        maxFactory.minProperty().bind(minFactory.valueProperty());

        fill.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 10));
    }

    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply(minLength.getValue(), maxLength.getValue(), fill.getValue(),
                    mainColor.getValue(), oneColor.isSelected(),
                    bgColor.getValue(), fillBackground.isSelected());
        }
    }

    private void apply(int min, int max, int fill,
                       Color color, boolean oneColor,
                       Color bgColor, boolean fillBackground) {
        if (fillBackground)
            fill(project, x1, y1, x2, y2, bgColor);
        int count = (x2 - x1) * (y2 - y1) / ((max + min) / 2) * fill / 100;
        log.debug("count = " + count);
        for (int i = 0; i < count; i++) {
            int length = (int) (Math.random() * (max - min)) + min;
            int middle = length / 2;
            int x = (int) ((x2 - x1) * Math.random()) + x1;
            int y = (int) ((y2 - y1) * Math.random()) + y1;
            Color starColor = oneColor ? color : Color.hsb(Math.random() * 360, 1, 1);
            for (int j = 0; j < middle; j++) {
                set(y++, x,
                        (j == 0 ? null : bgColor.interpolate(starColor, (double) j / middle)),
                        bgColor.interpolate(starColor, ((double) j + 1) / middle)
                );
            }
            set(y++, x, starColor, starColor);
            for (int j = 0; j < middle; j++) {
                set(y++, x,
                        starColor.interpolate(bgColor, ((double) j) / middle),
                        (j == middle - 1 ? null : starColor.interpolate(bgColor, ((double) j + 1) / middle))
                );
            }
        }
    }

    private void set(int frame, int pixel, Color startColor, Color endColor) {
        if (frame >= y1 && frame < y2)
            project.getPixelFrame(frame, pixel).setColor(startColor, endColor);
    }

    public void loadDialog() {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/effect/random.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Случайная яркость\"");
        window.setHeaderText("Выберите параметры эффекта");

        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

    }
}
