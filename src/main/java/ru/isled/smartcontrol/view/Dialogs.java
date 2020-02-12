package ru.isled.smartcontrol.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.util.BgCache;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static void preview(Project project) {
        Dialog<Void> preview = new Dialog<>();
        preview.setTitle("Предпросмотр");
        int lastChannel = 0, firstChannel;
        Pixel pixel;
        // this list for color changing in preview
        List<ObjectProperty<Background>> backgrounds = new ArrayList<>(project.getPixelsCount());

        FlowPane mainBackGround = new FlowPane(15, 15);
        mainBackGround.setStyle("-fx-background-color: black; -fx-padding: 20px;");

        buildPixels:
        for (int i = 0; i < project.getPixelsCount(); i++) {
            pixel = project.getPixel(i);
            VBox multiPixel = new VBox(3);
            multiPixel.setAlignment(Pos.TOP_CENTER);

            Text pixelNo = new Text(String.valueOf(i + 1));
            pixelNo.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 17));
            pixelNo.setFill(Color.WHITE);
            pixelNo.setBlendMode(BlendMode.DIFFERENCE);

            ObjectProperty<Background> bg = new SimpleObjectProperty<>(BgCache.INSTANCE.get(Color.BLACK));
            backgrounds.add(bg);

            // inserting quantified pixels into 'stack' until it less than maxChannelsCount
            for (int j = 0; j < pixel.getQuantifier(); j++) {
                firstChannel = lastChannel;
                lastChannel += pixel.getRgbMode().channels();
                // last redundant pixel will be skipped
                if (lastChannel > MAX_CHANNELS_COUNT) {
                    mainBackGround.getChildren().add(multiPixel);
                    break buildPixels;
                }
                VBox pixelPreview = new VBox();
                pixelPreview.setMinSize(MIN_COL_WIDTH, MIN_COL_WIDTH);
                pixelPreview.backgroundProperty().bind(bg);
                pixelPreview.setAlignment(Pos.CENTER);

                if (j == 0) pixelPreview.getChildren().add(pixelNo);

                Text channelNo = new Text((firstChannel + 1) + (lastChannel - firstChannel > 1 ? "-" + lastChannel : ""));
                channelNo.setFill(Color.WHITE);
                channelNo.setBlendMode(BlendMode.DIFFERENCE);
                pixelPreview.getChildren().add(channelNo);

                multiPixel.getChildren().add(pixelPreview);
            }
            mainBackGround.getChildren().add(multiPixel);
        }


        List<List<Color[]>> data = project.getInterpolated();

        final Task<Void> timer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Color color;
                while (preview.isShowing()) {
                    long stepStartTime = new Date().getTime();
                    for (int frameNo = 0; frameNo < data.size(); frameNo++) {
                        for (int subframeNo = 0; subframeNo < data.get(frameNo).get(0).length; subframeNo++) {
                            for (int pixelNo = 0; pixelNo < backgrounds.size(); pixelNo++) {
                                color = data.get(frameNo).get(pixelNo)[subframeNo];
                                // performance fix -- redraw fill only when it changes
                                if (subframeNo == 0 || !color.equals(data.get(frameNo).get(pixelNo)[subframeNo - 1])) {
                                    backgrounds.get(pixelNo).set(BgCache.INSTANCE.get(color));
                                }
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

//        CheckBox showGlow = new CheckBox("сияние");
//        showGlow.setSelected(false);
//        showGlow.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) backgrounds.forEach(l -> l.setEffect(new Glow(5)));
//            else backgrounds.forEach(l -> l.setEffect(null));
//        });


        VBox vBox = new VBox(5, label, mainBackGround/*, showGlow*/);
        preview.getDialogPane().setContent(vBox);
        preview.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        preview.initOwner(stage);
        preview.show();
        Thread thread = new Thread(timer, "Project Previewer");
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
