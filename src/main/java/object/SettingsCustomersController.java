package object;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.dto.DTOCustomers;
import subsidiary.classes.AlertWindow;
import java.io.IOException;
import java.util.Collection;
import static object.ODBC_PubsBD.selectCustomersList;

public class SettingsCustomersController<T extends DTOCustomers> {

    public Button addButton;
    public Button editButton;
    public Button removeButton;
    public ListView<T> listView;

    private ObservableList<T> categoryDataList = FXCollections.observableArrayList();
    private InfoObjectsController infoObjectsController;

    @FXML
    public void initialize(){
        initContextMenu();
        listView.setPlaceholder(new Label("Тут покищо немає жодного замовника"));
        listView.setItems(categoryDataList);
        listView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if ((listView.getSelectionModel().getSelectedItem()) != null) {
                setButtonsDisable(false);
            } else {
                setButtonsDisable(true);
            }
        });
    }

    public void initTableView(){
        categoryDataList.clear();
        listView.getItems().clear();
        categoryDataList.addAll((Collection<? extends T>) selectCustomersList());
    }

    public void createButtonAction(ActionEvent actionEvent) {
        showEditingCustomersWindow(null);
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void initContextMenu() {
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> addCustomer());
        addItem.setDisable(true);

        MenuItem createItem = new MenuItem("Створити нового замовника");
        createItem.setOnAction((ActionEvent event) -> showEditingCustomersWindow(null));

        MenuItem editItem = new MenuItem("Редагувати замовника");
        editItem.setOnAction((ActionEvent event) -> {
            showEditingCustomersWindow(listView.getSelectionModel().getSelectedItem());
        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити замовника");
//        removeItem.setOnAction((ActionEvent event) -> removeCategory());

        removeItem.setDisable(true);
        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(addItem, createItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }

    private void close(){
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    private void showEditingCustomersWindow(DTOCustomers dtoCustomers){
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/EditingCustomers.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingCustomersController editingCustomersController = fxmlLoader.getController();
        editingCustomersController.setSettingsCustomersController(this);
        editingCustomersController.setDtoCustomers(dtoCustomers);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 300, 162, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(listView.getScene().getWindow());
        primaryStage.showAndWait();

    }

    public void removeCategory(){
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }
//        deleteFromRepository(listView.getSelectionModel().getSelectedItem());
//        infoObjectsController.refreshRepository();
        initTableView();
    }

    public void editButtonAction(ActionEvent actionEvent) {
        showEditingCustomersWindow(listView.getSelectionModel().getSelectedItem());
    }

    public void removeButtonAction(ActionEvent actionEvent) {
        removeCategory();
    }

    private void setButtonsDisable(boolean isDisable){
        addButton.setDisable(isDisable);
        editButton.setDisable(isDisable);
        removeButton.setDisable(isDisable);
        listView.getContextMenu().getItems().get(0).setDisable(isDisable);
        listView.getContextMenu().getItems().get(2).setDisable(isDisable);
        listView.getContextMenu().getItems().get(3).setDisable(isDisable);
    }

    public void setInfoObjectsController(InfoObjectsController infoObjectsController) {
        this.infoObjectsController = infoObjectsController;
    }

    public void addButtonAction(ActionEvent actionEvent) {
        addCustomer();
    }

    private void addCustomer(){
        infoObjectsController.initCustomersView(listView.getSelectionModel().getSelectedItem().getId());
        close();
    }
}
