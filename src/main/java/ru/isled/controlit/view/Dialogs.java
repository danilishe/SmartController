package ru.isled.controlit.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;

import static javafx.scene.control.ButtonType.*;

public class Dialogs {
    public static ButtonBar.ButtonData askSaveProject(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Сохраненить изменения?");
        alert.setHeaderText("Имеются несохранённые изменения. Сохранить?");


        alert.getButtonTypes().setAll(YES, NO, CANCEL);

        return alert.showAndWait().get().getButtonData();

    }
}
