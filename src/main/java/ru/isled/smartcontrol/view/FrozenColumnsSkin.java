package ru.isled.smartcontrol.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableRowSkin;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.util.ArrayList;
import java.util.List;

final class FrozenColumnsSkin<T> extends TableViewSkin<T> {
    private FrozenColumnsSkin(TableView<T> table) {
        super(table);
    }

    static <T> void install(TableView<T> table, int columnCount) {
        List<TableColumnBase<?, ?>> frozenColumns = new ArrayList<>(
                table.getColumns().subList(0, Math.min(columnCount, table.getColumns().size())));
        FrozenColumnsSkin<T> skin = new FrozenColumnsSkin<>(table);
        ScrollBar bar = ((FrozenFlow<T>) skin.getVirtualFlow()).getHbarForFrozenColumns();
        ObservableNumberValue offset = Bindings.when(bar.visibleProperty()).then(bar.valueProperty()).otherwise(0.0);
        table.setRowFactory(view -> new TableRow<T>() {
            @Override
            protected TableRowSkin<T> createDefaultSkin() {
                return new TableRowSkin<T>(this) {
                    @Override
                    protected TableCell<T, ?> createCell(TableColumnBase column) {
                        TableCell<T, ?> cell = super.createCell(column);
                        if (frozenColumns.contains(column)) {
                            cell.translateXProperty().bind(offset);
                            cell.setViewOrder(-1);
                        }
                        return cell;
                    }
                };
            }
        });
        skin.frozenColumns = frozenColumns;
        skin.offset = offset;
        table.setSkin(skin);
    }

    private List<TableColumnBase<?, ?>> frozenColumns;
    private ObservableNumberValue offset;

    @Override
    protected FrozenFlow<T> createVirtualFlow() {
        return new FrozenFlow<>();
    }

    @Override
    protected TableHeaderRow createTableHeaderRow() {
        return new TableHeaderRow(this) {
            @Override
            protected NestedTableColumnHeader createRootHeader() {
                return new NestedTableColumnHeader(null) {
                    @Override
                    protected TableColumnHeader createTableColumnHeader(TableColumnBase column) {
                        TableColumnHeader header = super.createTableColumnHeader(column);
                        if (frozenColumns != null && frozenColumns.contains(column)) {
                            header.translateXProperty().bind(offset);
                            header.setViewOrder(-1);
                        }
                        return header;
                    }
                };
            }
        };
    }

    private static final class FrozenFlow<T> extends VirtualFlow<TableRow<T>> {
        private ScrollBar getHbarForFrozenColumns() {
            return getHbar();
        }
    }
}