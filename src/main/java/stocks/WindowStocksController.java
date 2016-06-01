package stocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import stocks.dto.DTORepository;
import stocks.dto.DTOStocks;
import java.math.BigDecimal;
import java.util.Collection;
import static stocks.ContextMenu.initContextMenu;
import static stocks.ODBC_PubsBD.selectRepositoryList;
import static stocks.ODBC_PubsBD.selectStockCategoryNameList;
import static stocks.ODBC_PubsBD.selectStockList;

public class WindowStocksController<T extends DTOStocks> {

    public StackPane stackPane;

    public GridPane controlsGridPane;
    public TextField nameTextField;
    private ComboBox categoryComboBox;
    private ComboBox comboBoxListener;
    public TextField priceTextField;
    public Button saveButton;
    public Button escapeButton;
    public ComboBox<DTORepository> repositoryComboBox;

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> stockNameCol = new CustomTableColumn<>("Найменування");
    public CustomTableColumn<T, String> categoryCol = new CustomTableColumn<>("Категорія");
    public CustomTableColumn<T, String> typeCol = new CustomTableColumn<>("Тип");
    public CustomTableColumn<T, BigDecimal> priceCol = new CustomTableColumn<>("Вартість");
    public CustomTableColumn<T, String> repositoryCol = new CustomTableColumn<>("Склад");

    ObservableList<T> tableViewDataList = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        setTableViewParameters();
        initTableView();
        initCategoryComboBox();
        repositoryComboBox.setItems(FXCollections.observableArrayList(selectRepositoryList()));
    }

    public void initTableView() {
        tableViewDataList.clear();
        tableViewDataList.addAll((Collection<? extends T>) FXCollections.observableArrayList(selectStockList("доступно")));

        tableView.getTableView().setItems(tableViewDataList);
    }

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        tableView.getTableView().getColumns().addAll(stockNameCol, categoryCol, typeCol, priceCol, repositoryCol);
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("Покищо тут немає жодного інвентаря"));
        tableView.getTableView().setItems(tableViewDataList);

//        tableView.getTableView().getSelectionModel().selectedItemProperty().addListener(event -> {
//            T selectedItem;
//            if ((selectedItem = tableView.getTableView().getSelectionModel().getSelectedItem()) != null) {
//                Integer stockId = selectedItem.getStockId();
//                clearStockAboutInfo();
//                initStockAboutInfo(stockId);
//            } else {
//                clearStockAboutInfo();
//            }
//        });
    }

    private void fillCols() {
        stockNameCol.setCellValueFactory(new PropertyValueFactory("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory("categoryName"));
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));
        repositoryCol.setCellValueFactory(new PropertyValueFactory("repositoryName"));
    }

    private void setColsDateProperties() {
        stockNameCol.setPercentWidth(200); stockNameCol.setMinWidth(200);
        categoryCol.setPercentWidth(200); categoryCol.setMinWidth(200);
        typeCol.setPercentWidth(100); typeCol.setMinWidth(100);
        priceCol.setPercentWidth(100); priceCol.setMinWidth(100);
        repositoryCol.setPercentWidth(150); repositoryCol.setMinWidth(150);
    }

    public void initCategoryComboBox() {
        categoryComboBox = new ComboBox();
        comboBoxListener = new ComboBox();

        categoryComboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        categoryComboBox.setTooltip(new Tooltip("Виберіть категорію інентаря"));

        categoryComboBox.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));

        new AutoCompleteComboBoxListener<>(categoryComboBox, comboBoxListener);

        controlsGridPane.add(categoryComboBox, 1, 0);

//        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell<String> call(ListView<String> param) {
//                final ListCell<String> cell = new ListCell<String>() {
//                    {
//                        super.setOnMousePressed(event -> {
////                                mouse pressed
//                            comboBoxListener.setValue(comboBox.getValue());
//                        });
//                    }
//
//                    @Override
//                    public void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setText(item);
//                    }
//                };
//                return cell;
//            }
//        });
//
//        comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
////                change detected
//                if (newValue != null) {
//                    comboBox.getStyleClass().remove("warning");
//                    searchInListView();
//                    comboBoxListener.setValue(null);
//                }
//            }
//        });
    }

    public void saveButtonAction(ActionEvent actionEvent) {

    }

    public void escapeButtonAction(ActionEvent actionEvent) {

    }

    public void editRecord(DTOStocks item) {

    }

    public void removeRecord(DTOStocks item) {

    }
}
