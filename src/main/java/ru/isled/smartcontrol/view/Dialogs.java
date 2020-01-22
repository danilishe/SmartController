package ru.isled.smartcontrol.view;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.RgbOrder;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public static void preview(Project project) {
        Dialog<Void> preview = new Dialog<>();
        preview.setTitle("Предпросмотр");
        // fixme как считать цветные пиксели?
        final int maxChannelsCount = Math.min(project.getChannelsCount(), MAX_CHANNELS_COUNT);
        int lastChannel = 0;
        Pixel pixel = null;
        // this list for next color changing in preview
        List<VBox> leds = new ArrayList<>(project.getFrameCount());
        buildPixels:
        for (int i = 0; i < project.getPixelCount(); i++) {
            pixel = project.getPixel(i);
            leds.add(new VBox(0));
            // inserting quantified pixels into 'stack' until it less than maxChannelsCount
            for (int j = 0; j < pixel.getQuantifier(); j++) {
                if (pixel.isRgb()) lastChannel += 3;
                else lastChannel++;
                // last redundant pixel will be skipped
                if (lastChannel > maxChannelsCount) {
                    break buildPixels;
                }
                // adding different quantified pixel depending on RGB/mono
                leds.get(i).getChildren().add(
                        pixel.isRgb() ?
                                getColorLabels(pixel.getRgbOrder(), lastChannel - 2) :
                                getMonoLabels(lastChannel)
                );
            }
        }

        HBox hbox = new HBox(5);
        hbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");
        hbox.getChildren().addAll(leds);

        List<List<Color[]>> data = new ArrayList<>();
        for (int i = 0; i < project.getFrameCount(); i++) {
            final int subFramesCount = project.getFrame(i).getFrameLength() / BASE_FRAME_LENGTH;
            List<Color[]> interpolatedFrame = new ArrayList<>();
            for (int k = 0; k < leds.size(); k++) {
                interpolatedFrame.add(project.getPixel(i, k).getInterpolated(subFramesCount));
            }
            for (int j = 0; j < project.getFrame(i).getCycles(); j++) {
                data.add(interpolatedFrame);
            }
        }

        Task<Void> timer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Map<Color, Background> cache = new HashMap<>();
                Background background = null;
                Color color = null;
                while (preview.isShowing()) {
                    long stepStartTime = new Date().getTime();
                    for (List<Color[]> interpolatedFrame : data)
                        for (int subFrameNo = 0; subFrameNo < interpolatedFrame.get(0).length; subFrameNo++) {
                            for (int pixel = 0; pixel < project.getFrameCount(); pixel++) {
                                for (int j = 0; j < leds.size(); j++) {
                                    // think this little cache would be useful
                                    color = interpolatedFrame.get(pixel)[subFrameNo];
                                    if (cache.containsKey(color))
                                        background = cache.get(color);
                                    else {
                                        background = new Background(new BackgroundFill(color, null, null));
                                        cache.put(color, background);
                                    }
                                    leds.get(j).setBackground(background);
                                }
                                Thread.sleep(BASE_FRAME_LENGTH);
                                long stepEndTime = new Date().getTime() - stepStartTime;
                                updateMessage(LocalTime.ofNanoOfDay(stepEndTime * 1_000_000).format(DateTimeFormatter.ISO_TIME));
                            }
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

    private static Node getMonoLabels(int channelNo) {
        Label label = new Label(String.valueOf(channelNo));
        label.setMinWidth(CHANNEL_PREVIEW_SIZE);
        label.setMinHeight(CHANNEL_PREVIEW_SIZE);
        return new HBox(label);
    }

    private static Node getColorLabels(RgbOrder rgbOrder, int i) {
        Node[] result = new Label[3];
        for (int j = 0; j < 3; j++) {
            Label label = new Label(String.valueOf(i + j));
            label.setTextFill(rgbOrder.getColors()[j]);
            label.setMinWidth(CHANNEL_PREVIEW_SIZE);
            label.setMinHeight(CHANNEL_PREVIEW_SIZE);
            result[i] = label;
        }
        return new HBox(result);
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
