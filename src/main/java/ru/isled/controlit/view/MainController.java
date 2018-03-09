package ru.isled.controlit.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;
import ru.isled.controlit.Controlit;
import ru.isled.controlit.model.*;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.controlit.Constants.*;

public class MainController {

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


    private ObservableList<LedFrame> frames = FXCollections.observableArrayList();
    private Project project;
    private List<Shape> previewPixels = new ArrayList<>(MAX_PIXELS_COUNT);
    private Controlit mainApp;

    @FXML
    public void setFadeInEffect() {
        setBrightSelectedCells(PixelEffect.Разгорание.index());
    }

    @FXML
    public void setBlinkEffect() {
        setBrightSelectedCells(PixelEffect.Мерцание.index());
    }

    @FXML
    public void setFadeOutEffect() {
        setBrightSelectedCells(PixelEffect.Угасание.index());
    }

    @FXML
    public void setBlinkingFadeInEffect() {
        setBrightSelectedCells(PixelEffect.МерцающееРазгорание.index());
    }

    @FXML
    public void setChaosEffect() {
        setBrightSelectedCells(PixelEffect.Хаос.index());
    }

    @FXML
    public void setBlinkingFadeOutEffect() {
        setBrightSelectedCells(PixelEffect.МерцающееУгасание.index());
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
//        project.setPixelCount(pixelSpinner.getValue());
        mainApp.saveProject();
    }

    @FXML
    public void loadFile() {
        mainApp.loadProject();
    }


    @FXML
    public void saveFileAs() {
//        project.setFrameCount(framesSpinner.getValue());
//        project.setPixelCount(pixelSpinner.getValue());
        File saveAs = Dialogs.saveAs(project.getFile());
        if (saveAs == null) return;
        project.setFileName(saveAs);
        mainApp.saveProject();
    }

    @FXML
    public void initialize() {
        initializeRowHeader();
        loadAndSetDefaultEffects();

        initZoomSlider();

        initSpinners();
        initDataColumns();
        initializePreviewZone();
        initializeBrightHandlers();

        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        frameTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, x -> handleCellSelection());
        frameTableView.setItems(frames);
//        frames.addListener((ListChangeListener<LedFrame>) c -> updateProgramLength());
    }

    private void initZoomSlider() {
        zoomSlider.setMin(MIN_COL_WIDTH);
        zoomSlider.setMax(MAX_COL_WIDTH);
        zoomSlider.setValue(INIT_COL_WIDTH);
        zoomSlider.setBlockIncrement((MAX_BRIGHT - MIN_BRIGHT) / 10);
        zoomSlider.valueProperty().addListener((o, ov, nv) -> setColumnsWidth(nv.doubleValue()));
    }


    private void initializePreviewZone() {
        for (int i = 0; i < MAX_PIXELS_COUNT; i++) {
            Shape pixel = new Circle(10, Color.rgb(0xFF, 0xFF, 0, 0));
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
        maxBright.setText(String.valueOf(MAX_BRIGHT));
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

    private void setBrightSelectedCells(int val) {
        for (TablePosition cell : getSelectedDataCells()) {
            int pixel = cell.getColumn() - SYS_COLS;
            int frame = cell.getRow();
            if (frames.get(frame).getInt(pixel) != val) {
                frames.get(frame).set(pixel, val);
                project.setHasChanges(true);
            }
        }
        redrawPreviewRow();
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

    @FXML
    private Label fullTime;

    private void updateProgramLength() {
        System.out.println("update prog length");
        long time = frameTableView.getItems().stream()
                .mapToLong(item -> item.getCycles().get() * item.getFrameLength().get())
                .sum();

        String aTime = millisectondsToProgramLength(time);
        fullTime.setText(aTime);
    }

    private String millisectondsToProgramLength(long time) {

        try {
            LocalTime dateTime = LocalTime.ofNanoOfDay(time * 1000_000);
            return dateTime.format(DateTimeFormatter.ISO_TIME);
        } catch (DateTimeException dte) {
//            synchronized (this) {
//                Dialogs.showErrorAlert("Максимально допустимая длина программы - 24 часа. Необходимо уменьшить длительность программы.");
//            }
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

        List<TablePosition> selectedDataCells = getSelectedDataCells();

        if (!selectedDataCells.isEmpty()) {

            redrawPreviewRow();

            TablePosition position = selectedDataCells.get(0);

            int cellValue = frames.get(position.getRow()).getInt(position.getColumn() - SYS_COLS);

            int frameLeng = frames.get(position.getRow()).getFrameLength().get();
            int frameCycl = frames.get(position.getRow()).getCycles().get();

            if (selectedDataCells.size() == 1) {

                frameLengthSpinner.getValueFactory().setValue(frameLeng);
                frameCyclesSpinner.getValueFactory().setValue(frameCycl);

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
                    frameTableView.getColumns().get(SYS_COLS),
                    lastRow,
                    frameTableView.getColumns().get(pixelSpinner.getValue() + SYS_COLS - 1));
        }
    }

    private List<TablePosition> getSelectedHeaderCells() {
        return frameTableView.getSelectionModel().getSelectedCells()
                .stream().filter(x -> x.getColumn() < SYS_COLS)
                .collect(Collectors.toList());
    }

    private int getSelectedRow() {
        try {
            return frameTableView.getSelectionModel().getSelectedCells().get(0).getRow();
        } catch (IndexOutOfBoundsException out) {
            return -1;
        }
    }

    private void setPreviewRow(int row) {
        // защита от попытки перерисовки при отсутствии выделенных ячеек
        if (row < 0) return;

        LedFrame frame = frames.get(row);
        for (int i = 0; i < pixelSpinner.getValue(); i++) {

            Shape pixel = previewPixels.get(i);

            pixel.getStyleClass().clear();

            if (frame.getInt(i) <= MAX_BRIGHT) {
                pixel.getStyleClass().clear();
                pixel.fillProperty().setValue(
                        Color.rgb(0xFF, 0xFF, 0, ((double) frame.getInt(i) / MAX_BRIGHT)));
            } else {
//                pixel.fillProperty().setValue(
//                        null
//                );

                if (pixel.getStyleClass().isEmpty())
                    pixel.getStyleClass().add(PixelEffect.cssByIndex(frame.getInt(i)));
                else
                    pixel.getStyleClass().set(0, PixelEffect.cssByIndex(frame.getInt(i)));

            }
        }
    }


    /**
     * @return возвращает все выбранные ячейки списком кроме заголовочных
     */
    private List<TablePosition> getSelectedDataCells() {
        return frameTableView.getSelectionModel().getSelectedCells()
                .stream()
                .filter(x -> x.getColumn() >= SYS_COLS)
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

        clearSelection();

        frameTableView.getSelectionModel().selectRange(0, colsList.get(SYS_COLS),
                rows - 1, colsList.get(lastVisibleColNumber + SYS_COLS - 1));
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
            System.out.println("oldValue = " + oldValue);
            System.out.println("newValue = " + newValue);
            System.out.println();

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
                System.out.println("frameLengthSpinner.val = " + frameLengthSpinner.getValue());
                frameTableView.getColumns().get(SYS_COLS - 1).setVisible(false);
                frameLengthSpinner.getEditor().commitValue();
                frameTableView.getColumns().get(SYS_COLS - 1).setVisible(true);
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
                    frameTableView.getColumns().get(SYS_COLS - 1).setVisible(false);
                    frameLengthSpinner.getValueFactory().setValue(val);
                    frameTableView.getColumns().get(SYS_COLS - 1).setVisible(true);
                }
            }
        });

    }

    private void initPixelSpinner() {
        pixelSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);

        pixelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_PIXELS_COUNT, MAX_PIXELS_COUNT, DEFAULT_PIXEL_COUNT)
        );

        pixelSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d+")) {
                pixelSpinner.getEditor().textProperty().setValue(oldValue);
            }
        });

        pixelSpinner.getValueFactory().valueProperty().addListener((ov, o, n) -> {
            if (n < MIN_PIXELS_COUNT)
                pixelSpinner.getValueFactory().setValue(MIN_PIXELS_COUNT);
            else if (n > MAX_PIXELS_COUNT)
                pixelSpinner.getValueFactory().setValue(MAX_PIXELS_COUNT);
            else {
                project.setPixelCount(n);
                refreshPixelCount();
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
                if (project.size() == i) project.addRow(new LedFrame());
                frames.add(project.getRow(i));
            }
            updateProgramLength();
        }
    }

    // скрывает колонки
    private void refreshPixelCount() {
        int selectedPixelNumber = pixelSpinner.getValue();
        for (int i = 0; i < MAX_PIXELS_COUNT; i++) {

            frameTableView.getColumns().get(i + SYS_COLS).visibleProperty().setValue(
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
        for (int i = 0; i < MAX_PIXELS_COUNT; i++) {
            TableColumn<LedFrame, Integer> column = new TableColumn<>(String.valueOf(i + 1));

            setDefaultColumnProperties(column);

            if (i >= DEFAULT_PIXEL_COUNT) column.visibleProperty().setValue(false);
            final int in = i;

            column.setCellFactory(cell -> new LedFrameTableCell(showDigits.selectedProperty(), showBright.selectedProperty()));

            column.setCellValueFactory(cellData -> cellData.getValue().get(in));

            frameTableView.getColumns().add(column);
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
        Dialogs.showAboutInfo();
    }

    @FXML
    public void applyEffectHandler() {
        List<TablePosition> cells = getSelectedDataCells();
        if (cells != null && cells.size() > 0) {
            TablePosition lastCell = cells.get(cells.size() - 1);
            TablePosition firstCell = cells.get(0);
            int cols = lastCell.getColumn() - firstCell.getColumn() + 1;
            int rows = lastCell.getRow() - firstCell.getRow() + 1;


            List<IntegerProperty> values = getValuesList(cells);


            String selectedEffect = effectsSelector.getValue();
            if (selectedEffect.equals(Effect.Разгорание.name())) {
                Pair<Integer, Integer> props = Dialogs.getFadeInProperties();
                Effect.Разгорание.apply(values, cols, rows, props.getKey(), props.getValue(), 0);

            } else if (selectedEffect.equals(Effect.Угасание.name())) {
                Pair<Integer, Integer> props = Dialogs.getFadeOutProperties();
                Effect.Угасание.apply(values, cols, rows, props.getKey(), props.getValue(), 0);

            } else if (selectedEffect.equals(Effect.Случайно.name())) {
                Pair<Integer, Integer> props = Dialogs.getFadeInProperties();
                Effect.Случайно.apply(values, null, null, props.getKey(), props.getValue(), 0);
            }

        }
//        redrawPreviewRow();
    }

    private List<IntegerProperty> getValuesList(List<TablePosition> cells) {
        return cells.stream()
                .map(c -> frames.get(
                        c.getRow()).getProperty(
                        c.getColumn() - SYS_COLS))
                .collect(Collectors.toList());
    }

    private void redrawPreviewRow() {
        setPreviewRow(getSelectedRow());
    }

    public void setMainApp(Controlit main) {
        mainApp = main;
    }

    public void setProject(Project project) {
        this.project = project;
        frames.clear();
        updateFramesCount();
    }

    private void setColumnsWidth(double columnsWidth) {
        frameTableView.getColumns().stream()
                .skip(SYS_COLS).forEach(col -> {
//            col.setMinWidth(columnsWidth);
            col.setPrefWidth(columnsWidth);
//            col.setMaxWidth(columnsWidth);
        });
    }
}
