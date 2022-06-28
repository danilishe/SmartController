package ru.isled.smartcontrol;

import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.isled.smartcontrol.controller.FileHelper;
import ru.isled.smartcontrol.controller.ProgramProperties;
import ru.isled.smartcontrol.controller.ProjectIO;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.view.Dialogs;
import ru.isled.smartcontrol.view.MainController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

//import ru.isled.smartcontrol.controller.ProjectIO;

public class SmartControl extends Application {
    private Stage mainStage = null;
    private Project project = null;
    private MainController controller;
    private List<String> lastFiles = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void loadDefaults() {
        Project.hasChangesProperty().addListener(l -> updateHeader());
        File file = new File(PROPS_PATH);
        try {
            if (file.exists()) {
                byte[] data = FileHelper.load(file);
                if (data == null) return;

                ProgramProperties props = JSON.parseObject(data, ProgramProperties.class);

                controller.zoomSlider.setValue(props.getZoom());
                mainStage.setMinHeight(MIN_HEIGHT);
                mainStage.setMinWidth(MIN_WIDTH);
                mainStage.setWidth(props.getWidth());
                mainStage.setHeight(props.getHeight());
                lastFiles.addAll(props.getLastFiles());
                updateLastFilesMenu();
            }
        } catch (Exception ignore) {
        }
    }

    private void loadLastFile(String text) {
        File lastFile = new File(text);
        if (!lastFile.exists()) {
            Dialogs.showErrorAlert(FILE_NOT_EXISTS);
            lastFiles.remove(text);
        } else {
            loadProject(lastFile);
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        Dialogs.setStage(stage);

        project = new Project();

        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/main.fxml"));
        Scene scene = new Scene(loader.load());

        controller = loader.getController();
        controller.setProject(project);
        controller.setMainApp(this);

        mainStage.setScene(scene);
        mainStage.getIcons().add(new Image("images/play.png"));
        mainStage.getScene().getStylesheets().addAll(ClassLoader.getSystemResource("css/custom.css").toExternalForm());
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(800);
        mainStage.centerOnScreen();
        mainStage.setOnCloseRequest(event -> {
            event.consume();
            exit();
        });
        loadDefaults();

        mainStage.show();
        updateHeader();
    }

    public void updateHeader() {
        mainStage.setTitle(String.format(TITLE, project.getName(), Project.hasChanges() ? "*" : ""));
    }


    public void loadProject(File fileForLoad) {
        if (Project.hasChanges())
            if (!continueAfterAskSaveFile()) return;

        if (fileForLoad == null) return;
        Project newProject = ProjectIO.load(fileForLoad);
        if (newProject == null) {
            return;
        }

        addItemToLastFiles(newProject.getFile());

        project = newProject;
        controller.setProject(project);
        updateHeader();
    }

    private void addItemToLastFiles(File file) {
        String filePath = file.getAbsolutePath();
        lastFiles.remove(filePath);

        lastFiles.add(0, filePath);

        if (lastFiles.size() > MAX_LAST_FILES_COUNT) lastFiles.remove(MAX_LAST_FILES_COUNT);

        updateLastFilesMenu();
    }

    private void updateLastFilesMenu() {
        controller.lastFiles.getItems().clear();
        lastFiles.forEach(filePath -> {
            MenuItem item = new MenuItem(filePath);
            controller.lastFiles.getItems().add(item);
            item.addEventHandler(EventType.ROOT, x -> loadLastFile(item.getText()));
        });
    }

    private boolean continueAfterAskSaveFile() {
        switch (Dialogs.askSaveProject()) {
            case YES:
                saveProject();
                if (Project.hasChanges()) return false;
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

        ProjectIO.save(project, project.getFile());
        addItemToLastFiles(project.getFile());
        Project.setHasChanges(false);
        updateHeader();
    }

    public void createNewProject() {
        if (project != null && Project.hasChanges())
            if (!continueAfterAskSaveFile()) {
                return;
            }

        project = new Project();
        controller.setProject(project);
        updateHeader();
    }

    public void exportProject() {
        File exportFile = Dialogs.export(project.getFile());
        if (exportFile == null) return;
        if (!ProjectIO.export(project, exportFile)) {
            Dialogs.showErrorAlert(EXPORT_ERROR_MESSAGE);
        }
    }

    public void exit() {
        if (Project.hasChanges())
            if (!continueAfterAskSaveFile()) return;
        saveDefaults();
        System.exit(0);
    }

    private void saveDefaults() {
        ProgramProperties props = new ProgramProperties();
        props.setHeight(mainStage.getHeight());
        props.setWidth(mainStage.getWidth());
        props.setZoom(controller.zoomSlider.getValue());
        props.setLastFiles(lastFiles);

        File file = new File(PROPS_PATH);
        boolean dirsCreated = file.getParentFile().mkdirs();

        FileHelper.save(file, JSON.toJSONString(props).getBytes());
    }
}
