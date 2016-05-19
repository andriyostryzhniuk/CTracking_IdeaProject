package objects.tracking;

import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import objects.tracking.dto.DTOObjectEmployees;

public class ContextMenu {

    public static void initContextMenu(TableView tableView, WindowObjectsTrackingController windowObjectsTrackingController) {
        tableView.setRowFactory(new Callback<TableView<DTOObjectEmployees>, TableRow<DTOObjectEmployees>>() {
            public TableRow<DTOObjectEmployees> call(final TableView<DTOObjectEmployees> StudentsTableView) {
                final TableRow<DTOObjectEmployees> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = StudentsTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction(event -> {
                    windowObjectsTrackingController.editRecord();
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction(event -> {
                    windowObjectsTrackingController.removeRecord(row.getItem());
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