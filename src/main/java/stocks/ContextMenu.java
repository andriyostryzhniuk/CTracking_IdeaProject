package stocks;

import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.util.Callback;
import stocks.dto.DTOStocks;

import static stocks.ODBC_PubsBD.updateStatus;

public class ContextMenu {

    public static void initContextMenu(TableView tableView, WindowStocksController windowStocksController,
                                       ChoiceBox statusChoiceBox) {
        tableView.setRowFactory(new Callback<TableView<DTOStocks>, TableRow<DTOStocks>>() {
            public TableRow<DTOStocks> call(final TableView<DTOStocks> StudentsTableView) {
                final TableRow<DTOStocks> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = StudentsTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem seeStateItem = new MenuItem("Місце знаходження");
                seeStateItem.setOnAction(event -> {
                    windowStocksController.showStateInformation(row.getItem());
                });
                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction(event -> {
                    windowStocksController.editRecord(row.getItem());
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction(event -> {
                    windowStocksController.removeRecord(row.getItem());
                });

                MenuItem backToUseItem = new MenuItem("Повернути");
                backToUseItem.setOnAction(event -> {
                    updateStatus(row.getItem().getStockId(), "доступно");
                    windowStocksController.initTableView();
                });
                backToUseItem.setDisable(true);

                MenuItem toRepairItem = new MenuItem("В ремонті");
                toRepairItem.setOnAction(event -> {
                    updateStatus(row.getItem().getStockId(), "в ремонті");
                    windowStocksController.initTableView();
                });

                MenuItem removeFromUseItem = new MenuItem("Списати");
                removeFromUseItem.setOnAction(event -> {
                    updateStatus(row.getItem().getStockId(), "списано");
                    windowStocksController.initTableView();
                });

                statusChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("Доступно")) {
                        backToUseItem.setDisable(true);
                        toRepairItem.setDisable(false);
                        removeFromUseItem.setDisable(false);
                    } else if (newValue.equals("В ремонті")) {
                        backToUseItem.setDisable(false);
                        toRepairItem.setDisable(true);
                        removeFromUseItem.setDisable(false);
                    } else {
                        backToUseItem.setDisable(false);
                        toRepairItem.setDisable(false);
                        removeFromUseItem.setDisable(true);
                    }
                });

                rowMenu.getItems().addAll(seeStateItem, new SeparatorMenuItem(),
                        editItem, removeItem, new SeparatorMenuItem(),
                        backToUseItem, toRepairItem, removeFromUseItem);
                row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                .then(rowMenu)
                                .otherwise((javafx.scene.control.ContextMenu) null));
                return row;
            }
        });
    }

}