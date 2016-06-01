package stocks;

import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import stocks.dto.DTOStocks;

public class ContextMenu {

    public static void initContextMenu(TableView tableView, WindowStocksController windowStocksController) {
        tableView.setRowFactory(new Callback<TableView<DTOStocks>, TableRow<DTOStocks>>() {
            public TableRow<DTOStocks> call(final TableView<DTOStocks> StudentsTableView) {
                final TableRow<DTOStocks> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = StudentsTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction(event -> {
                    windowStocksController.editRecord(row.getItem());
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction(event -> {
                    windowStocksController.removeRecord(row.getItem());
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