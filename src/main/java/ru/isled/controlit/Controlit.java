package ru.isled.controlit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.isled.controlit.model.Project;
import ru.isled.controlit.view.MainView;

public class Controlit extends Application {
    private Stage mainStage = null;
    private Project project = null;
    private MainView controller = null;


    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        loadViewAndSetIcons();
        getDefaultProject();
        updateHeader();

        mainStage.show();

    }

    private void loadViewAndSetIcons() throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/main.fxml"));
        controller = loader.getController();
        controller.setMainApp(this);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("images/play.png")));
        Parent root = loader.load();
        mainStage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void updateHeader() {
        mainStage.setTitle("ISLed Control It! " + project.getName() + (project.hasUnsavedChanges() ? "*" : ""));
    }

    public void getDefaultProject() {
        if (false) {
            // todo load last project
        } else {
            createNewProject();
        }
    }

    public void saveProject() {
        // todo
        project.setHasChanges(false);
        updateHeader();
    }

    public void createNewProject() {
        // todo
        project = new Project();
    }

    public void exit() {
        System.exit(0);
    }
}
