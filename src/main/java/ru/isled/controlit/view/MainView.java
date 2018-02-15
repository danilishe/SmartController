package ru.isled.controlit.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import ru.isled.controlit.Constants;
import ru.isled.controlit.Controlit;
import ru.isled.controlit.controller.FileHelper;
import ru.isled.controlit.model.Effect;
import ru.isled.controlit.model.LedFrame;
import ru.isled.controlit.model.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainView implements Constants {

    public ObservableList<LedFrame> frames;
    public List<LedFrame> framesBackup = new ArrayList<>(MAX_PIXELS);
    private Project project;

    @FXML
    public HBox previewBox;
    //    @FXML
//    public Spinner<Integer> frameLength;
    @FXML
    public Button maxBright;
    @FXML
    public Spinner<Integer> framesSpinner;
    @FXML
    public Spinner<Integer> pixelSpinner;
    @FXML
    public TableView<LedFrame> frameTableView;
    @FXML
    public TableColumn<LedFrame, String> frameNumColumn;
    @FXML
    public ChoiceBox<String> effectsSelector;
    @FXML
    public Slider brightSlider;
    @FXML
    public TextField brightField;
    @FXML
    public CheckBox showDigits;
    @FXML
    public CheckBox showBright;

    public File file = null;

    public List<Shape> previewPixels = new ArrayList<>(MAX_PIXELS);
    private Controlit mainApp;
//    @FXML
//    private Label framerateLabel;

    @FXML
    public void newFile() {
        if (project.hasUnsavedChanges())
            if (continueAfterAskSaveFile()) {
                mainApp.createNewProject();
            }
    }

    private boolean continueAfterAskSaveFile() {

        switch (Dialogs.askSaveProject(mainApp.getMainStage())) {
            case YES:
                mainApp.saveProject();
            case NO:
                return true;

            case CANCEL_CLOSE:
            default:
                return false;
        }

    }

    @FXML
    public void saveFile() {

        if (!project.hasName()) {
            file = getFileNameForSave();
            if (file == null) return;

            project.setName(file.getAbsolutePath());
        }

        mainApp.saveProject();

    }

    public void updateHeader() {
        mainApp.updateHeader();
    }

    public void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<Byte> getByteData() {
        List<Byte> data = new ArrayList<>();
        for (int i = 0; i < framesSpinner.getValue(); i++) {
            for (int j = 0; j < MAX_PIXELS; j++) {
                data.add(framesBackup.get(i).getPixel(j).getValue().byteValue());
            }
        }
        return data;
    }


    @FXML
    public void loadFile() {
        File newFileName = getFileNameForLoad();
        if (newFileName != null) {
            try {
                List<Byte> data = FileHelper.load(newFileName);

                if (data.size() % MAX_PIXELS > 0) throw new UnsupportedOperationException("Неверный формат файла");

                int rows = data.size() / MAX_PIXELS + 1;
                int maxPixels = 0;
                for (int i = 0; i < rows - 1; i++) {

                    List<Byte> subList = data.subList(i * MAX_PIXELS, (i + 1) * MAX_PIXELS);

                    if (i >= frames.size())
                        frames.add(new LedFrame());

                    int pix = frames.get(i).set(subList);
                    if (pix > maxPixels) maxPixels = pix;
                }

                framesSpinner.getValueFactory().setValue(rows);
                pixelSpinner.getValueFactory().setValue(maxPixels);

                file = newFileName;
                updateHeader();

                refresh();

            } catch (Exception e) {
                showErrorAlert("Ошибка при открытии файла: " + e.getMessage());
            }

        }
    }

    private File getFileNameForLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть...");

        return fileChooser.showOpenDialog(mainApp.getMainStage());

    }

    private File getFileNameForSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как...");
        fileChooser.setInitialFileName(file == null ? "data" : file.getName());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("ISLed проект для контроллера", "*.isd"));
        return fileChooser.showSaveDialog(mainApp.getMainStage());
    }

    @FXML
    public void saveFileAs() {
        File saveAs = getFileNameForSave();
        if (saveAs == null) return;
        file = saveAs;
        saveFile();
    }

    @FXML
    public void initialize() {
        frames = FXCollections.observableArrayList();

        disableColumnReordering();
        initializeRowHeader();
        loadAndSetDefaultEffects();

        initializeSpinners();
        initializeColumns();
        initializePreviewZone();
        initializeBrightHandlers();

        mainApp.createNewProject();

        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        frameTableView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> handleCellSelection()
        );
        frameTableView.setItems(frames);
    }


    public void initializePreviewZone() {
        for (int i = 0; i < MAX_PIXELS; i++) {
            Shape pixel = new Circle(10, Color.rgb(0xFF, 0xFF, 0, 0));
            pixel.setStroke(Color.BLACK);
            pixel.setStrokeWidth(0.7);

            if (i > INIT_PIXELS - 1) pixel.setVisible(false);
            previewPixels.add(pixel);
            previewBox.getChildren().add(pixel);
        }
    }

    public void initializeBrightHandlers() {

        maxBright.setText(String.valueOf(MAX_BRIGHT));

        brightField.textProperty().addListener((ov, oldValue, newValue) -> {
            try {
                int val = Integer.parseInt(newValue);

                if (val < MIN_BRIGHT) {
                    val = MIN_BRIGHT;
                } else if (val > MAX_BRIGHT) {
                    val = MAX_BRIGHT;
                }

                brightField.textProperty().setValue(String.valueOf(val));

                brightSlider.setValue(val);
//                if (val != Integer.parseInt(oldValue)) {
                updateSelectedCells(val);
//                }

            } catch (NumberFormatException nfe) {
                brightField.textProperty().setValue(oldValue);
            }

            // todo после загрузки и последующего сохранения, сохраняется только 10 шагов
            // после загрузки 1 лишняя строка?
            refresh();
        });
    }

    @FXML
    public void refresh() {
        frameTableView.refresh();
    }

    public void updateSelectedCells(int val) {
        for (TablePosition cell : getSelectedCells()) {
            frames.get(cell.getRow()).getPixel(cell.getColumn() - 1).setValue(val);
        }
        project.setHasChanges(true);
        refresh();
    }

    public void handleCellSelection() {
        // фильтруем ячейки заголовка
        List<TablePosition> rowHeaderCells = frameTableView.getSelectionModel().getSelectedCells()
                .stream().filter(x -> x.getColumn() == 0)
                .collect(Collectors.toList());


        if (!rowHeaderCells.isEmpty()) {
            int firstRow = rowHeaderCells.get(0).getRow();
            int lastRow = rowHeaderCells.get(rowHeaderCells.size() - 1).getRow() + 1;

            frameTableView.getSelectionModel().selectRange(firstRow, lastRow);
        }

        List<TablePosition> cellsWithoutHeaders = getSelectedCells();

        if (!cellsWithoutHeaders.isEmpty()) {
            brightField.disableProperty().setValue(false);

            setPreviewRow(getSelectedRow());
            TablePosition position = cellsWithoutHeaders.get(0);

            IntegerProperty cellValue = frames.get(position.getRow()).getPixel(position.getColumn() - 1);

            if (cellsWithoutHeaders.size() == 1) {
                brightField.textProperty().setValue(cellValue.getValue().toString());
            }

        } else {
            brightField.textProperty().setValue("");
            brightField.disableProperty().setValue(true);
        }
    }

    public int getSelectedRow() {
        return frameTableView.getSelectionModel().getSelectedCells().get(0).getRow();
    }

    public void setPreviewRow(int row) {
        LedFrame frame = frames.get(row);
        for (int i = 0; i < pixelSpinner.getValue(); i++) {
            previewPixels.get(i).fillProperty().setValue(Color.rgb(0xFF, 0xFF, 0, ((double) frame.getPixel(i).getValue() / MAX_BRIGHT)));
        }
    }

    // todo Один из вариантов решения для отсроченного обновления
//    public static Thread timer = new Thread(() -> {
//        public boolean isComplete() {
//
//        }
//    }
//   );

    public List<TablePosition> getSelectedCells() {
        return frameTableView.getSelectionModel().getSelectedCells()
                .stream()
                .filter(x -> x.getColumn() > 0)
                .collect(Collectors.toList());
    }

    @FXML
    public void increment5Frames() {
        framesSpinner.increment(5);
    }

    @FXML
    public void increment15Frames() {
        framesSpinner.increment(15);
    }

    @FXML
    public void decrement5Frames() {
        framesSpinner.decrement(5);
    }

    @FXML
    public void decrement15Frames() {
        framesSpinner.decrement(15);
    }

    @FXML
    public void selectAll() {
        List<TableColumn<LedFrame, ?>> colsList = frameTableView.getColumns();
        int lastVisibleColNumber = pixelSpinner.getValue();
        int rows = frameTableView.getItems().size();
        deselectAll();

        frameTableView.getSelectionModel().selectRange(0, colsList.get(1), rows - 1, colsList.get(lastVisibleColNumber));
//        refresh();
    }

    @FXML
    public void setBrightBySlider() {
        brightField.textProperty().setValue(String.valueOf((int) brightSlider.getValue()));
    }

    @FXML
    public void setMaxBright() {
        brightField.textProperty().setValue(String.valueOf(MAX_BRIGHT));
        updateSelectedCells(MAX_BRIGHT);
    }

    @FXML
    public void setMinBright() {
        brightField.textProperty().setValue(String.valueOf(MIN_BRIGHT));
        updateSelectedCells(MIN_BRIGHT);
    }

    @FXML
    public void setRandomBright() {
        int rnd = Math.round((int) (Math.random() * (MAX_BRIGHT + 1)));
        brightField.textProperty().setValue(String.valueOf(rnd));
    }

    @FXML
    public void deselectAll() {
        frameTableView.getSelectionModel().clearSelection();
    }


    private void initializeSpinners() {

//        frameLength.setValueFactory(
//                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAMERATE, MAX_FRAMERATE, DEFAULT_FRAMERATE)
//        );
//
//        frameLength.getEditor().textProperty().addListener((o, oldValue, newValue) -> {
//            try {
//                Integer rate = Integer.parseInt(newValue);
//                if (rate > MAX_FRAMERATE) {
//                    rate = MAX_FRAMERATE;
//                    frameLength.getEditor().textProperty().setValue(String.valueOf(rate));
//                } else if (rate < MIN_FRAMERATE) {
//                    rate = MIN_FRAMERATE;
//                    frameLength.getEditor().textProperty().setValue(String.valueOf(rate));
//                }
//            } catch (NumberFormatException nfe) {
//                frameLength.getEditor().textProperty().setValue(oldValue);
//            }
//
//        });
//

        framesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAMES, MAX_FRAMES, INIT_FRAMES)
        );

        framesSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            try {
                int val = Integer.parseInt(newValue);

                if (val < MIN_FRAMES) {
                    framesSpinner.getEditor().textProperty().setValue(String.valueOf(MIN_FRAMES));
                } else if (val > MAX_FRAMES)
                    framesSpinner.getEditor().textProperty().setValue(String.valueOf(MAX_FRAMES));

                // хак для вызова обновления спиннера
                framesSpinner.getValueFactory().setValue(
                        Integer.parseInt(framesSpinner.getEditor().textProperty().getValue())
                );


                updateFramesCount();
                refresh();

            } catch (NumberFormatException nfe) {
                framesSpinner.getEditor().textProperty().setValue(oldValue);
            }
        });

//        framesSpinner.getEditor().disableProperty().setValue(true);

        // todo отменить undo/redo полей
        // todo попробовать сделать, чтобы значение поля применялось не сразу, а через время или по откончанию редактирования (смены фокуса, подтверждению и т.п.)

        pixelSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        pixelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_PIXELS, MAX_PIXELS, INIT_PIXELS)
        );
        pixelSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            try {
                int val = Integer.parseInt(newValue);

                if (val < MIN_PIXELS)
                    pixelSpinner.getEditor().textProperty().setValue(String.valueOf(MIN_PIXELS));
                if (val > MAX_PIXELS)
                    pixelSpinner.getEditor().textProperty().setValue(String.valueOf(MAX_PIXELS));

                // хак для вызова обработчика событий спиннера
                pixelSpinner.getValueFactory().setValue(
                        Integer.parseInt(pixelSpinner.getEditor().textProperty().getValue()
                        ));

                refreshPixelCount();
            } catch (NumberFormatException nfe) {
                pixelSpinner.getEditor().textProperty().setValue(oldValue);
            }
        });
    }

    public synchronized void updateFramesCount() {
        int existsFrames = frameTableView.getItems().size();
//        frameTableView.
        int needFrames = framesSpinner.getValue();
        if (existsFrames > needFrames) {
            for (int i = existsFrames; i > needFrames; i--) {
                frames.remove(frames.size() - 1);
            }
        } else if (existsFrames < needFrames) {
            for (int i = existsFrames; i < needFrames; i++) {
                frames.add(framesBackup.get(i));
            }
        }
    }

    public void refreshPixelCount() {
        int selectedPixelNumber = pixelSpinner.getValue();
        for (int i = 1; i <= MAX_PIXELS; i++) {

            frameTableView.getColumns().get(i).visibleProperty().setValue(
                    i <= selectedPixelNumber);

            previewPixels.get(i - 1).setVisible(
                    i <= selectedPixelNumber);

        }
    }

    public void initializeRowHeader() {
        frameNumColumn.setStyle("-fx-alignment: center; " +
                "-fx-background-color: -fx-box-border, -fx-inner-border, -fx-body-color;" +
                "-fx-background-insets: 0, 0 1 1 0, 1 2 2 1;");
        frameNumColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        String.valueOf(
                                frames.indexOf(cellData.getValue()) + 1)
                ));
        frameNumColumn.setEditable(false);
    }

    public void initializeColumns() {

        for (int i = 1; i <= MAX_PIXELS; i++) {
            TableColumn<LedFrame, Integer> column = new TableColumn<>(String.valueOf(i));
            column.setId("px_" + i);
            column.setMaxWidth(50);
            column.setPrefWidth(50);
            column.setMinWidth(50);
            column.setSortable(false);
            column.setEditable(false);
            column.setStyle("-fx-alignment: CENTER;");

            if (i > INIT_PIXELS) column.visibleProperty().setValue(false);
            final int in = i - 1;

            column.setCellFactory(cell -> new TableCell<LedFrame, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);


                    if (item != null && showBright.isSelected()) {
                        double brightness = (double) item / MAX_BRIGHT;
                        Shape fig = new Circle(10, Color.color(1, 1, 0, brightness));
                        fig.setStroke(Color.BLACK);
                        fig.setStrokeWidth(0.5);
                        setGraphic(fig);
//                        this.getBackground()(new Background(new BackgroundFill(Color.color(1, 1, 0,), null, null)));
//                        this.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 0, item != null ? (double) item / MAX_BRIGHT : 0), null, null)));
                    }

                    if (showDigits.isSelected())
                        setText(item != null ? item.toString() : "");
                }
            });
//            column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

            column.setCellValueFactory(cellData -> cellData.getValue().getPixel(in).asObject());


            // не используется!
//            column.setOnEditCommit(cellEditEvent -> {
//                int oldValue = cellEditEvent.getOldValue();
//
//                try {
//                    int value = cellEditEvent.getNewValue();
//
//                    if (value < MIN_BRIGHT) value = MIN_BRIGHT;
//                    else if (value > MAX_BRIGHT) value = MAX_BRIGHT;
//
//                    if (value != oldValue) {
//                        cellEditEvent.getTableView().getItems().get(
//                                cellEditEvent.getTablePosition().getRow()
//                        ).setPixel(in, value);
//                    }
//                } catch (NumberFormatException ignore) {
//                }
//
//                refresh();
//
//            });

            frameTableView.getColumns().add(column);
        }
    }

    public void initializeDefaultData() {
        framesBackup.clear();
        frames.clear();

        for (int i = 0; i < MAX_FRAMES; i++) {
            framesBackup.add(new LedFrame());
        }
        updateFramesCount();
    }

    public void loadAndSetDefaultEffects() {
        for (Effect effect : Effect.values()) {
            effectsSelector.getItems().add(effect.toString());
        }
        effectsSelector.setValue(effectsSelector.getItems().get(0));
    }

    public void disableColumnReordering() {
        frameTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) frameTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });
    }

    @FXML
    public void exitHandler() {
        if (project.hasUnsavedChanges()) {
            if (continueAfterAskSaveFile())
                mainApp.exit();
        }

    }

    @FXML
    public void showAboutInfo() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(mainApp.getMainStage());
        alert.setTitle("О программе");
        alert.setHeaderText(null);
        alert.setContentText("Программа для создания программ для контроллера ISLed. ЗнакСвет (C) 2018");

        alert.showAndWait();
    }

    @FXML
    public void applyEffectHandler() {
        List<TablePosition> cells = getSelectedCells();
        if (cells != null && cells.size() > 0) {
            int cols = cells.get(cells.size() - 1).getColumn() - cells.get(0).getColumn() + 1;
            int rows = cells.get(cells.size() - 1).getRow() - cells.get(0).getRow() + 1;

            List<IntegerProperty> values = new ArrayList<>();
            for (TablePosition cell : cells) {
                values.add(frames.get(cell.getRow()).getPixel(cell.getColumn() - 1));
            }


            String selectedEffect = effectsSelector.getValue();
            if (selectedEffect.equals(Effect.Разгорание.name())) {
                Effect.Разгорание.apply(values, cols, rows, null, null);

            } else if (selectedEffect.equals(Effect.Угасание.name())) {
                Effect.Угасание.apply(values, cols, rows, null, null);

            } else if (selectedEffect.equals(Effect.Случайно.name())) {
                Effect.Случайно.apply(values, null, null, null, null);
            }

        }

    }

    public void setMainApp(Controlit main) {
        mainApp = main;
    }
}
