package ru.isled.smartcontrol.view;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.view.cell.LedFrameTableCell;
import ru.isled.smartcontrol.view.cell.PixelHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.*;

public class TableController {
    private final MainController mainController;
    private final TableView<LedFrame> frameTableView;
    private int lastFrame = -1;
    private final List<Shape> previewPixels = new ArrayList<>(MAX_CHANNELS_COUNT);

    public TableController(MainController mainController, TableView<LedFrame> frameTableView) {
        this.mainController = mainController;
        this.frameTableView = frameTableView;

        frameTableView.getSelectionModel().setCellSelectionEnabled(true);
        frameTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        frameTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, x -> handleCellSelection());

        disableColumnReordering();

        ContextMenu tableContextMenu = new ContextMenu();

        for (RgbMode rgbMode : RgbMode.values()) {
            Shape graphic = new Rectangle(20, 10);
            graphic.setStyle(rgbMode.getBackground().replace("background-color", "fill"));
            graphic.setStroke(Color.BLACK);
            MenuItem menuItem = new MenuItem(rgbMode.name(), graphic);
            menuItem.setOnAction(event -> mainController.setRgbMode(rgbMode));
            tableContextMenu.getItems().add(menuItem);
        }
        frameTableView.setContextMenu(tableContextMenu);
    }


    private void disableColumnReordering() {
        frameTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) frameTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });
    }

    private void handleCellSelection() {
        frameTableView.requestFocus();

        selectAllRowsWhereHeaderIsSelected();

        List<TablePosition> selectedDataCells = getSelectedDataCells();

        if (!selectedDataCells.isEmpty()) {
            mainController.onDataCellsSelected(selectedDataCells);
            if (getSelectedFrames().size() == 1 && getSelectedFrame().getNumber() != lastFrame) {
                mainController.onSelectedFrameChanged(getSelectedFrame());
                lastFrame = getSelectedFrame().getNumber();
            }
        }
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
                    frameTableView.getColumns().get(lastVisibleColumnIndex()));
        }
    }

    private Pixel.Frame getSelectedPixelFrame() {
        final TablePosition selectedCell = getSelectedDataCells().get(0);
        final int frameNo = selectedCell.getRow();
        final int pixelNo = selectedCell.getColumn() - HEADER_COLUMNS;
        return mainController.getProject().getPixel(pixelNo).getFrames().get(frameNo);
    }

    private List<TablePosition> getSelectedHeaderCells() {
        return frameTableView.getSelectionModel().getSelectedCells()
                .stream().filter(x -> x.getColumn() < HEADER_COLUMNS)
                .collect(Collectors.toList());
    }

    public LedFrame getSelectedFrame() {
        if (getSelectedFrames().isEmpty())
            return mainController.getProject().getFrame(0);
        else return getSelectedFrames().get(0);
    }

    public List<LedFrame> getSelectedFrames() {
        return frameTableView.getSelectionModel().getSelectedItems();
    }

    public void selectAll() {
        List<TableColumn<LedFrame, ?>> colsList = frameTableView.getColumns();
        int rows = frameTableView.getItems().size();

        clearSelection();

        frameTableView.getSelectionModel().selectRange(0, colsList.get(HEADER_COLUMNS),
                rows - 1, colsList.get(lastVisibleColumnIndex()));
    }

    private int lastVisibleColumnIndex() {
        return mainController.getProject().pixelsCount() + HEADER_COLUMNS - 1;
    }

    public void clearSelection() {
        frameTableView.getSelectionModel().clearSelection();
    }


    /**
     * @return возвращает все выбранные ячейки списком кроме заголовочных
     */
    List<TablePosition> getSelectedDataCells() {
        return frameTableView.getSelectionModel()
                .getSelectedCells()
                .stream()
                .filter(x -> x.getColumn() >= HEADER_COLUMNS)
                .collect(Collectors.toList());
    }

    // скрывает колонки
    void refreshVisibleColumnsCount() {
        int selectedPixelNumber = mainController.getProject().pixelsCount();
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            final boolean expectedVisibility = i < selectedPixelNumber;
            final TableColumn<LedFrame, ?> column = frameTableView.getColumns().get(i + HEADER_COLUMNS);
            if (column.isVisible() != expectedVisibility)
                column.setVisible(expectedVisibility);

        }
    }

    public List<Pixel> getSelectedPixels() {
        return getSelectedDataCells().stream()
                .map(position -> position.getColumn() - HEADER_COLUMNS)
                .distinct()
                .map(i -> mainController.getProject().getPixel(i))
                .collect(Collectors.toList());
    }

    public void initDataColumns(int visibleCount) {
        // preparation
        frameTableView.getColumns().remove(3, frameTableView.getColumns().size());
        // пиксели/каналы
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            TableColumn<LedFrame, String> column = new TableColumn<>();
            final Pixel pixel = mainController.getProject().getPixel(i);
            PixelHeader header = new PixelHeader(pixel);
            previewPixels.add(header.previewPixel);

            column.setGraphic(header);
            column.setPrefWidth(INIT_COL_WIDTH);
            column.setResizable(false);
            column.setSortable(false);
            column.setEditable(false);

            column.setCellFactory(cell -> new LedFrameTableCell());
            int finalI = i;
            column.setCellValueFactory(data -> data.getValue().getValue(finalI));
            column.setVisible(i < visibleCount);
            frameTableView.getColumns().add(column);
        }
    }

    void setColumnsWidth(double columnsWidth) {
        frameTableView.getColumns().stream()
                .skip(HEADER_COLUMNS).forEach(col -> {
            col.setPrefWidth(columnsWidth);
        });
    }

    public void refresh() {
        frameTableView.refresh();
    }

    public void refreshItems() {
        frameTableView.setItems(mainController.getProject().getFrames());
    }

    public TableColumn<LedFrame, ?> getColumn(int i) {
        return frameTableView.getColumns().get(i);
    }

    public List<Shape> getPreviewPixels() {
        return previewPixels;
    }
}
