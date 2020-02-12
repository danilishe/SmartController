package ru.isled.smartcontrol.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
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
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.view.cell.LedFrameLengthCell;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static ru.isled.smartcontrol.Constants.*;

public class MainController {
    private static final Logger log = LogManager.getLogger();

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
    private TableController tableController;


    @FXML
    public ChoiceBox<String> effectsSelector;
    @FXML
    public Slider zoomSlider;
    @FXML
    public Menu lastFiles;
    public FlowPane colorPalette;

    @FXML
    public ToggleButton animateFramePreview;
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
    private ColorPaletteController colorPaletteController;

    @FXML
    public HBox brightPalette;
    private BrightPaletteController brightPaletteController;

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

    @FXML
    public void exportHandler() {
        mainApp.exportProject();
    }

    // fixme update to new two-color model
    private void setBrightSelectedCells(int bright) {
        log.trace("updating bright to ");
        for (TablePosition cell : tableController.getSelectedDataCells()) {
            int pixelNo = cell.getColumn() - HEADER_COLUMNS;
            int frameNo = cell.getRow();
            project.getPixel(pixelNo).getFrames().get(frameNo).setBright(bright);
            project.setHasChanges(true);
        }
        framePreviewController.previewFrame(tableController.getSelectedFrame());
    }

    private void setEffectSelectedCells(PixelEffect effect) {
        log.trace("updating effect to " + effect);
        for (TablePosition cell : tableController.getSelectedDataCells()) {
            int pixelNo = cell.getColumn() - HEADER_COLUMNS;
            int frameNo = cell.getRow();
            project.getPixel(pixelNo).getFrames().get(frameNo).setEffect(effect);
            project.setHasChanges(true);
        }
        framePreviewController.previewFrame(tableController.getSelectedFrame());
    }

    public void updateProgramLength() {
        fullTime.setText(mSecToProgramLength(project.getLength()));
    }

    private String mSecToProgramLength(long time) {
        try {
            return LocalTime.ofNanoOfDay(time * 1_000_000).format(DateTimeFormatter.ISO_TIME);
        } catch (DateTimeException dte) {
            return "> 24 ч!";
        }
    }

    @FXML
    private Label bulbIcon;

    @FXML
    public void selectAll() {
        tableController.selectAll();
    }

    @FXML
    public void clearSelection() {
        tableController.clearSelection();
    }

    private void initPixelSpinner() {
        pixelSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);

        pixelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_PIXELS_COUNT, MAX_CHANNELS_COUNT, DEFAULT_PIXEL_COUNT)
        );

        pixelSpinner.getValueFactory().valueProperty().addListener((ov, o, n) -> {
            project.setPixelsCount(n);
            tableController.refreshVisibleColumnsCount();
            updateTotalPixelCount();
        });
    }

    @FXML
    private Slider gammaSlider;
    @FXML
    private Label gammaLabel;

    @FXML
    public void startPreview() {
        Dialogs.preview(project);
    }

    private void initPixelQuantifierSpinner() {
        chanelQuantifier.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, MAX_QUANTIFIER, 1));
        chanelQuantifier.setEditable(false);
        chanelQuantifier.getValueFactory().valueProperty().addListener((val, ov, nv) ->
                tableController.getSelectedPixels().forEach(pixel -> pixel.setQuantifier(nv)));
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

        framesSpinner.getValueFactory().valueProperty().addListener((ov, o, n) -> project.setFramesCount(n));
    }

    @FXML
    private Tooltip errorTooltip;
    @FXML
    private Label totalPixels;

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
        framesSpinner.getValueFactory().setValue(project.framesCount());

        updateTotalPixelCount();
        updateProgramLength();

        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            project.getPixel(i).rgbModeProperty().addListener((observable, oldValue, newValue) -> {
                refreshFramePreview();
                updateTotalPixelCount();
            });
            project.getPixel(i).quantifierProperty().addListener((observable, oldValue, newValue) -> updateTotalPixelCount());
        }

        tableController.initDataColumns();
        tableController.refreshItems();
    }

    public void refreshFramePreview() {
        framePreviewController.previewFrame(tableController.getSelectedFrame());
    }

    public Project getProject() {
        return project;
    }

    @FXML
    public void initialize() {

        frameNumColumn.setCellValueFactory(x1 -> x1.getValue().numberProperty());
        frameRepeatColumn.setCellValueFactory(x1 -> x1.getValue().cyclesProperty());
        frameLengthColumn.setCellFactory(column -> new LedFrameLengthCell());
        frameLengthColumn.setCellValueFactory(x1 -> x1.getValue().frameLengthProperty());

        // loading effects
        for (Effect effect : Effect.values()) {
            effectsSelector.getItems().add(effect.toString());
        }
        effectsSelector.setValue(effectsSelector.getItems().get(0));

        zoomSlider.setMin(MIN_COL_WIDTH);
        zoomSlider.setMax(MAX_COL_WIDTH);
        zoomSlider.setValue(INIT_COL_WIDTH);
        zoomSlider.setBlockIncrement((MAX_BRIGHT - MIN_BRIGHT) / 10d);
        zoomSlider.valueProperty().addListener((o, ov, nv) -> tableController.setColumnsWidth(nv.doubleValue()));

        gammaSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            final String value = String.format(Locale.US, "%.1f", (double) newValue);
            gammaLabel.textProperty().set(value);
            getProject().setGamma(Double.parseDouble(value));
        });

        initFrameSpinner();

        initPixelSpinner();

        frameHandlersController.init(frameLengthSpinner, frameCyclesSpinner);

        initPixelQuantifierSpinner();

        tableController = new TableController(this, frameTableView);

        framePreviewController = new FramePreviewController(this);
        framePreviewController.init(tableController.getPreviewPixels(), animateFramePreview.selectedProperty());
        animateFramePreview.selectedProperty().addListener((observable, oldValue, newValue) -> refreshFramePreview());

        colorPaletteController = new ColorPaletteController(this, colorPalette);
        brightPaletteController = new BrightPaletteController(this, brightPalette);

    }

    public void setRgbMode(RgbMode rgbMode) {
        tableController.getSelectedPixels().forEach(pixel -> pixel.setRgbMode(rgbMode));
    }

    private void updateTotalPixelCount() {
        int sum = project.getChannelsCount();
        if (sum > MAX_CHANNELS_COUNT) {
            pixelSpinner.decrement();
        } else
            totalPixels.setText(String.valueOf(sum));
    }

    public void scrollZoom(ScrollEvent scrollEvent) {
        final double currentValue = zoomSlider.getValue();
        zoomSlider.setValue(currentValue + scrollEvent.getTextDeltaY());
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
        if (scrollEvent.getDeltaY() > 0)
            frameCyclesSpinner.increment();
        else
            frameCyclesSpinner.decrement();
    }

    public void scrollQuantifier(ScrollEvent scrollEvent) {
        if (scrollEvent.getTextDeltaY() > 0)
            chanelQuantifier.increment();
        else
            chanelQuantifier.decrement();
    }

    public void scrollGamma(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() > 0) {
            gammaSlider.increment();
        } else {
            gammaSlider.decrement();
        }
    }

    public void setCycles(int cycles) {
        tableController.getSelectedFrames().forEach(frame -> frame.setCycles(cycles));
        updateProgramLength();
    }

    public void setLength(int length) {
        tableController.getSelectedFrames().forEach(frame -> frame.setLength(length));
        framePreviewController.previewFrame(tableController.getSelectedFrame());
        updateProgramLength();
    }

    public TableColumn<LedFrame, ?> getColumn(int i) {
        return tableController.getColumn(i);
    }

    public void setStartColor(Color color) {
        setColor(color, null);
    }

    public void setColor(Color startColor, Color endColor) {
        tableController.getSelectedDataCells().forEach(pos ->
                getProject().getPixel(pos.getColumn() - HEADER_COLUMNS)
                        .getFrames().get(pos.getRow())
                        .setColor(startColor, endColor)
        );
        framePreviewController.previewFrame(tableController.getSelectedFrame());
    }

    public void setEndColor(Color color) {
        setColor(null, color);
    }

    public void onDataCellsSelected(List<TablePosition> selectedDataCells) {
        chanelQuantifier.getEditor().textProperty().set("" + tableController.getSelectedPixels().get(0).getQuantifier());
    }

    public void onSelectedFrameChanged(LedFrame selectedFrame) {
        framePreviewController.previewFrame(selectedFrame);
        frameHandlersController.updateHandlers(selectedFrame);
    }

    public void setBright(Double startBright, Double endBright) {
        tableController.getSelectedDataCells().forEach(pixel -> {
                    int pixelNo = pixel.getColumn() - HEADER_COLUMNS;
                    int frameNo = pixel.getRow();
                    Pixel.Frame frame = getProject().getPixel(pixelNo).getFrames().get(frameNo);
                    if (startBright != null) {
                        Color startColor = frame.getStartColor();
                        frame.setColor(
                                Color.hsb(startColor.getHue(), startColor.getSaturation(), startBright), null);
                    }
                    if (endBright != null) {
                        Color endColor = frame.getEndColor();
                        frame.setColor(
                                null, Color.hsb(endColor.getHue(), endColor.getSaturation(), endBright));
                    }
                }
        );
    }

    public void refreshTableView() {
        tableController.getColumn(0).setVisible(false);
        tableController.getColumn(0).setVisible(true);
    }

    public void dropQuantifiers() {
        project.getPixels().forEach(pixel -> pixel.setQuantifier(1));
    }

    public void dropRgbMode() {
        project.getPixels().forEach(pixel -> pixel.setRgbMode(RgbMode.WHITE));
    }
}
