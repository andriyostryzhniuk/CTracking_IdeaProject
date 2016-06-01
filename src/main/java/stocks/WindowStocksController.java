package stocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import overridden.elements.number.spinner.NumberSpinner;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import stocks.dto.DTORepository;
import stocks.dto.DTOStocks;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static stocks.ContextMenu.initContextMenu;
import static stocks.ODBC_PubsBD.*;

public class WindowStocksController<T extends DTOStocks> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowStocksController.class);

    public StackPane stackPane;

    public GridPane editingControlsGridPane;
    public TextField nameTextField;
    public Label exceptionLabel;
    private ComboBox categoryComboBox;
    private ComboBox comboBoxListener;
    public TextField priceTextField;
    public ComboBox<DTORepository> repositoryComboBox;
    private NumberSpinner numberSpinner = new NumberSpinner();
    public Button saveButton;
    public Button escapeButton;

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> stockNameCol = new CustomTableColumn<>("Найменування");
    public CustomTableColumn<T, String> categoryCol = new CustomTableColumn<>("Категорія");
    public CustomTableColumn<T, String> typeCol = new CustomTableColumn<>("Тип");
    public CustomTableColumn<T, BigDecimal> priceCol = new CustomTableColumn<>("Вартість");
    public CustomTableColumn<T, String> repositoryCol = new CustomTableColumn<>("Склад");

    ObservableList<T> tableViewDataList = FXCollections.observableArrayList();

    private DTOStocks dtoStocksToUpdate;

    @FXML
    private void initialize(){
        setTableViewParameters();
        initTableView();
        initCategoryComboBox();
        setNameTextFieldListener();
        setPriceTextFieldListener();
        setIntegerListener();
        initNumberSpinner();
        initRepositoryComboBox();
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
        categoryComboBox.setPromptText("Виберіть категорію");

        categoryComboBox.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));

        new AutoCompleteComboBoxListener<>(categoryComboBox, comboBoxListener);

        categoryComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(event -> comboBoxListener.setValue(categoryComboBox.getValue()));
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        categoryComboBox.getEditor().setOnMouseClicked((MouseEvent event) -> {
            if (categoryComboBox.getStyleClass().contains("warning")) {
                categoryComboBox.getStyleClass().remove("warning");
                exceptionLabel.setText("Виберіть категорію");
            }
        });

        categoryComboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            exceptionLabel.setText("");
        });

        categoryComboBox.setOnMouseClicked((MouseEvent event) -> {
            if (categoryComboBox.getStyleClass().contains("warning")) {
                categoryComboBox.getStyleClass().remove("warning");
                exceptionLabel.setText("Виберіть категорію");
            }
        });

        categoryComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            exceptionLabel.setText("");
        });

        categoryComboBox.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            exceptionLabel.setText("");
        });

        editingControlsGridPane.add(categoryComboBox, 1, 0);

    }

    private void initNumberSpinner(){
        numberSpinner.setMinWidth(100);
        numberSpinner.setPrefWidth(100);
        numberSpinner.setMaxWidth(100);
        numberSpinner.setMaxHeight(25);
        numberSpinner.setMinValue(1);
        numberSpinner.setPromptText("Кількість");
        numberSpinner.setValue(1);
        numberSpinner.setTooltip(new Tooltip("Кількість"));
        editingControlsGridPane.add(numberSpinner, 4, 0);
    }

    public void saveButtonAction(ActionEvent actionEvent) {
        String name;
        if (nameTextField.getText() == null || nameTextField.getText().isEmpty()) {
            name = null;
        } else {
            name = nameTextField.getText();
        }

        if (! checkCategoryTextFieldValidation()) {
            return;
        }

        BigDecimal price;
        if (priceTextField.getText() == null || priceTextField.getText().isEmpty()) {
            price = null;
        } else if (! checkPriceTextFieldValidation()) {
            return;
        } else {
            price = new BigDecimal(priceTextField.getText());
        }

        if (repositoryComboBox.getValue() == null) {
            exceptionLabel.setText("Виберіть склад");
            return;
        }

        if (! checkNumberSpinnerValidation()) {
            return;
        }

        if (dtoStocksToUpdate == null) {
            DTOStocks dtoStocksToInsert = new DTOStocks(null, name,
                    selectCategoryId(comboBoxListener.getValue().toString()), price, "Доступно", null,
                    repositoryComboBox.getValue().getId());
            IntStream.range(0, numberSpinner.getValue().intValue()).forEach(i -> insertIntoStock(dtoStocksToInsert));
        } else {
            updateStock(new DTOStocks(dtoStocksToUpdate.getStockId(), name,
                            selectCategoryId(comboBoxListener.getValue().toString()), price, null,
                    repositoryComboBox.getValue().getId()));
        }

        clearEditingControls();
        initTableView();
    }

    private void setNameTextFieldListener(){
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            nameTextField.setText(nameTextField.getText().trim());
        });
    }

    private boolean checkCategoryTextFieldValidation() {
        String editorValue = categoryComboBox.getEditor().getText().toLowerCase();
        for (Object item : categoryComboBox.getItems()) {
            if (editorValue.equals(item.toString().toLowerCase())) {
                categoryComboBox.setValue(item);
                comboBoxListener.setValue(item);
                return true;
            }
        }
        if (!categoryComboBox.getStyleClass().contains("warning")) {
            categoryComboBox.getStyleClass().add("warning");
        }
        return false;
    }

    private void setPriceTextFieldListener() {
        Pattern pattern = Pattern.compile("[^'\'.\\d]");
        priceTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        priceTextField.setTooltip(new Tooltip("Вартість"));
        priceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            priceTextField.setText(priceTextField.getText().trim());
            exceptionLabel.setText("");
            checkPriceTextFieldValidation();
        });

        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Вартість повинна бути числовим значенням, та містити лише цифри");
            } else {
                exceptionLabel.setText("");
            }
        });

        priceTextField.setOnMouseClicked((MouseEvent event) -> {
            if (priceTextField.getStyleClass().contains("warning")) {
                priceTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Вартість повинна бути числовим значенням, та містити лише цифри");
            }
        });
    }

    private boolean checkPriceTextFieldValidation(){
        boolean isRight = true;
        priceTextField.getStyleClass().remove("warning");
        try {
            new BigDecimal(priceTextField.getText());
        } catch (NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (! priceTextField.getText().isEmpty()) {
                isRight = false;
                if (! priceTextField.getStyleClass().contains("warning")) {
                    priceTextField.getStyleClass().add("warning");
                }
            }
        }
        return isRight;
    }

    private void initRepositoryComboBox(){
        repositoryComboBox.setItems(FXCollections.observableArrayList(selectRepositoryList()));

        repositoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            exceptionLabel.setText("");
        });
    }

    private void setIntegerListener(){
        numberSpinner.textProperty().addListener((observable, oldValue, newValue) -> {
            checkNumberSpinnerValidation();
        });
    }

    private boolean checkNumberSpinnerValidation(){
        if (numberSpinner.getText() == null || numberSpinner.getText().isEmpty()) {
            exceptionLabel.setText("Кількість одиниць повинна визначатись цілим числом, не менше 1");
            return false;
        }
        try {
            Integer.parseInt(numberSpinner.getText());
        } catch (NumberFormatException e) {
            exceptionLabel.setText("Кількість одиниць повинна визначатись цілим числом, не менше 1");
            return false;
        }
        exceptionLabel.setText("");
        return true;
    }

    public void escapeButtonAction(ActionEvent actionEvent) {
        clearEditingControls();
    }

    public void editRecord(DTOStocks item) {
        numberSpinner.setDisable(true);
        dtoStocksToUpdate = item;
        nameTextField.setText(item.getName());
        categoryComboBox.setValue(item.getCategoryName());
        comboBoxListener.setValue(item.getCategoryName());
        repositoryComboBox.getItems().forEach(repositoryItem -> {
            if (repositoryItem.getId() == item.getRepositoryId()) {
                repositoryComboBox.setValue(repositoryItem);
            }
        });
        priceTextField.setText(item.getPrice().toString());
    }

    public void removeRecord(DTOStocks item) {
        deleteFromStock(item);
        initTableView();
    }

    private void clearEditingControls(){
        dtoStocksToUpdate = null;
        nameTextField.clear();
        categoryComboBox.setValue(null);
        categoryComboBox.getEditor().clear();
        comboBoxListener.setValue(null);
        categoryComboBox.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));
        categoryComboBox.getStyleClass().remove("warning");
        new AutoCompleteComboBoxListener<>(categoryComboBox, comboBoxListener);
        priceTextField.clear();
        priceTextField.getStyleClass().remove("warning");
        repositoryComboBox.setValue(null);
        numberSpinner.setValue(1);
        numberSpinner.setDisable(false);
    }

}
