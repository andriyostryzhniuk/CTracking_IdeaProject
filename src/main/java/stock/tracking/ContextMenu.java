package stock.tracking;

import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import stock.tracking.dto.DTOStockTracking;

public class ContextMenu {

    public static void initContextMenu(TableView tableView, WindowStockTrackingController windowStockTrackingController) {
        tableView.setRowFactory(new Callback<TableView<DTOStockTracking>, TableRow<DTOStockTracking>>() {
            public TableRow<DTOStockTracking> call(final TableView<DTOStockTracking> dtoStockTrackingTableView) {
                final TableRow<DTOStockTracking> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = dtoStockTrackingTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction(event -> {
                    windowStockTrackingController.editRecord(row.getItem());
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction(event -> {
                    windowStockTrackingController.removeRecord(row.getItem());
                });
                rowMenu.getItems().addAll(editItem, removeItem);
                row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                .then(rowMenu)
                                .otherwise((javafx.scene.control.ContextMenu) null));
                return row;
            }
        });
    }

}