package stocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import stocks.dto.DTOStockCategory;
import subsidiary.classes.AlertWindow;
import java.io.IOException;
import java.util.Collection;
import static stocks.ODBC_PubsBD.deleteFromStockCategory;
import static stocks.ODBC_PubsBD.selectStockCategoryList;

public class SettingsStockCategoryController<T extends DTOStockCategory> {

    public TableView<T> tableView;

    public Button editButton;
    public Button removeButton;
    public TableColumn nameCol;
    public TableColumn typeCol;

    private ObservableList<T> categoryDataList = FXCollections.observableArrayList();
    private WindowStocksController windowStocksController;

    @FXML
    public void initialize(){
        fillCols();
        initContextMenu();
        tableView.setPlaceholder(new Label("Покищо тут немає жодної категорії"));
        tableView.setItems(categoryDataList);
        tableView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if ((tableView.getSelectionModel().getSelectedItem()) != null) {
                setButtonsDisable(false);
            } else {
                setButtonsDisable(true);
            }
        });
    }

    public void initTableView(){
        categoryDataList.clear();
        tableView.getItems().clear();
        categoryDataList.addAll((Collection<? extends T>) selectStockCategoryList());
    }

    private void fillCols() {
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
    }

    public void addButtonAction(ActionEvent actionEvent) {
        showEditingCategoryWindow(null);
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void initContextMenu() {
        MenuItem createItem = new MenuItem("Створити нову категорію");
        createItem.setOnAction((ActionEvent event) -> showEditingCategoryWindow(null));

        MenuItem editItem = new MenuItem("Редагувати категорію");
        editItem.setOnAction((ActionEvent event) -> {
            showEditingCategoryWindow(tableView.getSelectionModel().getSelectedItem());
        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити категорію");
        removeItem.setOnAction((ActionEvent event) -> removeCategory());

        removeItem.setDisable(true);
        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(createItem, editItem, removeItem);

        tableView.setContextMenu(cellMenu);
    }

    private void close(){
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }

    private void showEditingCategoryWindow(DTOStockCategory dtoStockCategory){
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stocks/EditingCategory.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingCategoryController editingCategoryController = fxmlLoader.getController();
        editingCategoryController.setSettingsStockCategoryController(this);
        editingCategoryController.setWindowStocksController(windowStocksController);
        editingCategoryController.setDtoStockCategory(dtoStockCategory);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 300, 175, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getScene().getWindow());
        primaryStage.showAndWait();

    }

    public void removeCategory(){
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }
        deleteFromStockCategory(tableView.getSelectionModel().getSelectedItem());
        windowStocksController.refreshCategory();
        initTableView();
    }

    public void editButtonAction(ActionEvent actionEvent) {
        showEditingCategoryWindow(tableView.getSelectionModel().getSelectedItem());
    }

    public void removeButtonAction(ActionEvent actionEvent) {
        removeCategory();
    }

    private void setButtonsDisable(boolean isDisable){
        editButton.setDisable(isDisable);
        removeButton.setDisable(isDisable);
        tableView.getContextMenu().getItems().get(1).setDisable(isDisable);
        tableView.getContextMenu().getItems().get(2).setDisable(isDisable);
    }

    public void setWindowStocksController(WindowStocksController windowStocksController) {
        this.windowStocksController = windowStocksController;
    }

}
