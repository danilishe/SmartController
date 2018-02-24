package ru.isled.controlit.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static javafx.scene.control.ButtonType.*;
import static ru.isled.controlit.Constants.DEFAULT_FILE_NAME;
import static ru.isled.controlit.Constants.DEFAULT_WORK_DIRECTORY;

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

    public static ButtonBar.ButtonData askSaveProject() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Сохраненить изменения?");
        alert.setHeaderText("Имеются несохранённые изменения. Сохранить?");

        alert.getButtonTypes().setAll(YES, NO, CANCEL);

        return alert.showAndWait().get().getButtonData();
    }

    public static File saveAs(File file) {
        String fileName = file == null ? DEFAULT_FILE_NAME : file.getName();
        File parentDirectory = file == null ? new File(DEFAULT_WORK_DIRECTORY) : file.getParentFile();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialFileName(fileName);
        fileChooser.setInitialDirectory(parentDirectory);
        fileChooser.setTitle("Сохранить как...");

        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("ISLed проект для контроллера", "*.isd"));
        return fileChooser.showSaveDialog(stage);
    }

    public static void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("О программе");
        alert.setHeaderText(null);
        alert.setContentText("Программа для создания программ для контроллера ISLed. ЗнакСвет (C) 2018");
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
