package ru.isled.controlit.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.controlsfx.control.RangeSlider;
import ru.isled.controlit.Constants;

import java.io.File;

import static javafx.scene.control.ButtonType.*;
import static ru.isled.controlit.Constants.*;

public class Dialogs {

    private static Stage stage;

    public static void setStage(Stage stage) {
        Dialogs.stage = stage;
    }

    public static File loadFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(DEFAULT_WORK_DIRECTORY));
        fileChooser.setTitle("Открыть...");

        return fileChooser.showOpenDialog(stage);

    }

    public static Pair<Integer, Integer> getFadeInProperties() {
        Dialog dialog = new Dialog();
        dialog.initOwner(stage);
        RangeSlider rangeSlider = new RangeSlider(0, 255, 0, 255);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setShowTickMarks(true);

        Label hLabel = new Label("255");
        hLabel.setMinWidth(30);
        hLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.highValueProperty())
        );

        Label lLabel = new Label("0");
        lLabel.setMinWidth(30);
        lLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.lowValueProperty())
        );


        HBox hBox = new HBox(5, lLabel, rangeSlider, hLabel);
        hBox.setMinWidth(250);

        dialog.setHeaderText("Выберите максимум/минимум эффекта");
        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
        return new Pair<>((int) rangeSlider.getLowValue(), (int) rangeSlider.getHighValue());
    }

    public static ButtonBar.ButtonData askSaveProject() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Сохраненить изменения?");
        alert.setHeaderText("Имеются несохранённые изменения. Сохранить?");

        alert.getButtonTypes().setAll(YES, NO, CANCEL);

        return alert.showAndWait().get().getButtonData();
    }

    public static File saveAs(File file) {
        String fileName = file == null ? DEFAULT_PROJECT_FILE_NAME : file.getName();
        File parentDirectory = file == null ? new File(DEFAULT_WORK_DIRECTORY) : file.getParentFile();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialFileName(fileName);
        fileChooser.setInitialDirectory(parentDirectory);
        fileChooser.setTitle("Сохранить как...");

        fileChooser.setSelectedExtensionFilter(Constants.PROJECT_EXT);
        return fileChooser.showSaveDialog(stage);
    }

    public static File export(File file) {
        File parentDirectory = file == null ? new File(DEFAULT_WORK_DIRECTORY) : file.getParentFile();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialFileName(DEFAULT_EXPORT_FILE_NAME);
        fileChooser.setInitialDirectory(parentDirectory);
        fileChooser.setTitle("Экспорт...");

        fileChooser.setSelectedExtensionFilter(Constants.BIN_EXT);
        return fileChooser.showSaveDialog(stage);
    }

    public static void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("О программе");
        alert.setHeaderText(null);
        alert.setContentText("Программа для создания программ для контроллера ISLed. ЗнакСвет (C) 2018\nhttp://is-led.ru\nВерсия " + Constants.PROGRAM_VERSION);
        alert.showAndWait();
    }


    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
