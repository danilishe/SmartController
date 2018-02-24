package ru.isled.controlit.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import ru.isled.controlit.Controlit;
import ru.isled.controlit.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.controlit.Constants.*;

public class MainController {

    @FXML
    public HBox previewBox;
    @FXML
    public Spinner<Integer> frameLength;
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
    public TableColumn<LedFrame, String> frameRepeatColumn;
    @FXML
    public TableColumn<LedFrame, String> frameLengthColumn;
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
    @FXML
    public Spinner<Integer> frameLengthSpinner;
    @FXML
    public Spinner<Integer> frameCyclesSpinner;
    @FXML
    public Slider zoomSlider;


    private ObservableList<LedFrame> frames = FXCollections.observableArrayList();
    private Project project;
    private List<Shape> previewPixels = new ArrayList<>(MAX_PIXELS);
    private Controlit mainApp;
    //    public Button setBlinkingFadeOutEffect;
    //    @FXML
    //    public Button setChaosEffect;
    //    @FXML
    //    public Button setBlinkingFadeInEffect;
    //    @FXML

    @FXML
    public void setFadeInEffect() {
        setSelectedCells(PixelEffect.Разгорание.index());
    }

    @FXML
    public void setBlinkEffect() {
        setSelectedCells(PixelEffect.Мерцание.index());
    }

    @FXML
    public void setFadeOutEffect() {
        setSelectedCells(PixelEffect.Угасание.index());
    }

    @FXML
    public void setBlinkingFadeInEffect() {
        setSelectedCells(PixelEffect.МерцающееРазгорание.index());
    }

    @FXML
    public void setChaosEffect() {
        setSelectedCells(PixelEffect.Хаос.index());
    }

    @FXML
    public void setBlinkingFadeOutEffect() {
        setSelectedCells(PixelEffect.МерцающееУгасание.index());
    }

    @FXML
    private void refresh() {
        // todo придумать более памятеефективный способ обновлнеия внешнего вида ячеек
        frameTableView.refresh();
    }

    @FXML
    public void newFile() {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) {
                return;
            }
        mainApp.createNewProject();
    }

    private boolean continueAfterAskSaveFile() {
        switch (Dialogs.askSaveProject()) {
            case YES:
                saveFile();
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
            File file = Dialogs.saveAs(project.getFile());
            if (file == null) return;
            project.setFileName(file);
        }

        mainApp.saveProject();

    }

    @FXML
    public void loadFile() {
        if (project.hasUnsavedChanges())
            if (!continueAfterAskSaveFile()) return;
        File fileForLoad = Dialogs.loadFile();
        if (fileForLoad != null)
            mainApp.loadProject(fileForLoad);
    }


    @FXML
    public void saveFileAs() {
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
        initializeColumns();
        initializePreviewZone();
        initializeBrightHandlers();

//        mainApp.createNewProject();


        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        frameTableView.addEventHandler(EventType.ROOT, x -> handleCellSelection());
        frameTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, x -> handleCellSelection());
        frameTableView.setItems(frames);
    }

    private void initZoomSlider() {
        zoomSlider.setMin(MIN_COL_WIDTH);
        zoomSlider.setMax(MAX_COL_WIDTH);
        zoomSlider.setValue(INIT_COL_WIDTH);
        zoomSlider.setBlockIncrement((MAX_BRIGHT - MIN_BRIGHT) / 10);
        zoomSlider.valueProperty().addListener((o, ov, nv) -> setColumnsWidth(nv.doubleValue()));
//                IntegerSpinnerValueFactory(MIN_PIXELS, MAX_PIXELS, INIT_PIXELS)
    }


    public void initializePreviewZone() {
        for (int i = 0; i < MAX_PIXELS; i++) {
            Shape pixel = new Circle(10, Color.rgb(0xFF, 0xFF, 0, 0));
            Text pixelText = new Text("" + (i + 1));
            StackPane stack = new StackPane(pixel, pixelText);
            pixel.setStroke(Color.BLACK);
            pixel.setStrokeWidth(0.7);

            if (i >= INIT_PIXELS) stack.setVisible(false);
            previewPixels.add(pixel);
            previewBox.getChildren().add(stack);
        }
    }

    public void initializeBrightHandlers() {

        maxBright.setText(String.valueOf(MAX_BRIGHT));

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
                setSelectedCells(val);

            } catch (NumberFormatException nfe) {
//                System.err.println(nfe.getCause() + " newVal=" + newValue + " oldValue=" + oldValue);
//                brightField.textProperty().setValue(oldValue);
            }
        });
    }


    private void setSelectedCells(int val) {
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

    private void handleCellSelection() {

        frameTableView.requestFocus();

        selectAllRowsWhereHeaderIsSelected();

        List<TablePosition> selectedDataCells = getSelectedDataCells();

        if (!selectedDataCells.isEmpty()) {

            redrawPreviewRow();

            TablePosition position = selectedDataCells.get(0);

            int cellValue = frames.get(position.getRow()).getInt(position.getColumn() - SYS_COLS);

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
        brightField.textProperty().setValue(String.valueOf(MAX_BRIGHT));
        setSelectedCells(MAX_BRIGHT);
    }

    @FXML
    public void setMinBright() {
        brightField.textProperty().setValue(String.valueOf(MIN_BRIGHT));
        setSelectedCells(MIN_BRIGHT);
    }

    @FXML
    public void setRandomBright() {
        int rnd = Math.round((int) (Math.random() * (MAX_BRIGHT + 1)));
        brightField.textProperty().setValue(String.valueOf(rnd));
    }

    @FXML
    public void clearSelection() {
        frameTableView.getSelectionModel().clearSelection();
    }


    private void initSpinners() {

        initFrameSpinner();

        initPixelSpinner();


    }

    private void initPixelSpinner() {
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

    private void initFrameSpinner() {
        framesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAMES, MAX_FRAMES, DEFAULT_FRAMES_COUNT)
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
//                refresh();

            } catch (NumberFormatException nfe) {
                framesSpinner.getEditor().textProperty().setValue(oldValue);
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
        } else if (existsFrames < needFrames) {
            for (int i = existsFrames; i < needFrames; i++) {
                if (project.size() == i) project.addRow(new LedFrame());
                frames.add(project.getRow(i));
            }
        }
    }

    // скрывает колонки
    private void refreshPixelCount() {
        int selectedPixelNumber = pixelSpinner.getValue();
        for (int i = 0; i < MAX_PIXELS; i++) {

            frameTableView.getColumns().get(i + SYS_COLS).visibleProperty().setValue(
                    i < selectedPixelNumber);

            previewPixels.get(i).getParent().setVisible(
                    i < selectedPixelNumber);
        }
    }

    private void initializeRowHeader() {

        frameNumColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        String.valueOf(
                                frames.indexOf(cellData.getValue()) + 1)
                ));
        frameRepeatColumn.setCellValueFactory(x ->
                new SimpleStringProperty(
                        String.valueOf(x.getValue().getCycles()
                        )
                ));
        frameLengthColumn.setCellValueFactory(x ->
                new SimpleStringProperty(
                        String.valueOf(x.getValue().getFrameLength()
                        )
                ));
    }

    private void initializeColumns() {
        disableColumnReordering();

        // пиксели/каналы
        for (int i = 0; i < MAX_PIXELS; i++) {
            TableColumn<LedFrame, Integer> column = new TableColumn<>(String.valueOf(i + 1));

            setDefaultColumnProperties(column);

            if (i >= INIT_PIXELS) column.visibleProperty().setValue(false);
            final int in = i;

            column.setCellFactory(cell -> new LedFrameTableCell(showDigits.selectedProperty(), showBright.selectedProperty()));

            column.setCellValueFactory(cellData -> cellData.getValue().get(in));

            frameTableView.getColumns().add(column);
        }
    }


    private void setDefaultColumnProperties(TableColumn<LedFrame, Integer> column) {
//        column.setMaxWidth(50);
        column.setPrefWidth(INIT_COL_WIDTH);
//        column.setMinWidth(50);
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
        if (project.hasUnsavedChanges()) {
            if (continueAfterAskSaveFile())
                mainApp.exit();
            else
                return;
        }
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
                Effect.Разгорание.apply(values, cols, rows, null, null);

            } else if (selectedEffect.equals(Effect.Угасание.name())) {
                Effect.Угасание.apply(values, cols, rows, null, null);

            } else if (selectedEffect.equals(Effect.Случайно.name())) {
                Effect.Случайно.apply(values, null, null, null, null);
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
