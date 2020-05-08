package ru.isled.smartcontrol.view.effect_controller;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.isled.smartcontrol.model.Project;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuildingEffectController implements Initializable, MultiFrameEffect {
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

    public void apply(boolean blockFallingFromRight,
                      int bright, int blockWidth,
                      int bgBright, boolean isBgOpaque,
                      int tailBefore, int tailAfter) {

        int firstGlareLine = 0;
        int lastGlareLine = 0;
        if (isBgOpaque) cells.forEach(cell -> cell.setValue(bgBright));
        final int totalBlockWidth = tailAfter + tailBefore + blockWidth;

        // наложение бегущего блика-блока
        int blocksCountForFillingLine = (int) Math.ceil((double) sizeX / blockWidth);
        for (int block = 1; block <= blocksCountForFillingLine && lastGlareLine < sizeY; block++) {
            lastGlareLine = Math.min(firstGlareLine + totalBlockWidth + sizeX, sizeY);

            int lastGlareIndex = Math.min(lastGlareLine * sizeX, cells.size());
            int firstGlareIndex = Math.min(firstGlareLine * sizeX, cells.size() - 1);

            List<IntegerProperty> forGlare = cells.subList(firstGlareIndex, lastGlareIndex);
//            GlareEffectController.get(forGlare, sizeX, lastGlareLine - firstGlareLine)
//                    .apply(!blockFallingFromRight, bright, blockWidth, bgBright, false,
//                            tailBefore, tailAfter);

            firstGlareLine = lastGlareLine - tailAfter - (block * blockWidth);
        }

        // наложение закраски блоков
        firstGlareLine = 0;
        for (int block = 1; block <= blocksCountForFillingLine; block++) {
            int filledCellsLine = firstGlareLine + (sizeX - (block * blockWidth)) + tailBefore + 1;

            for (int y = filledCellsLine; y < sizeY; y++) {
                for (int x = 0; x < block * blockWidth; x++) {
                    try {
                        int index = blockFallingFromRight ? y * sizeX + x : (y + 1) * sizeX - x - 1;
                        cells.get(index).set(bright);
                    } catch (IndexOutOfBoundsException e) {
                        return;
                    }
                }
            }
            firstGlareLine += totalBlockWidth + sizeX - tailAfter - (block * blockWidth);
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
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("effect/building.fxml"));
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

    @Override
    public void apply(Project project, int x1, int y1, int x2, int y2) {
        Optional<ButtonType> button = window.showAndWait();
        if (ButtonType.OK.equals(button.get())) {
            apply(fromRight.isSelected(),
                    (int) effectBrightness.getValue(), blockWidth.getValue(),
                    (int) backgroundBrightness.getValue(), background.isSelected(),
                    traceBefore.getValue(), traceAfter.getValue());
        }
    }
}
