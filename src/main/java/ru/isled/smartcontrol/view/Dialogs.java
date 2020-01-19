package ru.isled.smartcontrol.view;

import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javafx.scene.control.ButtonType.*;
import static ru.isled.smartcontrol.Constants.*;

public class Dialogs {

    public static final String MAX = "max";
    public static final String DIRECTION = "direction";
    public static final String MIN = "min";
    public static final String ONLY_NEW = "only new";
    private static Stage stage;
    private static Alert alert;

    public static void setStage(Stage stage) {
        Dialogs.stage = stage;
    }

    public static File loadFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(DEFAULT_WORK_DIRECTORY));
        fileChooser.setTitle("Открыть...");
        fileChooser.getExtensionFilters().setAll(PROJECT_EXT);

        return fileChooser.showOpenDialog(stage);

    }

    public static void preview(Project project) {
        Dialog<Void> preview = new Dialog<>();
        preview.setTitle("Предпросмотр");
        // fixme как считать цветные пиксели?
        final int channelsCount = Math.min(project.getChannelsCount(), MAX_CHANNELS_COUNT);

        List<Shape> px = Stream
                .generate(() -> new VBox(5))
                .limit(channelsCount)
                .collect(Collectors.toList());

        List<StackPane> objects = IntStream
                .range(0, channelsCount)
                .mapToObj(i -> {
                            Circle circle = new Circle(15, Color.BLACK);
                            Label label = new Label("" + (i + 1));
                            label.setTextFill(Color.DEEPSKYBLUE);
                            return new StackPane(circle, label);
                        }
                ).collect(Collectors.toList());

        List<VBox> vBoxes = new ArrayList<>();
        int currentPixel = 0;
        groupPixels:
        for (int i = 0; i < project.getPixelCount(); i++) {
            final Pixel pixel = project.getPixels().get(i);
            for (int j = 0; j < pixel.getQuantifier(); j++) {
                currentPixel += pixel.isRgb() ? 3 : 1;
                if (currentPixel > channelsCount) break groupPixels;
                if (pixel.isRgb()) {
                    vBoxes.add(new)
                }
            }
        }

        HBox hbox = new HBox(5);
        hbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");
        hbox.getChildren().addAll(vBoxes);


        Task<Void> timer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (preview.isShowing()) {
                    long stepStartTime = new Date().getTime();
                    for (int i = 0; i < data.length; i += MAX_CHANNELS_COUNT) {
                        long stepEndTime = new Date().getTime() - stepStartTime;
                        updateMessage(LocalTime.ofNanoOfDay(stepEndTime * 1_000_000).format(DateTimeFormatter.ISO_TIME));
                        for (int j = 0; j < width; j++) {
                            double opacity = (double) ((int) data[i + j] & 0xFF) / MAX_BRIGHT;
                            px.get(j).setOpacity(opacity);
                        }
                        Thread.sleep(BASE_FRAME_LENGTH);
                    }
                }
                return null;
            }
        };

        Label label = new Label();
        label.textProperty().bind(timer.messageProperty());
//        timer.setOnSucceeded((s) -> label.textProperty().unbind());
        VBox vBox = new VBox(5, label, hbox);
        preview.getDialogPane().setContent(vBox);
        preview.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        preview.initOwner(stage);
        preview.show();
        Thread thread = new Thread(timer);
        thread.setDaemon(true);
        thread.start();


    }

    public static ButtonBar.ButtonData askSaveProject() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
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

        fileChooser.getExtensionFilters().setAll(Constants.PROJECT_EXT);
        return fileChooser.showSaveDialog(stage);
    }

    public static File export(File file) {
        File parentDirectory = file == null ? new File(DEFAULT_WORK_DIRECTORY) : file.getParentFile();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialFileName(DEFAULT_EXPORT_FILE_NAME);
        fileChooser.setInitialDirectory(parentDirectory);
        fileChooser.setTitle("Экспорт...");

        fileChooser.getExtensionFilters().setAll(Constants.BIN_EXT);
        return fileChooser.showSaveDialog(stage);
    }

    public static Optional<ButtonType> showErrorAlert(String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
