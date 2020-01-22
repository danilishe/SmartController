package ru.isled.smartcontrol.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.SmartControl;
import ru.isled.smartcontrol.model.*;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.*;

public class MainController {
    private static final Logger log = LogManager.getLogger();

    @FXML
    public HBox previewZone;
    @FXML
    public Button maxBright;
    @FXML
    public Spinner<Integer> framesSpinner;
    @FXML
    public Spinner<Integer> pixelSpinner;
    @FXML
    public TableView<LedFrame> frameTableView;
    @FXML
    public TableColumn<LedFrame, Integer> frameNumColumn;
    @FXML
    public TableColumn<LedFrame, Integer> frameRepeatColumn;
    @FXML
    public TableColumn<LedFrame, Integer> frameLengthColumn;
    @FXML
    public ChoiceBox<String> effectsSelector;
    @FXML
    public Slider brightSlider;
    @FXML
    public TextField brightField;
    @FXML
    public ToggleButton showDigits;
    @FXML
    public ToggleButton showBright;
    @FXML
    public Spinner<Integer> frameLengthSpinner;
    @FXML
    public Spinner<Integer> frameCyclesSpinner;
    @FXML
    public Slider zoomSlider;
    @FXML
    public Menu lastFiles;
//    private ObservableList<LedFrame> frames = FXCollections.observableArrayList();
    private Project project;
    private List<Shape> previewPixels = new ArrayList<>(MAX_CHANNELS_COUNT);
    private SmartControl mainApp;
    @FXML
    private Label fullTime;
    @FXML
    private Spinner<Integer> chanelQuantifier;

    @FXML
    public void setFadeInEffect() {
        setBrightSelectedCells(PixelEffect.FadeIn.index());
    }

    @FXML
    public void setBlinkEffect() {
        setBrightSelectedCells(PixelEffect.Blinking.index());
    }

    @FXML
    public void setFadeOutEffect() {
        setBrightSelectedCells(PixelEffect.FadeOut.index());
    }

    @FXML
    public void setFadeInOutEffect() {
        setBrightSelectedCells(PixelEffect.FadeInOut.index());
    }

    @FXML
    public void setFadeOutInEffect() {
        setBrightSelectedCells(PixelEffect.FadeOutIn.index());
    }

    @FXML
    public void setBlinkingFadeInEffect() {
        setBrightSelectedCells(PixelEffect.BlinkingFadeIn.index());
    }

    @FXML
    public void setChaosEffect() {
        setBrightSelectedCells(PixelEffect.Chaos.index());
    }

    @FXML
    public void setBlinkingFadeOutEffect() {
        setBrightSelectedCells(PixelEffect.BlinkingFadeOut.index());
    }

    @FXML
    private void refresh() {
        // todo придумать более памятеефективный способ обновлнеия внешнего вида ячеек
        frameTableView.refresh();
    }

    @FXML
    public void newFile() {
        mainApp.createNewProject();
    }

    @FXML
    public void saveFile() {
//        project.setFrameCount(framesSpinner.getValue());
//        project.setChanelCount(pixelSpinner.getValue());
        mainApp.saveProject();
    }

    @FXML
    public void loadFile() {
        mainApp.loadProject(Dialogs.loadFile());
    }

    @FXML
    public void saveFileAs() {
//        project.setFrameCount(framesSpinner.getValue());
//        project.setChanelCount(pixelSpinner.getValue());
        File saveAs = Dialogs.saveAs(project.getFile());
        if (saveAs == null) return;
        project.setFileName(saveAs);
        mainApp.saveProject();
    }

    private void initZoomSlider() {
        zoomSlider.setMin(MIN_COL_WIDTH);
        zoomSlider.setMax(MAX_COL_WIDTH);
        zoomSlider.setValue(INIT_COL_WIDTH);
        zoomSlider.setBlockIncrement((MAX_BRIGHT - MIN_BRIGHT) / 10d);
        zoomSlider.valueProperty().addListener((o, ov, nv) -> setColumnsWidth(nv.doubleValue()));
    }

    private void initializePreviewZone() {
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            Shape pixel = new Rectangle(20, 20, Color.rgb(0xFF, 0xFF, 0, 0));
            Text pixelText = new Text("" + (i + 1));
            StackPane stack = new StackPane(pixel, pixelText);
            pixel.setStroke(Color.BLACK);
            pixel.setStrokeWidth(0.7);

            if (i >= DEFAULT_PIXEL_COUNT) stack.setVisible(false);
            previewPixels.add(pixel);
            previewZone.getChildren().add(stack);
        }
    }

    private void initializeBrightHandlers() {
        brightSlider.valueProperty().addListener((ov, o, n) -> {
            brightField.setText(String.valueOf(n.intValue()));
        });
        brightField.textProperty().addListener((ol, oldValue, newValue) -> {
            if ("".equals(newValue)) return;
            try {
                int val = Integer.parseInt(newValue);

                if (val < MIN_BRIGHT) {
                    val = MIN_BRIGHT;
                } else if (val > MAX_BRIGHT) {
                    val = MAX_BRIGHT;
                }
                brightField.textProperty().setValue(String.valueOf(val));

                brightSlider.setValue(val);
                setBrightSelectedCells(val);

            } catch (NumberFormatException nfe) {
//                System.err.println(nfe.getCause() + " newVal=" + newValue + " oldValue=" + oldValue);
//                brightField.textProperty().setValue(oldValue);
            }
        });
    }

    @FXML
    public void exportHandler() {
        mainApp.exportProject();
    }

    private void setBrightSelectedCells(int bright) {
        for (TablePosition<LedFrame, FramePixel> cell : getSelectedDataCells()) {
            int pixelNo = cell.getColumn() - HEADER_COLUMNS;
            int frameNo = cell.getRow();
            if (!project.getPixels().get(pixelNo).isRgb() && project.getFrames().get(frameNo).getPixel(pixelNo).getStartColor().getB) {
                project.getFrames().get(frameNo).getPixel(pixelNo).setBright(bright);
                project.setHasChanges(true);
            }
        }
        previewFrame(getSelectedFrame());
    }

    private void setLengthSelectedFrames(int length) {
        for (LedFrame frame : frameTableView.getSelectionModel().getSelectedItems()) {
//            System.out.println("was=" + frame.getFrameLength() + " become=" + length);
            frames.get(frames.indexOf(frame)).setLength(length);
            project.setHasChanges(true);

//            frame.setLength(length);
        }
        updateProgramLength();
    }

    private void updateProgramLength() {
        long time = frameTableView.getItems().stream()
                .mapToLong(item -> item.getCycles() * item.getFrameLength())
                .sum();

        String aTime = mSecToProgramLength(time);
        fullTime.setText(aTime);
    }

    private String mSecToProgramLength(long time) {

        try {
            LocalTime dateTime = LocalTime.ofNanoOfDay(time * 1_000_000);
            return dateTime.format(DateTimeFormatter.ISO_TIME);
        } catch (DateTimeException dte) {
            return "> 24 ч";
        }

    }

    private void setCyclesSelectedFrames(int cycles) {
        for (LedFrame frame : frameTableView.getSelectionModel().getSelectedItems()) {
            frame.setLength(cycles);
        }
        updateProgramLength();
    }

    private void handleCellSelection() {

        frameTableView.requestFocus();

        selectAllRowsWhereHeaderIsSelected();

        List<TablePosition<LedFrame, FramePixel>> selectedDataCells = getSelectedDataCells();

        if (!selectedDataCells.isEmpty()) {

            TablePosition<LedFrame, FramePixel> position = selectedDataCells.get(0);

            final int frameNo = position.getRow();
            final int pixelNo = position.getColumn() - HEADER_COLUMNS;
            LedFrame frame = project.getFrame(frameNo);
            FramePixel pixel = frame.getPixel(pixelNo);

            previewFrame(frame);

            if (selectedDataCells.size() == 1) {


                if (cellValue <= MAX_BRIGHT) {
                    brightField.textProperty().setValue("" + cellValue);
                } else {
                    brightField.textProperty().setValue(PixelEffect.byIndex(cellValue).name());
                }
            }
        }
    }

    private void setFrameHandlersDisabled(boolean b) {
        brightField.setDisable(b);
        brightSlider.setDisable(b);
        frameLengthSpinner.setDisable(b);
        frameCyclesSpinner.setDisable(b);
    }

    private void selectAllRowsWhereHeaderIsSelected() {
        List<TablePosition> selectedHeaderCells = getSelectedHeaderCells();
        if (!selectedHeaderCells.isEmpty()) {
            int firstRow = selectedHeaderCells.get(0).getRow();
            int lastRow = selectedHeaderCells.get(selectedHeaderCells.size() - 1).getRow();

            clearSelection();
            frameTableView.getSelectionModel().selectRange(firstRow,
                    frameTableView.getColumns().get(HEADER_COLUMNS),
                    lastRow,
                    frameTableView.getColumns().get(pixelSpinner.getValue() + HEADER_COLUMNS - 1));
        }
    }

    private List<TablePosition> getSelectedHeaderCells() {
        return frameTableView.getSelectionModel().getSelectedCells()
                .stream().filter(x -> x.getColumn() < HEADER_COLUMNS)
                .collect(Collectors.toList());
    }

    private LedFrame getSelectedFrame() {
        try {
            final int frameNo = frameTableView.getSelectionModel().getSelectedCells().get(0).getRow();
            return project.getFrame(frameNo);
        } catch (IndexOutOfBoundsException out) {
            return project.getFrame(0);
        }
    }

    private void previewFrame(LedFrame frame) {
        frameLengthSpinner.getValueFactory().setValue(frame.getFrameLength());
        frameCyclesSpinner.getValueFactory().setValue(frame.getCycles());

        // защита от попытки перерисовки при отсутствии выделенных ячеек
        if (row < 0) return;

        LedFrame frame = frames.get(row);
        for (int i = 0; i < pixelSpinner.getValue(); i++) {

            Shape pixel = previewPixels.get(i);

            pixel.getStyleClass().clear();

            if (frame.getPixel(i) <= MAX_BRIGHT) {
                pixel.getStyleClass().clear();
                pixel.scaleYProperty().set(1 + .1 * project.getPixels().get(i));
                pixel.fillProperty().setValue(
                        Color.rgb(0xFF, 0xFF, 0, ((double) frame.getPixel(i) / MAX_BRIGHT)));
            } else {
//                pixel.fillProperty().setValue(
//                        null
//                );

                if (pixel.getStyleClass().isEmpty())
                    pixel.getStyleClass().add(PixelEffect.cssByIndex(frame.getPixel(i)));
                else
                    pixel.getStyleClass().set(0, PixelEffect.cssByIndex(frame.getPixel(i)));

            }
        }
    }

    /**
     * @return возвращает все выбранные ячейки списком кроме заголовочных
     */
    private List<TablePosition<LedFrame, FramePixel>> getSelectedDataCells() {
        return frameTableView.getSelectionModel()
                .getSelectedCells()
                .stream()
                .filter(x -> x.getColumn() >= HEADER_COLUMNS)
                .collect(Collectors.<TablePosition<LedFrame, FramePixel>>toList());
    }

    @FXML
    public void startPreview() {
        Dialogs.preview(project);
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

        clearSelection();

        frameTableView.getSelectionModel().selectRange(0, colsList.get(HEADER_COLUMNS),
                rows - 1, colsList.get(lastVisibleColNumber + HEADER_COLUMNS - 1));
    }

    @FXML
    public void setBrightBySlider() {
        brightField.textProperty().setValue(String.valueOf((int) brightSlider.getValue()));
    }

    @FXML
    public void setMaxBright() {
//        brightField.textProperty().setValue(String.valueOf(MAX_BRIGHT));
        setBrightSelectedCells(MAX_BRIGHT);
    }

    @FXML
    public void set25Bright() {
        setBrightSelectedCells((int) (MAX_BRIGHT * 0.25));
    }

    @FXML
    public void set75Bright() {
        setBrightSelectedCells((int) (MAX_BRIGHT * 0.75));
    }

    @FXML
    public void set50Bright() {
        setBrightSelectedCells((int) (MAX_BRIGHT * 0.5));
    }

    @FXML
    public void setMinBright() {
//        brightField.textProperty().setValue(String.valueOf(MIN_BRIGHT));
        setBrightSelectedCells(MIN_BRIGHT);
    }

    @FXML
    public void clearSelection() {
        frameTableView.getSelectionModel().clearSelection();
    }

    private void initSpinners() {

        initFrameSpinner();

        initPixelSpinner();

        initLengthSpinner();

        initCyclesSpinner();

        initPixelQuantifierSpinner();

    }

    private void initPixelQuantifierSpinner() {
        chanelQuantifier.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        chanelQuantifier.getValueFactory().valueProperty().addListener((val, ov, nv) -> {
            changePixelQuantities();
            previewFrame(getSelectedFrame());
        });
    }

    private void initCyclesSpinner() {
        frameCyclesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_CYCLES, MAX_CYCLES, 1));
        frameCyclesSpinner.getEditor().textProperty().addListener((ov, oldV, newV) -> {
            if (!newV.matches("\\d+"))
                frameCyclesSpinner.getEditor().setText(oldV);
        });
        frameCyclesSpinner.getValueFactory().valueProperty().addListener((ov, oldV, newV) -> {
            if (newV > MAX_CYCLES)
                frameCyclesSpinner.getEditor().setText("" + MAX_CYCLES);
            else if (newV < MIN_CYCLES)
                frameCyclesSpinner.getEditor().setText("" + MIN_CYCLES);

            if (newV >= MIN_CYCLES && newV <= MAX_CYCLES) {
                setCyclesSelectedFrames();
            }
        });
    }

    private void setCyclesSelectedFrames() {
        int cycles = frameCyclesSpinner.getValue();
        frameTableView.getSelectionModel().getSelectedItems().forEach(frame -> frame.setCycles(cycles));
        updateProgramLength();
    }

    private void initLengthSpinner() {
        frameLengthSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAME_LENGTH, MAX_FRAME_LENGTH, DEFAULT_FRAME_LENGTH, FRAME_LENGTH_STEP)
        );

        // запрет ввода НЕ цифр
        frameLengthSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d+"))
                frameLengthSpinner.getEditor().setText(oldValue);
        });

        // отслеживает корректность значения спиннера (не текстового поля, входящего в его состав)
        frameLengthSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue % FRAME_LENGTH_STEP != 0)
                frameLengthSpinner.getValueFactory().setValue((newValue / FRAME_LENGTH_STEP * FRAME_LENGTH_STEP));

            if (newValue > MAX_FRAME_LENGTH)
                frameLengthSpinner.getValueFactory().setValue(MAX_FRAME_LENGTH);
            else if (newValue < MIN_FRAME_LENGTH)
                frameLengthSpinner.getValueFactory().setValue(MIN_FRAME_LENGTH);

            if (newValue % MIN_FRAME_LENGTH == 0 && newValue <= MAX_FRAME_LENGTH && newValue >= MIN_FRAME_LENGTH) {
                setLengthSelectedFrames(newValue);
            } else {
//                frameLengthSpinner.getValueFactory().getValue());
            }

        });

        frameLengthSpinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                frameTableView.getColumns().get(HEADER_COLUMNS - 1).setVisible(false);
                frameLengthSpinner.getEditor().commitValue();
                frameTableView.getColumns().get(HEADER_COLUMNS - 1).setVisible(true);
                event.consume();
            }
        });
        // применяет значение после потери фокуса
        frameLengthSpinner.getEditor().focusedProperty().addListener((ov, o, n) -> {
            if (n == false) {
                int val = Integer.parseInt(frameLengthSpinner.getEditor().getText());
                if (val % FRAME_LENGTH_STEP != 0)
                    val = val / FRAME_LENGTH_STEP * FRAME_LENGTH_STEP;

                if (val > MAX_FRAME_LENGTH)
                    val = MAX_FRAME_LENGTH;
                else if (val < MIN_FRAME_LENGTH)
                    val = MIN_FRAME_LENGTH;

                // чётко обновляет внешний вид таблицы, так как после скрытия столбца, его значение меняется
                if (frameLengthSpinner.getValue() != val) {
                    frameTableView.getColumns().get(HEADER_COLUMNS - 1).setVisible(false);
                    frameLengthSpinner.getValueFactory().setValue(val);
                    frameTableView.getColumns().get(HEADER_COLUMNS - 1).setVisible(true);
                }
            }
        });

    }

    private void initPixelSpinner() {
        pixelSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);

        pixelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_PIXELS_COUNT, MAX_CHANNELS_COUNT, DEFAULT_PIXEL_COUNT)
        );

        pixelSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d+")) {
                pixelSpinner.getEditor().textProperty().setValue(oldValue);
            }
        });

        pixelSpinner.getValueFactory().valueProperty().addListener((ov, o, n) -> {
            if (n < MIN_PIXELS_COUNT)
                pixelSpinner.getValueFactory().setValue(MIN_PIXELS_COUNT);
            else if (n > MAX_CHANNELS_COUNT)
                pixelSpinner.getValueFactory().setValue(MAX_CHANNELS_COUNT);
            else {
                project.setPixelCount(n);
                refreshVisibleColumnsCount();
                updateTotalPixelCount();
            }
        });

        pixelSpinner.getEditor().focusedProperty().addListener((ov, o, n) -> {
            if (!n) {
                pixelSpinner.getValueFactory().setValue(
                        Integer.parseInt(
                                pixelSpinner.getEditor().textProperty().get()
                        )
                );
            }
        });
    }

    @FXML
    private Slider gammaSlider;
    @FXML
    private Label gammaLabel;

    private void initGammaHandlers() {
        gammaSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            final String value = String.format(Locale.US, "%.1f", (double) newValue);
            gammaLabel.textProperty().set(value);
            getProject().setGamma(Double.parseDouble(value));
        });
    }

    private void initFrameSpinner() {
        framesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAMES, MAX_FRAMES, DEFAULT_FRAMES_COUNT)
        );

        framesSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d+")) {
                framesSpinner.getEditor().textProperty().setValue(oldValue);
            }
        });

        framesSpinner.getValueFactory().valueProperty().addListener((ov, o, n) -> {
            if (n < MIN_FRAMES)
                framesSpinner.getValueFactory().setValue(MIN_FRAMES);
            else if (n > MAX_FRAMES)
                framesSpinner.getValueFactory().setValue(MAX_FRAMES);
            else {
                project.setFrameCount(n);
                updateFramesCount();
            }
        });

        framesSpinner.getEditor().focusedProperty().addListener((ov, o, n) -> {
            if (!n) {
                framesSpinner.getValueFactory().setValue(
                        Integer.parseInt(
                                framesSpinner.getEditor().textProperty().get()
                        )
                );
            }
        });

    }

    private void updateFramesCount() {
        int existsFrames = frameTableView.getItems().size();
        int needFrames = framesSpinner.getValue();
        if (existsFrames > needFrames) {
            for (int i = existsFrames; i > needFrames; i--) {
                frames.remove(frames.size() - 1);
            }
            updateProgramLength();
        } else if (existsFrames < needFrames) {
            for (int i = existsFrames; i < needFrames; i++) {
                if (project.size() == i) project.addFrame(new LedFrame());
                frames.add(project.getFrame(i));
            }
            updateProgramLength();
        }
    }

    // скрывает колонки
    private void refreshVisibleColumnsCount() {
        int selectedPixelNumber = pixelSpinner.getValue();
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {

            frameTableView.getColumns().get(i + HEADER_COLUMNS).visibleProperty().setValue(
                    i < selectedPixelNumber);

            previewPixels.get(i).getParent().setVisible(
                    i < selectedPixelNumber);
        }
    }

    private void initializeRowHeader() {

        frameNumColumn.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(frames.indexOf(cellData.getValue()) + 1));

        frameRepeatColumn.setCellValueFactory(x -> x.getValue().getCycles());

        frameLengthColumn.setCellFactory(column -> new LedFrameLengthCell());
        frameLengthColumn.setCellValueFactory(x -> x.getValue().getFrameLength());
    }

    private void initDataColumns() {
        disableColumnReordering();

        // пиксели/каналы
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            final int absNum = i + HEADER_COLUMNS;
            TableColumn<LedFrame, Integer> column = new TableColumn<>(String.valueOf(i + 1) + "\n[1]");

//            column.addEventHandler(MouseEvent.ANY,
//                    event -> {
//                        frameTableView.getSelectionModel()
//                                .selectRange(0, frameTableView.getColumns().get(absNum),
//                                        framesSpinner.getValue(), frameTableView.getColumns().get(absNum + 1));
//                        event.consume();
//                    });
            setDefaultColumnProperties(column);

            if (i >= DEFAULT_PIXEL_COUNT) column.visibleProperty().setValue(false);
            final int in = i;

            column.setCellFactory(cell -> new LedFrameTableCell(showDigits.selectedProperty(), showBright.selectedProperty()));

            column.setCellValueFactory(cellData -> cellData.getValue().get(in));

            frameTableView.getColumns().add(column);
        }
    }

    private void changePixelQuantities() {
        final int value = chanelQuantifier.getValue();
        frameTableView.getSelectionModel().getSelectedCells().stream().mapToInt(TablePosition::getColumn)
                .distinct()
                .forEach(col -> {
                    project.getPixels().set(col - HEADER_COLUMNS, value);
                    TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(col);
                    String text = column.getText();
                    column.setText(text.replaceAll("\\[\\d+]", "[" + value + "]"));
                });
        project.setHasChanges(true);
        updateTotalPixelCount();
    }

    @FXML
    private Label bulbIcon;
    @FXML
    private Tooltip errorTooltip;
    @FXML
    private Label totalPixels;

    public void updateHeaderQuantifiers() {
        List<Integer> quantifiers = project.getPixels();
        for (int i = 0; i < quantifiers.size(); i++) {
            TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(i + HEADER_COLUMNS - 1);
            column.setText(column.getText().replaceAll("\\[\\d+]", "[" + quantifiers.get(i) + "]"));
        }
    }

    private void setDefaultColumnProperties(TableColumn<LedFrame, Integer> column) {
        column.setPrefWidth(INIT_COL_WIDTH);
        column.setResizable(false);
        column.setSortable(false);
        column.setEditable(false);
        column.setStyle(DEFAULT_CELL_STYLE);
    }

    private void loadAndSetDefaultEffects() {
        for (Effect effect : Effect.values()) {
            effectsSelector.getItems().add(effect.toString());
        }
        effectsSelector.setValue(effectsSelector.getItems().get(0));
    }

    private void disableColumnReordering() {
        frameTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) frameTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });
    }

    @FXML
    public void exitHandler() {
        mainApp.exit();
    }

    @FXML
    public void showAboutInfoHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(mainApp.getMainStage());

        alert.setTitle("О программе");
        alert.setHeaderText("SMART Control " + PROGRAM_VERSION);
        final Label label = new Label("Программа для создания и редактирования эффектов для контроллера ISLed" +
                "\nЗнакСвет © 2018-2020" +
                "\nСуетин Д.Е. © 2018-2020" +
                "\nВерсия " + PROGRAM_VERSION +
                "\nПамяти JVM свободно/всего: " + Runtime.getRuntime().freeMemory() / 1_000_000 + "МБ / " + Runtime.getRuntime().totalMemory() / 1_000_000 + "МБ"
        );
        final Hyperlink isLedLink = new Hyperlink(CONTROLLER_PAGE_URL);
        isLedLink.setOnAction(event -> mainApp.getHostServices().showDocument(CONTROLLER_PAGE_URL));

        alert.getDialogPane().setContent(new VBox(label, isLedLink));
        alert.showAndWait();
    }

    @FXML
    public void applyEffectHandler() {
        List<TablePosition> cells = getSelectedDataCells();
        new ColorPicker(Color.WHITE).getValue();

        if (cells != null && cells.size() > 0) {
            project.setHasChanges(true);

            //fixme ПЕРЕДЕЛАТЬ МЕТОД, ЧТОБЫ ВОЗВРАЩАЛ ДВУМЕРНЫЙ МАССИВ
            //todo СДЕЛАТЬ НАСТРОЙКУ В ЭФФЕКТАХ, ЧТОБЫ МОЖНО БЫЛО ДОБАВИТЬ КАДРЫ ДО ОКОНЧАНИЯ ЭФФЕКТА

            TablePosition lastCell = cells.get(cells.size() - 1);
            TablePosition firstCell = cells.get(0);
            int cols = lastCell.getColumn() - firstCell.getColumn() + 1;
            int rows = lastCell.getRow() - firstCell.getRow() + 1;


            List<IntegerProperty> values = getValuesList(cells);


            String selectedEffect = effectsSelector.getValue();
            Effect.valueOf(selectedEffect).apply(values, cols, rows);

        }
    }

    private List<IntegerProperty> getValuesList(List<TablePosition> cells) {
        return cells.stream()
                .map(c -> frames.get(
                        c.getRow()).getProperty(
                        c.getColumn() - HEADER_COLUMNS))
                .collect(Collectors.toList());
    }

    public void setMainApp(SmartControl main) {
        mainApp = main;
    }

    public void setProject(Project project) {
        this.project = project;
        frames.clear();
        pixelSpinner.getValueFactory().setValue(project.getPixelCount());
        framesSpinner.getValueFactory().setValue(project.getFrameCount());
        updateFramesCount();
        if (frameTableView.getColumns().size() > HEADER_COLUMNS) updateHeaderQuantifiers();
        updateTotalPixelCount();
    }

    public Project getProject() {
        return project;
    }

    private void setColumnsWidth(double columnsWidth) {
        frameTableView.getColumns().stream()
                .skip(HEADER_COLUMNS).forEach(col -> {
            col.setPrefWidth(columnsWidth);
        });
    }

    @FXML
    public void initialize() {
        initializeRowHeader();
        loadAndSetDefaultEffects();

        initZoomSlider();
        initGammaHandlers();

        initSpinners();
        initDataColumns();
        initializePreviewZone();
        initializeBrightHandlers();

        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        frameTableView.addEventHandler(EventType.ROOT, x -> handleCellSelection());
        frameTableView.setItems(frames);
//        frames.addListener((ListChangeListener<LedFrame>) c -> updateProgramLength());
    }

    private void updateTotalPixelCount() {
        int sum = project.getChannelsCount();
        if (sum > MAX_CHANNELS_COUNT) {
            bulbIcon.setTextFill(Color.RED);
            errorTooltip.setText(TOO_MUCH_CHANNELS_ERROR_HINT);
        } else {
            bulbIcon.setTextFill(Color.BLACK);
            errorTooltip.setText(CHANNELS_COUNTER_HINT);
        }
        totalPixels.setText(String.valueOf(sum));
    }

    public void scrollZoom(ScrollEvent scrollEvent) {
        final double currentValue = zoomSlider.getValue();
        zoomSlider.setValue(currentValue + scrollEvent.getTextDeltaY());
    }

    public void scrollBright(ScrollEvent scrollEvent) {
        brightSlider.setValue(brightSlider.getValue() + scrollEvent.getTextDeltaY());
    }

    public void scrollFrameLength(ScrollEvent scrollEvent) {
        final int delta = (int) scrollEvent.getTextDeltaY();
        frameLengthSpinner.increment(delta);
    }

    public void scrollFrames(ScrollEvent scrollEvent) {
        final int delta = (int) scrollEvent.getTextDeltaY();
        framesSpinner.increment(delta);
    }

    public void scrollPixels(ScrollEvent scrollEvent) {
        final int delta = (int) scrollEvent.getTextDeltaY();
        pixelSpinner.increment(delta);
    }

    public void scrollRepeats(ScrollEvent scrollEvent) {
        final int delta = (int) scrollEvent.getTextDeltaY();
        frameCyclesSpinner.increment(delta);
    }

    public void scrollQuantifier(ScrollEvent scrollEvent) {
        final int delta = (int) scrollEvent.getTextDeltaY();
        chanelQuantifier.increment(delta);
    }

    public void scrollGamma(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() > 0) {
            gammaSlider.increment();
        } else {
            gammaSlider.decrement();
        }
    }
}
