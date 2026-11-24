package ru.isled.smartcontrol.view;

import com.sun.javafx.scene.SceneHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.isled.smartcontrol.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.*;

public class FrozenColumnsSkinTest {
    @BeforeClass
    public void startJavaFx() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(10, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
    }

    @Test
    public void keepsHeadersAndCellsFixedAcrossScrollingAndUpdates() throws Exception {
        onFxThread(() -> {
            TableView<String> table = new TableView<>();
            List<TableColumn<String, String>> columns = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                TableColumn<String, String> column = new TableColumn<>("Column " + i);
                column.setPrefWidth(60);
                column.setCellValueFactory(value -> new SimpleStringProperty(value.getValue()));
                columns.add(column);
                table.getColumns().add(column);
            }
            for (int i = 0; i < 100; i++) {
                table.getItems().add("Row " + i);
            }
            table.getSelectionModel().setCellSelectionEnabled(true);
            table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            FrozenColumnsSkin.install(table, 3);

            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(table, 360, 250));
                stage.show();
                layout(table);
                ScrollBar horizontal = (ScrollBar) table.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                ScrollBar vertical = (ScrollBar) table.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
                assertTrue(horizontal.isVisible());
                assertTrue(vertical.isVisible());

                vertical.setValue(vertical.getMin());
                layout(table);
                double[] originalHeaders = headerPositions(table, columns);
                int firstRow = visibleRow(table, 0).getIndex();
                double[] originalCells = cellPositions(table, columns, firstRow);
                horizontal.setValue(horizontal.getMax() / 2);
                layout(table);
                assertFrozenPositions(table, columns, firstRow, originalHeaders, originalCells);
                assertTrue(header(table, columns.get(3)).localToScene(header(table, columns.get(3)).getBoundsInLocal()).getMinX()
                        < originalHeaders[3] - 1);

                table.getSelectionModel().select(firstRow, columns.get(1));
                assertTrue(table.getSelectionModel().isSelected(firstRow, columns.get(1)));
                vertical.setValue(vertical.getMax());
                layout(table);
                TableRow<?> row = visibleRow(table, 90);
                assertFrozenPositions(table, columns, row.getIndex(), originalHeaders, originalCells);
                assertTrue(table.getSelectionModel().isSelected(firstRow, columns.get(1)));

                columns.get(0).setPrefWidth(80);
                columns.get(2).setVisible(false);
                columns.get(2).setVisible(true);
                TableColumn<String, String> added = new TableColumn<>("Added");
                added.setPrefWidth(80);
                table.getColumns().add(added);
                layout(table);
                double[] changedHeaders = headerPositions(table, columns);
                double[] changedCells = cellPositions(table, columns, row.getIndex());
                horizontal.setValue(horizontal.getMax());
                layout(table);
                assertFrozenPositions(table, columns, row.getIndex(), changedHeaders, changedCells);

                horizontal.setValue(0);
                layout(table);
                assertFrozenPositions(table, columns, row.getIndex(), changedHeaders, changedCells);

                for (int i = 3; i < columns.size(); i++) {
                    columns.get(i).setVisible(false);
                }
                added.setVisible(false);
                layout(table);
                assertFalse(horizontal.isVisible());
                for (int i = 0; i < 3; i++) {
                    assertEquals(header(table, columns.get(i)).getTranslateX(), 0.0, 0.01);
                    assertEquals(cell(table, columns.get(i), row.getIndex()).getTranslateX(), 0.0, 0.01);
                }
            } finally {
                stage.close();
            }
            return null;
        });
    }

    @Test
    public void fixesTheThreeColumnsOfTheActualFrameTable() throws Exception {
        onFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setProject(new Project());
            controller.getProject().getFrame(0).setFrameLength(20000);
            TableView<?> table = controller.frameTableView;
            List<? extends TableColumn<?, ?>> columns = table.getColumns();
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(root, 900, 850));
                stage.show();
                layout(table);
                ScrollBar horizontal = (ScrollBar) table.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                assertTrue(horizontal.isVisible());
                int row = visibleRow(table, 0).getIndex();
                double[] headers = headerPositions(table, columns);
                double[] cells = cellPositions(table, columns, row);
                horizontal.setValue(horizontal.getMax());
                layout(table);
                assertFrozenPositions(table, columns, row, headers, cells);
                for (int i = 0; i < 3; i++) {
                    assertTrue(cell(table, columns.get(i), row).getBackground().getFills().stream()
                            .anyMatch(fill -> fill.getFill().isOpaque()), "opaque cell " + i);
                }
                controller.zoomSlider.setValue(controller.zoomSlider.getMax());
                controller.pixelSpinner.getValueFactory().setValue(12);
                layout(table);
                assertFrozenPositions(table, columns, row, headers, cells);
            } finally {
                stage.close();
            }
            return null;
        });
    }

    @Test
    public void mouseClicksReachFrozenCellsAfterScrolling() throws Exception {
        onFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setProject(new Project());
            Stage stage = new Stage();
            try {
                Scene scene = new Scene(root, 900, 850);
                stage.setScene(scene);
                stage.show();
                TableView<?> table = controller.frameTableView;
                layout(table);
                ScrollBar horizontal = (ScrollBar) table.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                horizontal.setValue(horizontal.getMax());
                layout(table);
                AtomicReference<TableColumn<?, ?>> clickedColumn = new AtomicReference<>();
                table.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    Node picked = event.getPickResult().getIntersectedNode();
                    while (picked != null && !(picked instanceof TableCell)) {
                        picked = picked.getParent();
                    }
                    if (picked instanceof TableCell) {
                        clickedColumn.set(((TableCell<?, ?>) picked).getTableColumn());
                    }
                });
                for (int i = 0; i < 3; i++) {
                    int row = visibleRow(table, 0).getIndex();
                    TableCell<?, ?> target = cell(table, table.getColumns().get(i), row);
                    Bounds bounds = target.localToScene(target.getBoundsInLocal());
                    double x = (bounds.getMinX() + bounds.getMaxX()) / 2;
                    double y = (bounds.getMinY() + bounds.getMaxY()) / 2;
                    clickedColumn.set(null);
                    SceneHelper.processMouseEvent(scene, new MouseEvent(MouseEvent.MOUSE_PRESSED, x, y, x, y,
                            MouseButton.PRIMARY, 1, false, false, false, false, true, false, false,
                            false, false, true, null));
                    SceneHelper.processMouseEvent(scene, new MouseEvent(MouseEvent.MOUSE_RELEASED, x, y, x, y,
                            MouseButton.PRIMARY, 1, false, false, false, false, false, false, false,
                            false, false, true, null));
                    assertSame(clickedColumn.get(), table.getColumns().get(i));
                    for (int j = 3; j < 3 + controller.getProject().pixelsCount(); j++) {
                        TableColumn<?, ?> selectedColumn = table.getColumns().get(j);
                        assertTrue(table.getSelectionModel().getSelectedCells().stream()
                                .anyMatch(position -> position.getRow() == row && position.getTableColumn() == selectedColumn));
                    }
                }
            } finally {
                stage.close();
            }
            return null;
        });
    }

    private static void layout(TableView<?> table) {
        table.applyCss();
        table.layout();
    }

    private static double[] headerPositions(TableView<?> table, List<? extends TableColumn<?, ?>> columns) {
        double[] positions = new double[4];
        for (int i = 0; i < positions.length; i++) {
            Node node = header(table, columns.get(i));
            positions[i] = node.localToScene(node.getBoundsInLocal()).getMinX();
        }
        return positions;
    }

    private static double[] cellPositions(TableView<?> table, List<? extends TableColumn<?, ?>> columns, int row) {
        double[] positions = new double[3];
        for (int i = 0; i < positions.length; i++) {
            Node node = cell(table, columns.get(i), row);
            positions[i] = node.localToScene(node.getBoundsInLocal()).getMinX();
        }
        return positions;
    }

    private static void assertFrozenPositions(TableView<?> table, List<? extends TableColumn<?, ?>> columns, int row,
                                              double[] headers, double[] cells) {
        double[] actualHeaders = headerPositions(table, columns);
        double[] actualCells = cellPositions(table, columns, row);
        for (int i = 0; i < 3; i++) {
            assertEquals(actualHeaders[i], headers[i], 1.0, "header " + i);
            assertEquals(actualCells[i], cells[i], 1.0, "cell " + i);
            assertEquals(actualHeaders[i], actualCells[i], 1.0, "alignment " + i);
            assertTrue(header(table, columns.get(i)).getViewOrder() < 0);
            assertTrue(cell(table, columns.get(i), row).getViewOrder() < 0);
        }
    }

    private static TableColumnHeader header(TableView<?> table, TableColumn<?, ?> column) {
        return table.lookupAll(".column-header").stream()
                .filter(node -> node instanceof TableColumnHeader)
                .map(node -> (TableColumnHeader) node)
                .filter(node -> node.getTableColumn() == column)
                .findFirst().orElseThrow(AssertionError::new);
    }

    private static TableRow<?> visibleRow(TableView<?> table, int minimumIndex) {
        Node flow = table.lookup(".virtual-flow");
        Bounds viewport = flow.localToScene(flow.getBoundsInLocal());
        return table.lookupAll(".table-row-cell").stream()
                .filter(node -> node instanceof TableRow)
                .map(node -> (TableRow<?>) node)
                .filter(row -> row.getIndex() >= minimumIndex && row.getIndex() < table.getItems().size())
                .filter(row -> {
                    Bounds bounds = row.localToScene(row.getBoundsInLocal());
                    double centerY = (bounds.getMinY() + bounds.getMaxY()) / 2;
                    return row.isVisible() && centerY > viewport.getMinY() && centerY < viewport.getMaxY() - 20;
                })
                .findFirst().orElseThrow(AssertionError::new);
    }

    private static TableCell<?, ?> cell(TableView<?> table, TableColumn<?, ?> column, int row) {
        return table.lookupAll(".table-cell").stream()
                .filter(node -> node instanceof TableCell)
                .map(node -> (TableCell<?, ?>) node)
                .filter(node -> node.getTableColumn() == column && node.getIndex() == row)
                .findFirst().orElseThrow(() -> new AssertionError("Missing cell for " + column.getText() + " row " + row));
    }

    private static <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(30, TimeUnit.SECONDS);
    }
}