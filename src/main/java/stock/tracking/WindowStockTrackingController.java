package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import stock.tracking.dto.DTOStockTracking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import static stock.tracking.ContextMenu.initContextMenu;
import static stock.tracking.ODBC_PubsBDForLiable.*;

public class WindowStockTrackingController<T extends DTOStockTracking> {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane rightSideGridPane;
    public StackPane stackPane;

    public ChoiceBox contentChoiceBox;
    public ChoiceBox stockTypeChoiceBox;
    public ChoiceBox repositoryChoiceBox;
    public CheckBox onlyAvailableStockCheckBox;
    public ChoiceBox liableTypeChoiceBox;

    public TextArea notesTextArea;

    public StockListViewController stockListViewController;
    public LiableListViewController liableListViewController;

    public Label objectNameLabel;
    public Label employeesNameLabel;
    private ObservableList<T> tableViewDataList = FXCollections.observableArrayList();

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> stockNameCol = new CustomTableColumn<>("Інвентар");
    public CustomTableColumn<T, String> givingDateCol = new CustomTableColumn<>("Видано");
    public CustomTableColumn<T, String> returnDateCol = new CustomTableColumn<>("Повернено");

    @FXML
    public void initialize() {
        notesTextArea.getStylesheets().add(getClass().getResource("/styles/TextAreaStyle.css").toExternalForm());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/StockListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            stockListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/LiableListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 3, 0);
            liableListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initLeftSideGritPane();
        setTableViewParameters();

        liableListViewController.setStockListViewController(stockListViewController);
        stockListViewController.setWindowStockTrackingController(this);
        liableListViewController.setWindowStockTrackingController(this);
        stockListViewController.initListView(false);
        liableListViewController.initListView(false);

    }

    public void initLeftSideGritPane() {
        initContentChoiceBox();
        initStockTypeChoiceBox();
        initRepositoryChoiceBox();
        initOnlyAvailableStockCheckBox();
        initLiableTypeChoiceBox();
    }

    public void initContentChoiceBox(){
        contentChoiceBox.setTooltip(new Tooltip("Вибрати перегляд"));

        contentChoiceBox.getItems().addAll("Категорії", "Весь інвентар");
        contentChoiceBox.setValue(contentChoiceBox.getItems().get(0));
        stockListViewController.setContentType(contentChoiceBox.getValue().toString());

        contentChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            stockListViewController.setContentType(contentChoiceBox.getValue().toString());
            stockListViewController.initListView(false);
        });
    }

    public void initStockTypeChoiceBox() {
        stockTypeChoiceBox.setTooltip(new Tooltip("Вибрати тип інвентаря\n(Вартісний/Розхідний)"));
        stockTypeChoiceBox.getItems().addAll("Вартісні", "Розхідні");

        stockTypeChoiceBox.setValue(stockTypeChoiceBox.getItems().get(0));
        stockListViewController.setStockType(stockTypeChoiceBox.getValue().toString());

        stockTypeChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            stockListViewController.setStockType(stockTypeChoiceBox.getValue().toString());
            if (! stockListViewController.getContentType().equals("Категорії") &&
                    ! stockListViewController.getContentType().equals("Весь інвентар")) {
                stockListViewController.setContentType("Категорії");
            }
            stockListViewController.setStockType(newValue.toString());
            stockListViewController.initListView(false);
        });
    }

    public void initRepositoryChoiceBox(){
        repositoryChoiceBox.setTooltip(new Tooltip("Вибрати склад"));

        repositoryChoiceBox.setItems(ODBC_PubsBDForStock.selectRepositoryName());
        repositoryChoiceBox.setValue(repositoryChoiceBox.getItems().get(0));
        stockListViewController.setRepository(repositoryChoiceBox.getValue().toString());

        repositoryChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            stockListViewController.setRepository(newValue.toString());
            stockListViewController.initListView(false);
        });
    }

    public void initOnlyAvailableStockCheckBox(){
        onlyAvailableStockCheckBox.setTooltip(new Tooltip("Показувати категорії в яких на даний момент" +
                "\nнемає жодного доступного інвентаря"));
        onlyAvailableStockCheckBox.setSelected(true);
        onlyAvailableStockCheckBox.selectedProperty().addListener(observable -> {
            if (stockListViewController.getContentType().equals("Категорії")) {
                stockListViewController.setOnlyAvailableStock(onlyAvailableStockCheckBox.isSelected());
                stockListViewController.initListView(true);
            }
        });
    }

    public void initLiableTypeChoiceBox(){
        liableTypeChoiceBox.setTooltip(new Tooltip("Вибрати перегляд"));

        liableTypeChoiceBox.getItems().addAll("Об'єкти", "Працівники");
        liableTypeChoiceBox.setValue(liableTypeChoiceBox.getItems().get(0));
        liableListViewController.setLiableType(liableTypeChoiceBox.getValue().toString());

        liableTypeChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            liableListViewController.setLiableType(liableTypeChoiceBox.getValue().toString());
            liableListViewController.initListView(false);
        });
    }

    public void aboutStockInfoClear(){
        notesTextArea.clear();
    }

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("Не закріплено жодного інвентаря"));

//        tableView.getTableView().getSelectionModel().selectedItemProperty().addListener(event -> {
//            T selectedItem;
//            if ((selectedItem = tableView.getTableView().getSelectionModel().getSelectedItem()) != null) {
//
//            } else {
//
//            }
//        });
    }

    private void fillCols() {
        stockNameCol.setCellValueFactory(new PropertyValueFactory("stockName"));
        givingDateCol.setCellValueFactory(new PropertyValueFactory("formatGivingDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory("formatReturnDate"));
    }

    private void setColsDateProperties() {
        stockNameCol.setPercentWidth(118); stockNameCol.setMinWidth(118);
        givingDateCol.setPercentWidth(65); givingDateCol.setMinWidth(65);
        returnDateCol.setPercentWidth(65); returnDateCol.setMinWidth(65);
    }

    private void fillTableView(){
        tableView.getTableView().getColumns().addAll(stockNameCol, givingDateCol, returnDateCol);
    }

    public void initTableView(Integer objectId, Integer employeeId){
        clearTableView();
        if (objectId != null) {
            objectNameLabel.setText(selectObjectAddress(objectId));
        }
        if (employeeId != null) {
            employeesNameLabel.setText(selectEmployeesName(employeeId));
        }
        tableViewDataList.addAll((Collection<? extends T>)
                FXCollections.observableArrayList(selectStockTracking(objectId, employeeId, LocalDate.now())));

        tableView.getTableView().setItems(tableViewDataList);
    }

    public void clearTableView(){
        tableViewDataList.clear();
        tableView.getTableView().getItems().clear();
        objectNameLabel.setText(null);
        employeesNameLabel.setText(null);
    }

    public void removeRecord(T item) {
        deleteFromStockTracking(item.getId());
        liableListViewController.initListView(true);
        stockListViewController.initListView(true);

    }

    public ChoiceBox getContentChoiceBox() {
        return contentChoiceBox;
    }

    public CheckBox getOnlyAvailableStockCheckBox() {
        return onlyAvailableStockCheckBox;
    }

    public TextArea getNotesTextArea() {
        return notesTextArea;
    }

    public ChoiceBox getLiableTypeChoiceBox() {
        return liableTypeChoiceBox;
    }

    public void editRecord(T item) {

    }
}
