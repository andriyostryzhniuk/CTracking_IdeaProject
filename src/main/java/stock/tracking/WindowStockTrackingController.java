package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import stock.tracking.dto.DTOStockTracking;
import stock.tracking.dto.DtoStock;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import static stock.tracking.ContextMenu.initContextMenu;
import static stock.tracking.ODBC_PubsBDForLiable.*;
import static stock.tracking.ODBC_PubsBDForStock.selectStock;

public class WindowStockTrackingController<T extends DTOStockTracking> {

    public GridPane gridPane;
    public StackPane stackPane;

    public DatePicker datePicker;
    public Button todayButton;
    private LocalDate oldDatePickerValue;
    public BorderPane rootBorderPane;
    public ChoiceBox contentChoiceBox;
    public ChoiceBox stockTypeChoiceBox;
    public ChoiceBox repositoryChoiceBox;
    public CheckBox onlyAvailableStockCheckBox;
    public ChoiceBox liableTypeChoiceBox;

    public TextArea stockNameTextArea;
    public Label stockCategoryLabel;
    public TextArea stockCategoryTextArea;
    public Label repositoryNameLabel;
    public TextArea repositoryNameTextArea;
    public Label priceLabel;
    public Label priceValueLabel;
    public Label notesLabel;
    public TextArea notesTextArea;

    public StockListViewController stockListViewController;
    public LiableListViewController liableListViewController;
    private boolean successSave;

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
        initDatePicker();
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

        repositoryChoiceBox.setItems(FXCollections.observableArrayList(ODBC_PubsBDForStock.selectRepositoryNames()));
        repositoryChoiceBox.setValue(repositoryChoiceBox.getItems().get(0));
        stockListViewController.setRepository(repositoryChoiceBox.getValue().toString());

        repositoryChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            stockListViewController.setRepository(newValue.toString());
            stockListViewController.initListView(false);
        });
    }

    public void initOnlyAvailableStockCheckBox(){
        onlyAvailableStockCheckBox.setTooltip(new Tooltip("Показувати тільки вільний на даний момент інвентар"));
        onlyAvailableStockCheckBox.setSelected(true);
        onlyAvailableStockCheckBox.selectedProperty().addListener(observable -> {
            stockListViewController.setOnlyAvailableStock(onlyAvailableStockCheckBox.isSelected());
            stockListViewController.initListView(false);
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

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        tableView.getTableView().getColumns().addAll(stockNameCol, givingDateCol, returnDateCol);
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("Не закріплено жодного інвентаря"));

        tableView.getTableView().getSelectionModel().selectedItemProperty().addListener(event -> {
            T selectedItem;
            if ((selectedItem = tableView.getTableView().getSelectionModel().getSelectedItem()) != null) {
                Integer stockId = selectedItem.getStockId();
                clearStockAboutInfo();
                initStockAboutInfo(stockId);
            } else {
                clearStockAboutInfo();
            }
        });
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

    public void initTableView(Integer objectId, Integer employeeId){
        clearTableView();
        if (objectId != null) {
            objectNameLabel.setText(selectObjectAddress(objectId));
        }
        if (employeeId != null) {
            employeesNameLabel.setText(selectEmployeesFullName(employeeId));
        }
        tableViewDataList.addAll((Collection<? extends T>)
                FXCollections.observableArrayList(selectStockTracking(objectId, employeeId, datePicker.getValue())));

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

    public boolean editRecord(T item, boolean toUpdate) {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/EditingPromptWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingPromptWindowController editingPromptWindowController = fxmlLoader.getController();
        editingPromptWindowController.setToUpdate(toUpdate);
        editingPromptWindowController.setDtoStockTracking(item);
        editingPromptWindowController.setWindowStockTrackingController(this);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 500, 235, Color.rgb(0, 0, 0, 0)));
        editingPromptWindowController.initShortcuts();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(rootBorderPane.getScene().getWindow());
        primaryStage.showAndWait();

        return successSave;
    }

    private void initDatePicker(){
        datePicker.setTooltip(new Tooltip("Дата перегляду"));
        datePicker.setValue(LocalDate.now());
        oldDatePickerValue = datePicker.getValue();
        initTodayButton();

        datePicker.valueProperty().addListener(observable -> {
            LocalDate newValue = datePicker.getValue();
            if (oldDatePickerValue.compareTo(newValue) != 0) {
                oldDatePickerValue = newValue;
                stockListViewController.setDateView(newValue);
                stockListViewController.initListView(false);
                liableListViewController.setDateView(newValue);
                liableListViewController.initListView(false);
            }
        });
    }

    private void initTodayButton() {
        Image image = new Image(getClass().getResourceAsStream("/icons/today_icon.png"));
        todayButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        todayButton.setGraphic(new ImageView(image));
        todayButton.setTooltip(new Tooltip("Сьогоднішня дата"));
        todayButton.setOnAction(event -> datePicker.setValue(LocalDate.now()));
    }

    public void initStockAboutInfo(Integer stockId){
        DtoStock dtoStock = selectStock(stockId);

        stockNameTextArea.setText(dtoStock.getName());
        stockCategoryLabel.setVisible(true);
        stockCategoryTextArea.setText(dtoStock.getStockCategory());
        if (dtoStock.getRepositoryName() != null) {
            repositoryNameLabel.setVisible(true);
            repositoryNameTextArea.setText(dtoStock.getRepositoryName());
        }
        if (dtoStock.getPrice() != null) {
            priceLabel.setVisible(true);
            priceValueLabel.setText(dtoStock.getPrice().toString());
            priceValueLabel.setVisible(true);
        }
        if (dtoStock.getNotes() != null) {
            notesLabel.setVisible(true);
            notesTextArea.setText(dtoStock.getNotes());
        }
    }

    public void clearStockAboutInfo(){
        stockNameTextArea.clear();
        stockCategoryLabel.setVisible(false);
        stockCategoryTextArea.clear();
        repositoryNameLabel.setVisible(false);
        repositoryNameTextArea.clear();
        priceLabel.setVisible(false);
        priceValueLabel.setText(null);
        notesLabel.setVisible(false);
        notesTextArea.clear();
    }

    public ChoiceBox getContentChoiceBox() {
        return contentChoiceBox;
    }

    public CheckBox getOnlyAvailableStockCheckBox() {
        return onlyAvailableStockCheckBox;
    }

    public ChoiceBox getLiableTypeChoiceBox() {
        return liableTypeChoiceBox;
    }

    public void setSuccessSave(boolean successSave) {
        this.successSave = successSave;
    }

    public StockListViewController getStockListViewController() {
        return stockListViewController;
    }
}
