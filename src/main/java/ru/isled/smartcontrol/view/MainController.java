package ru.isled.smartcontrol.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.SmartControl;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.effect.Effect;
import ru.isled.smartcontrol.model.effect.PixelEffect;
import ru.isled.smartcontrol.view.cell.ColumnHeaderFactory;
import ru.isled.smartcontrol.view.cell.LedFrameLengthCell;
import ru.isled.smartcontrol.view.cell.LedFrameTableCell;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.*;

public class MainController {
    private static final Logger log = LogManager.getLogger();

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
    public Slider zoomSlider;
    @FXML
    public Menu lastFiles;
    @FXML
    public HBox previewZone;
    private FramePreviewController framePreviewController;
    private Project project;
    private SmartControl mainApp;
    @FXML
    private Label fullTime;
    @FXML
    private Spinner<Integer> chanelQuantifier;

    @FXML
    public Spinner<Integer> frameLengthSpinner;
    @FXML
    public Spinner<Integer> frameCyclesSpinner;
    private FrameHandlersController frameHandlersController = new FrameHandlersController(this);
    private int lastFrame = -1;

    @FXML
    public void setFadeInEffect() {
        setEffectSelectedCells(PixelEffect.FadeIn);
    }

    @FXML
    public void setSolidEffect() {
        setEffectSelectedCells(PixelEffect.Solid);
    }

    @FXML
    public void setBlinkEffect() {
        setEffectSelectedCells(PixelEffect.Blinking);
    }

    @FXML
    public void setFadeOutEffect() {
        setEffectSelectedCells(PixelEffect.FadeOut);
    }

    @FXML
    public void setFadeInOutEffect() {
        setEffectSelectedCells(PixelEffect.FadeInOut);
    }

    @FXML
    public void setFadeOutInEffect() {
        setEffectSelectedCells(PixelEffect.FadeOutIn);
    }

    @FXML
    public void setBlinkingFadeInEffect() {
        setEffectSelectedCells(PixelEffect.BlinkingFadeIn);
    }

    @FXML
    public void setChaosEffect() {
        setEffectSelectedCells(PixelEffect.Chaos);
    }

    @FXML
    public void setBlinkingFadeOutEffect() {
        setEffectSelectedCells(PixelEffect.BlinkingFadeOut);
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
        log.trace("updating bright to ");
        for (TablePosition cell : getSelectedDataCells()) {
            int pixelNo = cell.getColumn() - HEADER_COLUMNS;
            int frameNo = cell.getRow();
            project.getPixel(pixelNo).getFrames().get(frameNo).setBright(bright);
            project.setHasChanges(true);
        }
        framePreviewController.previewFrame(getSelectedFrame());
    }

    private void setEffectSelectedCells(PixelEffect effect) {
        log.trace("updating effect to " + effect);
        for (TablePosition cell : getSelectedDataCells()) {
            int pixelNo = cell.getColumn() - HEADER_COLUMNS;
            int frameNo = cell.getRow();
            project.getPixel(pixelNo).getFrames().get(frameNo).setEffect(effect);
            project.setHasChanges(true);
        }
        framePreviewController.previewFrame(getSelectedFrame());
    }

    private void updateProgramLength() {
        fullTime.setText(mSecToProgramLength(project.getLength()));
    }

    private String mSecToProgramLength(long time) {
        try {
            return LocalTime.ofNanoOfDay(time * 1_000_000).format(DateTimeFormatter.ISO_TIME);
        } catch (DateTimeException dte) {
            return "> 24 ч!";
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

        List<TablePosition> selectedDataCells = getSelectedDataCells();

        if (!selectedDataCells.isEmpty()) {
            if (getSelectedFrames().size() == 1 && getSelectedFrame().getNumber() != lastFrame) {
                framePreviewController.previewFrame(getSelectedFrame());
                frameHandlersController.updateHandlers(getSelectedFrame());
                lastFrame = getSelectedFrame().getNumber();
            }
        }
    }

    private Pixel.Frame getSelectedPixelFrame() {
        final TablePosition selectedCell = getSelectedDataCells().get(0);
        final int frameNo = selectedCell.getRow();
        final int pixelNo = selectedCell.getColumn() - HEADER_COLUMNS;
        return project.getPixel(pixelNo).getFrames().get(frameNo);
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
        if (getSelectedFrames().isEmpty())
            return project.getFrame(0);
        else return getSelectedFrames().get(0);
    }

    public List<LedFrame> getSelectedFrames() {
        return frameTableView.getSelectionModel().getSelectedItems();
    }

    //    private void changePixelQuantities() {
//        final int value = chanelQuantifier.getValue();
//        frameTableView.getSelectionModel().getSelectedCells().stream().mapToInt(TablePosition::getColumn)
//                .distinct()
//                .forEach(col -> {
//                    project.getPixels().set(col - HEADER_COLUMNS, value);
//                    TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(col);
//                    String text = column.getText();
//                    column.setText(text.replaceAll("\\[\\d+]", "[" + value + "]"));
//                });
//        project.setHasChanges(true);
//        updateTotalPixelCount();
//    }
//
    @FXML
    private Label bulbIcon;

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

        frameHandlersController.init(frameLengthSpinner, frameCyclesSpinner);

        initPixelQuantifierSpinner();

    }

    /**
     * @return возвращает все выбранные ячейки списком кроме заголовочных
     */
    private List<TablePosition> getSelectedDataCells() {
        return frameTableView.getSelectionModel()
                .getSelectedCells()
                .stream()
                .filter(x -> x.getColumn() >= HEADER_COLUMNS)
                .collect(Collectors.toList());
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
                project.setPixelsCount(n);
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

    @FXML
    public void startPreview() {
//        Dialogs.preview(project);
    }

//    private void updateFramesCount() {
//        int existsFrames = frameTableView.getItems().size();
//        int needFrames = framesSpinner.getValue();
//        if (existsFrames > needFrames) {
//            for (int i = existsFrames; i > needFrames; i--) {
//                frames.remove(frames.size() - 1);
//            }
//            updateProgramLength();
//        } else if (existsFrames < needFrames) {
//            for (int i = existsFrames; i < needFrames; i++) {
//                if (project.size() == i) project.addFrame(new LedFrame());
//                frames.add(project.getFrame(i));
//            }
//            updateProgramLength();
//        }
//    }

    // скрывает колонки
    private void refreshVisibleColumnsCount() {
        int selectedPixelNumber = pixelSpinner.getValue();
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {

            final boolean expectedVisibility = i < selectedPixelNumber;
            final TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(i + HEADER_COLUMNS);
            if (column.isVisible() != expectedVisibility)
                column.setVisible(expectedVisibility);

        }

        framePreviewController.show(selectedPixelNumber);
    }

    private void initializeRowHeader() {
        frameNumColumn.setCellValueFactory(x -> x.getValue().numberProperty());
        frameRepeatColumn.setCellValueFactory(x -> x.getValue().cyclesProperty());
        frameLengthColumn.setCellFactory(column -> new LedFrameLengthCell());
        frameLengthColumn.setCellValueFactory(x -> x.getValue().frameLengthProperty());
    }

    private void disableColumnReordering() {
        frameTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) frameTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });
    }

    private void initPixelQuantifierSpinner() {
        chanelQuantifier.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        chanelQuantifier.getValueFactory().valueProperty().addListener((val, ov, nv) -> {
//            changePixelQuantities();
            framePreviewController.previewFrame(getSelectedFrame());
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
                project.setFramesCount(n);
//                updateFramesCount();
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

    @FXML
    private Tooltip errorTooltip;
    @FXML
    private Label totalPixels;

//    public void updateHeaderQuantifiers() {
//        List<Integer> quantifiers = project.getPixels();
//        for (int i = 0; i < quantifiers.size(); i++) {
//            TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(i + HEADER_COLUMNS - 1);
//            column.setText(column.getText().replaceAll("\\[\\d+]", "[" + quantifiers.get(i) + "]"));
//        }
//    }

    private void loadAndSetDefaultEffects() {
        for (Effect effect : Effect.values()) {
            effectsSelector.getItems().add(effect.toString());
        }
        effectsSelector.setValue(effectsSelector.getItems().get(0));
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

    private void initDataColumns() {
        disableColumnReordering();

        // пиксели/каналы
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            TableColumn<LedFrame, String> column = new TableColumn<>();
            final Pixel pixel = project.getPixel(i);
            column.setGraphic(ColumnHeaderFactory.get(pixel));


//            column.addEventHandler(MouseEvent.ANY,
//                    event -> {
//                        frameTableView.getSelectionModel()
//                                .selectRange(0, frameTableView.getColumns().get(absNum),
//                                        framesSpinner.getValue(), frameTableView.getColumns().get(absNum + 1));
//                        event.consume();
//                    });
            column.setPrefWidth(INIT_COL_WIDTH);
            column.setResizable(false);
            column.setSortable(false);
            column.setEditable(false);

            column.setCellFactory(cell -> new LedFrameTableCell());
            int finalI = i;
            column.setCellValueFactory(data -> data.getValue().getValue(finalI));
            column.setVisible(i < DEFAULT_PIXEL_COUNT);
            frameTableView.getColumns().add(column);
        }
    }

    @FXML
    public void applyEffectHandler() {
//        List<TablePosition> cells = getSelectedDataCells();
//        new ColorPicker(Color.WHITE).getValue();
//
//        if (cells != null && cells.size() > 0) {
//            project.setHasChanges(true);
//
//            //fixme ПЕРЕДЕЛАТЬ МЕТОД, ЧТОБЫ ВОЗВРАЩАЛ ДВУМЕРНЫЙ МАССИВ
//            //todo СДЕЛАТЬ НАСТРОЙКУ В ЭФФЕКТАХ, ЧТОБЫ МОЖНО БЫЛО ДОБАВИТЬ КАДРЫ ДО ОКОНЧАНИЯ ЭФФЕКТА
//
//            TablePosition lastCell = cells.get(cells.size() - 1);
//            TablePosition firstCell = cells.get(0);
//            int cols = lastCell.getColumn() - firstCell.getColumn() + 1;
//            int rows = lastCell.getRow() - firstCell.getRow() + 1;
//
//
//            List<IntegerProperty> values = getValuesList(cells);
//
//
//            String selectedEffect = effectsSelector.getValue();
//            Effect.valueOf(selectedEffect).apply(values, cols, rows);
//
//        }
    }

    public void setMainApp(SmartControl main) {
        mainApp = main;
    }

    public void setProject(Project project) {
        this.project = project;
        pixelSpinner.getValueFactory().setValue(project.getPixelsCount());
        framesSpinner.getValueFactory().setValue(project.programLength());
//        updateFramesCount();
//        if (frameTableView.getColumns().size() > HEADER_COLUMNS) updateHeaderQuantifiers();
        updateTotalPixelCount();

        initDataColumns();
        frameTableView.setItems(project.getFrames());

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

        framePreviewController = new FramePreviewController(this);
        framePreviewController.init(previewZone);

        initializeBrightHandlers();

        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        frameTableView.addEventHandler(EventType.ROOT, x -> handleCellSelection());
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

    public void setCycles(int cycles) {
        getSelectedFrames().forEach(frame -> frame.setCycles(cycles));
    }

    public void setLength(int length) {
        getSelectedFrames().forEach(frame -> frame.setLength(length));
        framePreviewController.previewFrame(getSelectedFrame());
    }

    public TableColumn<LedFrame, ?> getColumn(int i) {
        return frameTableView.getColumns().get(i);
    }
}
