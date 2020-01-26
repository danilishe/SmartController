package ru.isled.smartcontrol.view;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.effect.RgbMode;

import java.io.File;
import java.util.Optional;

import static javafx.scene.control.ButtonType.*;
import static ru.isled.smartcontrol.Constants.*;

public class Dialogs {

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

//    public static void preview(Project project) {
//        Dialog<Void> preview = new Dialog<>();
//        preview.setTitle("Предпросмотр");
//        final int maxChannelsCount = Math.min(project.getChannelsCount(), MAX_CHANNELS_COUNT);
//        int lastChannel = 0;
//        Pixel pixel = null;
//        // this list for next color changing in preview
//        List<VBox> leds = new ArrayList<>(project.getFramesCount());
//        buildPixels:
//        for (int i = 0; i < project.getPixelsCount(); i++) {
//            pixel = project.getPixel(i);
//            leds.add(new VBox(0));
//            // inserting quantified pixels into 'stack' until it less than maxChannelsCount
//            for (int j = 0; j < pixel.getQuantifier(); j++) {
//                if (pixel.isRgb()) lastChannel += 3;
//                else lastChannel++;
//                // last redundant pixel will be skipped
//                if (lastChannel > maxChannelsCount) {
//                    break buildPixels;
//                }
//                // adding different quantified pixel depending on RGB/mono
//                leds.get(i).getChildren().add(
//                        pixel.isRgb() ?
//                                getColorLabels(pixel.getRgbMode(), lastChannel - 2) :
//                                getMonoLabels(lastChannel)
//                );
//            }
//        }
//
//        HBox hbox = new HBox(5);
//        hbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");
//        hbox.getChildren().addAll(leds);
//
//        List<List<Color[]>> data = new ArrayList<>();
//        for (int i = 0; i < project.getFramesCount(); i++) {
//            List<Color[]> interpolatedFrame = project.getInterpolatedFrame(i, leds.size());
//            for (int j = 0; j < project.getFrame(i).getCycles(); j++) {
//                data.add(interpolatedFrame);
//            }
//        }
//
//        Task<Void> timer = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                Color color;
//                while (preview.isShowing()) {
//                    long stepStartTime = new Date().getTime();
//                    for (List<Color[]> interpolatedFrame : data)
//                        for (int subFrameNo = 0; subFrameNo < interpolatedFrame.get(0).length; subFrameNo++) {
//                            for (int pixel = 0; pixel < project.getFramesCount(); pixel++) {
//                                for (int j = 0; j < leds.size(); j++) {
//                                    color = interpolatedFrame.get(pixel)[subFrameNo];
//                                    leds.get(j).setBackground(BgCache.INSTANCE.get(color));
//                                }
//                                Thread.sleep(BASE_FRAME_LENGTH);
//                                long stepEndTime = new Date().getTime() - stepStartTime;
//                                updateMessage(LocalTime.ofNanoOfDay(stepEndTime * 1_000_000).format(DateTimeFormatter.ISO_TIME));
//                            }
//                        }
//                }
//                return null;
//            }
//        };
//
//        Label label = new Label();
//        label.textProperty().bind(timer.messageProperty());
//        //        timer.setOnSucceeded((s) -> label.textProperty().unbind());
//        VBox vBox = new VBox(5, label, hbox);
//        preview.getDialogPane().setContent(vBox);
//        preview.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
//
//        preview.initOwner(stage);
//        preview.show();
//        Thread thread = new Thread(timer);
//        thread.setDaemon(true);
//        thread.start();
//    }

    private static Node getMonoLabels(int channelNo) {
        Label label = new Label(String.valueOf(channelNo));
        label.setMinWidth(CHANNEL_PREVIEW_SIZE);
        label.setMinHeight(CHANNEL_PREVIEW_SIZE);
        return new HBox(label);
    }

    private static Node getColorLabels(RgbMode rgbMode, int channel) {
        Node[] labels = new Label[3];
        for (int j = 0; j < 3; j++) {
            Label label = new Label(String.valueOf(channel + j));
            label.setTextFill(rgbMode.getColors()[j]);
            label.setMinWidth(CHANNEL_PREVIEW_SIZE);
            label.setMinHeight(CHANNEL_PREVIEW_SIZE);
            labels[channel + j] = label;
        }
        return new HBox(labels);
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
