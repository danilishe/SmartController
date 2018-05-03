package ru.isled.smartcontrol;

import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.isled.smartcontrol.controller.FileHelper;
import ru.isled.smartcontrol.controller.ProjectIO;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.ProgramProperties;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.view.Dialogs;
import ru.isled.smartcontrol.view.MainController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class SmartControl extends Application {
    public static final int MAX_LAST_FILES_COUNT = 10;
    private Stage mainStage = null;
    private Project project = null;
    private MainController controller = null;
    private List<String> lastFiles = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    private void loadDefaults() {
        File file = new File(PROPS_PATH);
        try {
            if (file.exists()) {
                byte[] data = FileHelper.load(file);
                if (data == null) return;

                ProgramProperties props = JSON.parseObject(data, ProgramProperties.class);

                controller.zoomSlider.setValue(props.getZoom());
                controller.showBright.setSelected(props.isShowBright());
                controller.showDigits.setSelected(props.isShowDigits());
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
            Dialogs.showErrorAlert("Файл не существует!");
            lastFiles.remove(lastFiles.indexOf(text));
        } else {
            loadProject(lastFile);
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        Dialogs.setStage(stage);

        loadViewAndSetIcons();

        createNewProject();

        loadDefaults();

        mainStage.show();

    }

    private void loadViewAndSetIcons() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/main.fxml"));

        Scene scene = new Scene(loader.load());
        mainStage.setScene(scene);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("images/play.png")));
        mainStage.getScene().getStylesheets().addAll(getClass().getResource("css/custom.css").toExternalForm());
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(720);
        mainStage.centerOnScreen();
        mainStage.setOnCloseRequest(event -> {
            event.consume();
            exit();
        });
        controller = loader.getController();
        controller.setMainApp(this);

    }

    public void updateHeader() {
        String name = project.getName() == null ? "- несохранённый проект -" : project.getName();
        String unsavedMark = project.hasUnsavedChanges() ? "*" : "";
        mainStage.setTitle("ISLed SMART Control " + name + unsavedMark);
    }


    public void loadProject(File fileForLoad) {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) return;

        if (fileForLoad == null) return;

        Project newProject = ProjectIO.load(fileForLoad);
        if (newProject == null) {
            Dialogs.showErrorAlert("Неверный формат файла или данная версия не поддерживается программой!");
            return;
        }

        addItemToLastFiles(newProject.getFile());

        project = newProject;
        registerProjToControllerAndListener();
    }

    private void addItemToLastFiles(File file) {
        String filePath = file.getAbsolutePath();
        if (lastFiles.contains(filePath)) {
            lastFiles.remove(filePath);
        }

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
                if (project.hasUnsavedChanges()) return false;
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
        project.setHasChanges(false);
        updateHeader();
    }

    public void createNewProject() {
        if (project != null && project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) {
                return;
            }

        project = new Project();
        project.setFrameCount(DEFAULT_FRAMES_COUNT);
        project.setChanelCount(DEFAULT_PIXEL_COUNT);

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
        File exportFile = Dialogs.export(project.getFile());
        if (exportFile == null) return;
        ProjectIO.export(project, exportFile);
    }

    public void exit() {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) return;
        saveDefaults();
        System.exit(0);
    }

    private void saveDefaults() {
        ProgramProperties props = new ProgramProperties();
        props.setHeight(mainStage.getHeight());
        props.setWidth(mainStage.getWidth());
        props.setShowBright(controller.showBright.isSelected());
        props.setShowDigits(controller.showDigits.isSelected());
        props.setZoom(controller.zoomSlider.getValue());
        props.setLastFiles(lastFiles);

        File file = new File(PROPS_PATH);
        boolean dirsCreated = file.getParentFile().mkdirs();

        FileHelper.save(file, JSON.toJSONString(props).getBytes());
    }
}
