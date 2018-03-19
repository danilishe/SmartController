package ru.isled.smartcontrol.view;

import javafx.beans.binding.Bindings;
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
import javafx.util.Pair;
import org.controlsfx.control.RangeSlider;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.Effect;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        return fileChooser.showOpenDialog(stage);

    }

    public static Pair<Integer, Integer> getFadeInProperties() {
        Dialog dialog = new Dialog();
        dialog.initOwner(stage);
        RangeSlider rangeSlider = new RangeSlider(MIN_BRIGHT, MAX_BRIGHT, MIN_BRIGHT, MAX_BRIGHT);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setShowTickMarks(true);

        Label hLabel = new Label("" + MAX_BRIGHT);
        hLabel.setMinWidth(30);
        hLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.highValueProperty())
        );

        Label lLabel = new Label("" + MIN_BRIGHT);
        lLabel.setMinWidth(30);
        lLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.lowValueProperty())
        );


        HBox hBox = new HBox(5, lLabel, rangeSlider, hLabel);
        hBox.setPrefWidth(250);

        dialog.setHeaderText("Выберите максимум/минимум эффекта");
        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
        return new Pair<>((int) rangeSlider.getLowValue(), (int) rangeSlider.getHighValue());
    }

    public static Map<String, Integer> getGlareOptions() {
        Dialog dialog = new Dialog();
        dialog.initOwner(stage);

        CheckBox inverse = new CheckBox("Негативный блик");
        CheckBox onlyGlare = new CheckBox("Наложить только блик");

        ToggleGroup direction = new ToggleGroup();
        RadioButton rightToLeft = new RadioButton("Справа налево");
        RadioButton leftToRight = new RadioButton("Слева направо");
        leftToRight.setSelected(true);
        rightToLeft.setToggleGroup(direction);
        leftToRight.setToggleGroup(direction);
        HBox radioButtons = new HBox(5, leftToRight, rightToLeft);

        RangeSlider rangeSlider = new RangeSlider(MIN_BRIGHT, MAX_BRIGHT, MIN_BRIGHT, MAX_BRIGHT);
        rangeSlider.setShowTickMarks(true);
        rangeSlider.setShowTickLabels(true);

        Label hLabel = new Label("" + MAX_BRIGHT);
        hLabel.setMinWidth(30);
        hLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.highValueProperty())
        );

        Label lLabel = new Label("" + MIN_BRIGHT);
        lLabel.setMinWidth(30);
        lLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.lowValueProperty())
        );


        HBox rangeControls = new HBox(5, lLabel, rangeSlider, hLabel);
        rangeControls.setPrefWidth(250);

        VBox optionsContent = new VBox(5, inverse, onlyGlare, radioButtons, rangeControls);
        dialog.setHeaderText("Выберите параметры эффекта");
        dialog.getDialogPane().setContent(optionsContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();

        Map<String, Integer> result = new HashMap<>();
        result.put(MIN, (int) rangeSlider.getLowValue());
        result.put(MAX, (int) rangeSlider.getHighValue());
        int dir = leftToRight.isSelected() ? Effect.Options.Вправо.ordinal() : Effect.Options.Влево.ordinal();
        dir = inverse.isSelected() ? -dir : dir;

        result.put(DIRECTION, dir);

        if (onlyGlare.isSelected()) result.put(ONLY_NEW, 1);

        return result;
    }

    public static Map<String, Integer> getFillOptions() {
        Dialog dialog = new Dialog();
        dialog.initOwner(stage);

        CheckBox inverse = new CheckBox("Негативное наполнение");
        CheckBox onlyGlare = new CheckBox("Только наполнение");

        ToggleGroup direction = new ToggleGroup();
        RadioButton toLeft = new RadioButton("Налево");
        RadioButton toRight = new RadioButton("Направо");
        RadioButton toCenter = new RadioButton("К центру");
        RadioButton fromCenter = new RadioButton("Из центра");
        toRight.setSelected(true);
        toLeft.setToggleGroup(direction);
        toRight.setToggleGroup(direction);
        toCenter.setToggleGroup(direction);
        fromCenter.setToggleGroup(direction);
        HBox radioButtons = new HBox(5, toRight, toLeft, toCenter, fromCenter);

        RangeSlider rangeSlider = new RangeSlider(MIN_BRIGHT, MAX_BRIGHT, MIN_BRIGHT, MAX_BRIGHT);
        rangeSlider.setShowTickMarks(true);
        rangeSlider.setShowTickLabels(true);

        Label hLabel = new Label("" + MAX_BRIGHT);
        hLabel.setMinWidth(30);
        hLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.highValueProperty())
        );

        Label lLabel = new Label("" + MIN_BRIGHT);
        lLabel.setMinWidth(30);
        lLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.lowValueProperty())
        );


        HBox rangeControls = new HBox(5, lLabel, rangeSlider, hLabel);
        rangeControls.setPrefWidth(250);

        VBox optionsContent = new VBox(5, inverse, onlyGlare, radioButtons, rangeControls);
        dialog.setHeaderText("Параметры эффекта \"Наполнение\"");
        dialog.getDialogPane().setContent(optionsContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();

        Map<String, Integer> result = new HashMap<>();
        result.put(MIN, (int) rangeSlider.getLowValue());
        result.put(MAX, (int) rangeSlider.getHighValue());
        int dir = toRight.isSelected() ? Effect.Options.Вправо.ordinal() :
                toLeft.isSelected() ? Effect.Options.Влево.ordinal() :
                toCenter.isSelected() ? Effect.Options.В_центр.ordinal() :
                Effect.Options.Из_центра.ordinal();
        dir = inverse.isSelected() ? -dir : dir;

        result.put(DIRECTION, dir);

        if (onlyGlare.isSelected()) result.put(ONLY_NEW, 1);

        return result;
    }

    public static void preview(final byte[] data, int width) {
        Dialog preview = new Dialog();
        preview.setTitle("Предпросмотр");
        List<Shape> px = Stream
                .generate(() -> new Circle(15, Color.YELLOW))
                .limit(width)
                .collect(Collectors.toList());
        HBox hbox = new HBox(5);
        hbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");
        List<StackPane> objects = IntStream.range(0, width).mapToObj(i ->
                new StackPane(px.get(i), new Label("" + (i + 1)))
        ).collect(Collectors.toList());

        hbox.getChildren().addAll(objects);


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


    public static Pair<Integer, Integer> getFadeOutProperties() {
        Dialog dialog = new Dialog();
        dialog.initOwner(stage);
        RangeSlider rangeSlider = new RangeSlider(-MAX_BRIGHT, MIN_BRIGHT, -MAX_BRIGHT, MIN_BRIGHT);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setShowTickMarks(true);

        Label hLabel = new Label("" + MIN_BRIGHT);
        hLabel.setMinWidth(30);
        hLabel.textProperty().bind(
                Bindings.format("%.0f", rangeSlider.highValueProperty())
        );

        Label lLabel = new Label("" + MAX_BRIGHT);
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
        return new Pair<>((int) -rangeSlider.getLowValue(), (int) -rangeSlider.getHighValue());
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
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("О программе");
        alert.setHeaderText("Control It! 2018");
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
