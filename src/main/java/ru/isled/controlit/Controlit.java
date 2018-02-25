package ru.isled.controlit;

import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.isled.controlit.controller.ProjectLoader;
import ru.isled.controlit.model.LedFrame;
import ru.isled.controlit.model.Project;
import ru.isled.controlit.view.Dialogs;
import ru.isled.controlit.view.MainController;

import java.io.File;
import java.io.IOException;

import static ru.isled.controlit.Constants.DEFAULT_FRAMES_COUNT;

public class Controlit extends Application {
    private Stage mainStage = null;
    private Project project = null;
    private MainController controller = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        Dialogs.setStage(stage);

        loadViewAndSetIcons();

        getDefaultProject();

        mainStage.show();

    }

    private void loadViewAndSetIcons() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/main.fxml"));

        Parent root = loader.load();
        mainStage.setScene(new Scene(root));
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("images/play.png")));
        mainStage.getScene().getStylesheets().addAll(getClass().getResource("css/custom.css").toExternalForm());
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(720);
        mainStage.centerOnScreen();
        mainStage.setOnCloseRequest(event -> exit());
        controller = loader.getController();
        controller.setMainApp(this);

    }

    public void updateHeader() {
        String name = project.getName() == null ? "- несохранённый проект -" : project.getName();
        String unsavedMark = project.hasUnsavedChanges() ? "*" : "";
        mainStage.setTitle("ISLed Control It! " + name + unsavedMark);
    }

    public void getDefaultProject() {
        if (false) {
            // todo load last project
        } else {
            createNewProject();
        }
    }

    public void loadProject() {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) return;
        File fileForLoad = Dialogs.loadFile();
        if (fileForLoad == null) return;

        //todo
        Project newProject = ProjectLoader.load(fileForLoad);
        if (newProject == null) {
            Dialogs.showErrorAlert("Неверный формат файла или данная версия не поддерживается программой!");
            return;
        }
        registerProjToControllerAndListener();
    }

    private boolean continueAfterAskSaveFile() {
        switch (Dialogs.askSaveProject()) {
            case YES:
                saveProject();
            case NO:
                return true;

            case CANCEL_CLOSE:
            default:
                return false;
        }

    }


    public void saveProject() {
        if (!project.hasName()) {
            File file = Dialogs.saveAs(project.getFile());
            if (file == null) return;
            project.setFileName(file);
        }

        // todo
        String json = JSON.toJSONString(project);
        System.out.println("json = " + json);
        project.setHasChanges(false);
        updateHeader();
    }

    public void createNewProject() {
        if (project != null && project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) {
                return;
            }

        project = new Project();
        for (int i = 0; i < DEFAULT_FRAMES_COUNT; i++) {
            project.addRow(new LedFrame());
        }
        registerProjToControllerAndListener();
    }

    private void registerProjToControllerAndListener() {
        controller.setProject(project);
        project.hasChangesProperty().addListener(l -> updateHeader());
        updateHeader();
    }

    public void exportProject() {
        // todo
    }

    public void exit() {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) return;
        System.exit(0);
    }
}
