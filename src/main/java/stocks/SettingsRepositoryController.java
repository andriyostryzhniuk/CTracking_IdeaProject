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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import stocks.dto.DTORepository;
import subsidiary.classes.AlertWindow;

import java.io.IOException;
import java.util.Collection;

import static stocks.ODBC_PubsBD.deleteFromRepository;
import static stocks.ODBC_PubsBD.selectRepositoryList;

public class SettingsRepositoryController<T extends DTORepository> {

    public Button editButton;
    public Button removeButton;
    public ListView<T> listView;

    private ObservableList<T> categoryDataList = FXCollections.observableArrayList();
    private WindowStocksController windowStocksController;

    @FXML
    public void initialize(){
        initContextMenu();
        listView.setPlaceholder(new Label("Тут покищо немає жодного складу"));
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
        categoryDataList.addAll((Collection<? extends T>) selectRepositoryList());
    }

    public void addButtonAction(ActionEvent actionEvent) {
        showEditingRepositoryWindow(null);
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void initContextMenu() {
        MenuItem createItem = new MenuItem("Додати новий склад");
        createItem.setOnAction((ActionEvent event) -> showEditingRepositoryWindow(null));

        MenuItem editItem = new MenuItem("Редагувати склад");
        editItem.setOnAction((ActionEvent event) -> {
            showEditingRepositoryWindow(listView.getSelectionModel().getSelectedItem());
        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити склад");
        removeItem.setOnAction((ActionEvent event) -> removeCategory());

        removeItem.setDisable(true);
        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(createItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }

    private void close(){
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    private void showEditingRepositoryWindow(DTORepository dtoRepository){
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stocks/EditingRepository.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingRepositoryController editingRepositoryController = fxmlLoader.getController();
        editingRepositoryController.setSettingsRepositoryController(this);
        editingRepositoryController.setWindowStocksController(windowStocksController);
        editingRepositoryController.setDtoRepository(dtoRepository);

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
        deleteFromRepository(listView.getSelectionModel().getSelectedItem());
        windowStocksController.refreshRepository();
        initTableView();
    }

    public void editButtonAction(ActionEvent actionEvent) {
        showEditingRepositoryWindow(listView.getSelectionModel().getSelectedItem());
    }

    public void removeButtonAction(ActionEvent actionEvent) {
        removeCategory();
    }

    private void setButtonsDisable(boolean isDisable){
        editButton.setDisable(isDisable);
        removeButton.setDisable(isDisable);
        listView.getContextMenu().getItems().get(1).setDisable(isDisable);
        listView.getContextMenu().getItems().get(2).setDisable(isDisable);
    }

    public void setWindowStocksController(WindowStocksController windowStocksController) {
        this.windowStocksController = windowStocksController;
    }

}
