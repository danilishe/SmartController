package ru.isled.smartcontrol.view.effect_controller;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class ChaosEffectController implements Initializable {
    private static List<IntegerProperty> cells;
    private static int sizeX;
    private static int sizeY;
    private static ChaosEffectController controller;

    @FXML
    private Slider maxBright;

    @FXML
    private Label maxBrightLabel;

    @FXML
    private Slider minBright;

    @FXML
    private Label minBrightLabel;

    @FXML
    private CheckBox onlyEmpty;


    Alert window;

    private ChaosEffectController() {
        loadDialog();
    }

    public static ChaosEffectController get(List<IntegerProperty> aCells, int x, int y) {
        cells = aCells;
        sizeX = x;
        sizeY = y;

        if (controller == null)
            controller = new ChaosEffectController();
        return controller;
    }


//    @FXML
//    public void initialize() {
//
//    }

    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply((int) minBright.getValue(), (int) maxBright.getValue(), onlyEmpty.isSelected());
        }
    }

    public void apply(int a, int b, boolean fillOnlyEmpty) {
        Random random = new Random();
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        int bound = Math.abs(max - min + 1);
        cells.forEach(cell -> {
            int val = random.nextInt(bound) + min;
            if (fillOnlyEmpty) {
                if (cell.get() == 0) cell.setValue(val);
            } else {
                cell.setValue(val);
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minBright.valueProperty().addListener(((observable, oldValue, newValue) -> {
            minBrightLabel.setText(String.valueOf(newValue.intValue()));
        }));
        maxBright.valueProperty().addListener(((observable, oldValue, newValue) -> {
            maxBrightLabel.setText(String.valueOf(newValue.intValue()));
        }));
    }

    public void loadDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("random.fxml"));
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
