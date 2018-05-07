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

    public static void preview(final byte[] data, int width, List<Integer> quantifiers) {
        Dialog preview = new Dialog();
        preview.setTitle("Предпросмотр");
        List<Shape> px = Stream
                .generate(() -> new Circle(15, Color.YELLOW))
                .limit(width)
                .collect(Collectors.toList());

        List<StackPane> objects = IntStream.range(0, width).mapToObj(i ->
                new StackPane(px.get(i), new Label("" + (i + 1)))
        ).collect(Collectors.toList());

        List<VBox> vBoxes = new ArrayList<>();
        int currentPixel = 0;

        vboxCycle:
        for (int q : quantifiers) {
            VBox vBox = new VBox(0);
            for (int i = 0; i < q; i++) {
                vBox.getChildren().add(objects.get(currentPixel));
                currentPixel++;
                if (currentPixel == width) {
                    vBoxes.add(vBox);
                    break vboxCycle;
                }
            }
            vBoxes.add(vBox);
        }

        HBox hbox = new HBox(5);
        hbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");
        hbox.getChildren().addAll(vBoxes);


        Task<Void> timer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (preview.isShowing()) {
                    long stepStartTime = new Date().getTime();
                    for (int i = 0; i < data.length; i += MAX_PIXELS_COUNT) {
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

    public static void showAboutInfo() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("О программе");
        alert.setHeaderText("SMART Control 2018");
        alert.setContentText(
                "Программа для создания и редактирования эффектов для контроллера ISLed" +
                        "\nЗнакСвет (C) 2018\nСуетин Д.Е. (C) 2018" +
                        "\nhttp://is-led.ru/" +
                        "\nВерсия " + Constants.PROGRAM_VERSION +
                        "\nПамяти JVM свободно/всего: " + Runtime.getRuntime().freeMemory() / 1_000_000 + "МБ / " + Runtime.getRuntime().totalMemory() / 1_000_000 + "МБ"
        );
        alert.showAndWait();
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
