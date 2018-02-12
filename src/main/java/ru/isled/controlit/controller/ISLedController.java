package ru.isled.controlit.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.isled.controlit.view.MainController;

public class ISLedController extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/main.fxml"));


        Parent root = loader.load();

        mainStage.setTitle("ISLed MainController ");
        mainStage.setScene(new Scene(root));
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/play.png")));

        mainStage.show();

        MainController controller = loader.getController();
        controller.setMainApp(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
