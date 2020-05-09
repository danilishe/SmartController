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
import java.util.ResourceBundle;

public class FadeInEffectController implements Initializable {
    private static List<IntegerProperty> cells;
    private static int sizeX;
    private static int sizeY;
    private static FadeInEffectController controller;
    @FXML
    Slider endBright;

    @FXML
    Label endBrightLabel;

    @FXML
    Slider startBright;

    @FXML
    Label startBrightLabel;

    @FXML
    CheckBox onlyEmpty;

    Alert window;

    private FadeInEffectController() {
        loadDialog();
    }

    public static FadeInEffectController get(List<IntegerProperty> aCells, int x, int y) {
        cells = aCells;
        sizeX = x;
        sizeY = y;

        if (controller == null)
            controller = new FadeInEffectController();
        return controller;
    }

    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply((int) endBright.getValue(), (int) startBright.getValue(), onlyEmpty.isSelected());
        }
    }

    public void apply(double endBr, double startBr, boolean onlyEmpty) {

        double step = (endBr - startBr) / (sizeY - 1);


        for (int i = 0; i < sizeY; i++, startBr += step) {
            for (int j = 0; j < sizeX; j++) {
                IntegerProperty integerProperty = cells.get(i * sizeX + j);
                if (onlyEmpty) {
                    if (integerProperty.get() == 0) integerProperty.setValue((int) startBr);
                } else {
                    integerProperty.setValue((int) startBr);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startBright.valueProperty()
                .addListener(((observable, oldValue, newValue) ->
                        startBrightLabel.setText(String.valueOf(newValue.intValue()))));

        endBright.valueProperty()
                .addListener(((observable, oldValue, newValue) ->
                        endBrightLabel.setText(String.valueOf(newValue.intValue()))));
    }

    private void loadDialog() {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/effect/fadeIn.fxml"));
        loader.setController(this);
        window = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            window.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.setTitle("Эффект \"Разгорание\"");
        window.setHeaderText("Выберите параметры эффекта");

        window.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

    }
}
