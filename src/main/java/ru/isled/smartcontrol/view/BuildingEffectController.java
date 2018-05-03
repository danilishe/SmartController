package ru.isled.smartcontrol.view;

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
import java.util.stream.IntStream;

public class BuildingEffectController implements Initializable {
    private static List<IntegerProperty> cells;
    private static int sizeX;
    private static int sizeY;
    private static BuildingEffectController controller;

    @FXML
    private RadioButton fromRight;

    @FXML
    private Slider effectBrightness;

    @FXML
    private Label effectBrightnessLabel;

    @FXML
    private CheckBox background;

    @FXML
    private Slider backgroundBrightness;

    @FXML
    private Label backgroundBrightnessLabel;

    @FXML
    private Spinner<Integer> blockWidth;

    @FXML
    private Spinner<Integer> traceAfter;

    @FXML
    private Spinner<Integer> traceBefore;


    Alert window;

    private BuildingEffectController() {
        loadDialog();
    }

    public static BuildingEffectController get(List<IntegerProperty> aCells, int x, int y) {
        cells = aCells;
        sizeX = x;
        sizeY = y;

        if (controller == null)
            controller = new BuildingEffectController();
        return controller;
    }

    @FXML
    public void apply() {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply(fromRight.isSelected(),
                    (int) effectBrightness.getValue(), blockWidth.getValue(),
                    (int) backgroundBrightness.getValue(), background.isSelected(),
                    traceBefore.getValue(), traceAfter.getValue());
        }
    }

    public void apply(boolean blockFallingFromRight,
                      int bright, int blockWidth,
                      final int bgBright, boolean isBgOpaque,
                      int tailBefore, int tailAfter) {

        int startLine = 0;

        int totalBlockWidth = tailAfter + tailBefore + blockWidth;
        for (int fixedBlocks = 0; fixedBlocks < Math.ceil((double) sizeX / blockWidth); fixedBlocks++) {
            int endLine = startLine + totalBlockWidth + sizeX;
            int filledBlocks = startLine + (sizeX -  (fixedBlocks * blockWidth)) + tailBefore;

            List<IntegerProperty> forGlare = cells.subList(startLine * sizeX, Math.min(endLine * sizeX, cells.size()));

            if (blockFallingFromRight) {
                GlareEffectController.get(forGlare, sizeX, endLine - startLine)
                        .apply(false, bright, blockWidth, bgBright, isBgOpaque,
                                tailBefore, tailAfter);

                for (int i = filledBlocks; i < endLine; i++) {
                    for (int j = 0; j < fixedBlocks * blockWidth; j++) {
                        cells.get(i * sizeX + j).set(bright);
                    }
                }

            }
            

            startLine = endLine - tailAfter - (fixedBlocks * blockWidth);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        blockWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        traceBefore.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
        traceAfter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));

        backgroundBrightness.valueProperty().addListener(((observable, oldValue, newValue) -> {
            backgroundBrightnessLabel.setText(String.valueOf(newValue.intValue()));
        }));
        effectBrightness.valueProperty().addListener(((observable, oldValue, newValue) -> {
            effectBrightnessLabel.setText(String.valueOf(newValue.intValue()));
        }));
    }

    public void loadDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("building.fxml"));
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
}
