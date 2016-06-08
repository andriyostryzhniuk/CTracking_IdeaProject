package stocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.BigDecimalStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import overridden.elements.number.spinner.NumberSpinner;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import stock.tracking.dto.DTOStockTracking;
import stocks.dto.DTORepository;
import stocks.dto.DTOStocks;
import subsidiary.classes.EditPanel;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static stock.tracking.ODBC_PubsBDForLiable.selectEmployeesShortName;
import static stock.tracking.ODBC_PubsBDForLiable.selectIfStockIsDisable;
import static stock.tracking.ODBC_PubsBDForLiable.selectObjectAddress;
import static stocks.ContextMenu.initContextMenu;
import static stocks.ODBC_PubsBD.*;

public class WindowStocksController<T extends DTOStocks> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowStocksController.class);

    public StackPane stackPane;
    public GridPane controlsGridPane;

    public GridPane editingControlsGridPane;
    public TextField nameTextField;
    public Label exceptionLabel;
    public GridPane parentGridPane;
    private ComboBox categoryComboBox;
    private ComboBox comboBoxListener;
    public TextField priceTextField;
    public ComboBox<DTORepository> repositoryComboBox;
    private NumberSpinner numberSpinner = new NumberSpinner();
    public TextArea editingNotesTextArea;
    public Button saveButton;
    public Button escapeButton;

    public GridPane filtersGridPane;
    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxSearchListener = new ComboBox();
    private ComboBox comboBoxCategoryFilter = new ComboBox();
    private ComboBox comboBoxFilterListener = new ComboBox();
    public ChoiceBox<DTORepository> repositoryFilterChoiceBox;
    public ChoiceBox statusFilterChoiceBox;
    public TextArea notesTextArea;
    private String category;
    private Integer repositoryId;
    private String status;

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
        initEditingControls();
        initFiltersControls();
        initTableView();
    }

    public void initTableView() {
        tableViewDataList.clear();
        comboBoxSearch.getItems().clear();

        if (category.equals("Всі категорії")) {
            if (repositoryId == null) {
                tableViewDataList.addAll((Collection<? extends T>)
                        FXCollections.observableArrayList(selectStockList(status)));
            } else {
                tableViewDataList.addAll((Collection<? extends T>)
                        FXCollections.observableArrayList(selectStockOnRepositoryList(status, repositoryId)));
            }
        } else {
            if (repositoryId == null) {
                tableViewDataList.addAll((Collection<? extends T>) FXCollections.observableArrayList(
                        selectStockWithCategoryList(status, selectCategoryId(category))));
            } else {
                tableViewDataList.addAll((Collection<? extends T>) FXCollections.observableArrayList(
                        selectStockWithCategoryOnRepositoryList(status, selectCategoryId(category), repositoryId)));
            }
        }

        tableViewDataList.forEach(item -> comboBoxSearch.getItems().add(item.getName()));
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxSearchListener);
    }

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        tableView.getTableView().getColumns().addAll(stockNameCol, categoryCol, typeCol, priceCol, repositoryCol);
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        initContextMenu(tableView.getTableView(), this, statusFilterChoiceBox);
        tableView.getTableView().setPlaceholder(new Label("Покищо тут немає жодного інвентаря"));
        tableView.getTableView().setItems(tableViewDataList);

        tableView.getTableView().getSelectionModel().selectedItemProperty().addListener(event -> {
            T selectedItem;
            if ((selectedItem = tableView.getTableView().getSelectionModel().getSelectedItem()) != null) {
                notesTextArea.setText(selectedItem.getNotes());
            } else {
                notesTextArea.clear();
            }
        });
    }

    private void fillCols() {
        stockNameCol.setCellValueFactory(new PropertyValueFactory("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory("categoryName"));
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));
        repositoryCol.setCellValueFactory(new PropertyValueFactory("repositoryName"));

        stockNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        priceCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        repositoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void setColsDateProperties() {
        stockNameCol.setPercentWidth(200); stockNameCol.setMinWidth(200); stockNameCol.setSortable(false);
        categoryCol.setPercentWidth(200); categoryCol.setMinWidth(200); categoryCol.setSortable(false);
        typeCol.setPercentWidth(100); typeCol.setMinWidth(100); typeCol.setSortable(false);
        priceCol.setPercentWidth(100); priceCol.setMinWidth(100); priceCol.setSortable(false);
        repositoryCol.setPercentWidth(150); repositoryCol.setMinWidth(150); repositoryCol.setSortable(false);
    }

    private void initEditingControls(){
        parentGridPane.getChildren().remove(controlsGridPane);
        initCategoryComboBox();
        initSettingsCategoryButton();
        setNameTextFieldListener();
        setPriceTextFieldListener();
        setIntegerListener();
        initSettingsRepositoryButton();
        initNumberSpinner();
        initRepositoryComboBox();
        setNotesTextAreaListener();
    }

    private void initFiltersControls(){
        initAddButton();
        initSeeStateButton();
        initEditButton();
        initDeleteButton();
        initComboBoxSearch();
        initComboBoxCategoryFilter();
        initRepositoryFilterChoiceBox();
        initStatusFilterChoiceBox();
    }

    public void initCategoryComboBox() {
        categoryComboBox = new ComboBox();
        comboBoxListener = new ComboBox();

        categoryComboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        categoryComboBox.setTooltip(new Tooltip("Виберіть категорію інентаря"));
        categoryComboBox.setPromptText("Виберіть категорію");
        categoryComboBox.setMaxWidth(200);
        categoryComboBox.setMaxWidth(200);

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

        editingControlsGridPane.add(categoryComboBox, 2, 0);

    }

    private void initNumberSpinner(){
        numberSpinner.setMinWidth(90);
        numberSpinner.setPrefWidth(90);
        numberSpinner.setMaxWidth(90);
        numberSpinner.setMaxHeight(25);
        numberSpinner.setMinValue(1);
        numberSpinner.setPromptText("Кількість");
        numberSpinner.setValue(1);
        numberSpinner.setTooltip(new Tooltip("Кількість"));
        editingControlsGridPane.add(numberSpinner, 6, 0);
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

        String notes;
        if (editingNotesTextArea.getText() == null || editingNotesTextArea.getText().isEmpty()) {
            notes = null;
        } else {
            notes = editingNotesTextArea.getText();
        }

        if (dtoStocksToUpdate == null) {
            DTOStocks dtoStocksToInsert = new DTOStocks(null, name,
                    selectCategoryId(comboBoxListener.getValue().toString()), price, "доступно", notes,
                    repositoryComboBox.getValue().getId());
            IntStream.range(0, numberSpinner.getValue().intValue()).forEach(i -> insertIntoStock(dtoStocksToInsert));
        } else {
            updateStock(new DTOStocks(dtoStocksToUpdate.getStockId(), name,
                            selectCategoryId(comboBoxListener.getValue().toString()), price, notes,
                    repositoryComboBox.getValue().getId()));
        }

        clearEditingControls();
        initTableView();
    }

    private void setNameTextFieldListener(){
        nameTextField.setTooltip(new Tooltip("Введіть найменування інвентаря"));

        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (nameTextField.getText() != null && ! nameTextField.getText().isEmpty()) {
                nameTextField.setText(nameTextField.getText().trim());
                nameTextField.setText(
                        nameTextField.getText().substring(0, 1).toUpperCase() + nameTextField.getText().substring(1));
            }
        });

        nameTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            if (nameTextField.getText().length() > 45) {
                String text = nameTextField.getText().substring(0, 45);
                nameTextField.setText(text);
            }
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
        repositoryComboBox.setTooltip(new Tooltip("Виберіть склад"));
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

    private void initComboBoxSearch() {
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");
        comboBoxSearch.setMinWidth(300);

        comboBoxSearch.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    super.setOnMousePressed((MouseEvent event) -> {
                        comboBoxSearchListener.setValue(comboBoxSearch.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        comboBoxSearchListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//                change detected
            if (newValue != null) {
                comboBoxSearch.getStyleClass().remove("warning");
                searchInTableView();
                comboBoxSearchListener.setValue(null);
            }
        });

        filtersGridPane.add(comboBoxSearch, 5, 0);
    }

    private void searchInTableView() {
        Object comboBoxListenerValue = comboBoxSearchListener.getValue();

        int i = 0;
        for (DTOStocks item : tableView.getTableView().getItems()) {
            if (item.getName().equals(comboBoxListenerValue)) {
                tableView.getTableView().getSelectionModel().select(i);
                tableView.getTableView().getFocusModel().focus(i);
                tableView.getTableView().scrollTo(i);
                break;
            }
            i++;
        }
    }

    private void initComboBoxCategoryFilter() {
        comboBoxCategoryFilter.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        comboBoxCategoryFilter.setTooltip(new Tooltip("Пошук за категорією"));
        comboBoxCategoryFilter.setPromptText("Виберіть категорію");
        comboBoxCategoryFilter.setMinWidth(250);

        comboBoxCategoryFilter.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));
        comboBoxCategoryFilter.getItems().add(0, "Всі категорії");

        new AutoCompleteComboBoxListener<>(comboBoxCategoryFilter, comboBoxFilterListener);

        comboBoxCategoryFilter.setValue(comboBoxCategoryFilter.getItems().get(0));
        comboBoxFilterListener.setValue(comboBoxCategoryFilter.getValue());
        category = comboBoxFilterListener.getValue().toString();

        comboBoxCategoryFilter.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    super.setOnMousePressed((MouseEvent event) -> {
                        comboBoxFilterListener.setValue(comboBoxCategoryFilter.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        comboBoxFilterListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//                change detected
            if (newValue != null) {
                comboBoxCategoryFilter.getStyleClass().remove("warning");
                category = comboBoxFilterListener.getValue().toString();
                initTableView();
                comboBoxFilterListener.setValue(null);
            }
        });

        filtersGridPane.add(comboBoxCategoryFilter, 6, 0);
    }

    private void initRepositoryFilterChoiceBox(){
        repositoryFilterChoiceBox.setTooltip(new Tooltip("Виберіть склад для перегляду"));
        repositoryFilterChoiceBox.getItems().add(0, new DTORepository(null, "Всі склади"));
        repositoryFilterChoiceBox.getItems().addAll(selectRepositoryList());
        repositoryFilterChoiceBox.setValue(repositoryFilterChoiceBox.getItems().get(0));
        repositoryId = repositoryFilterChoiceBox.getValue().getId();

        repositoryFilterChoiceBox.setOnAction(event -> {
            if (! repositoryFilterChoiceBox.getItems().isEmpty()) {
                repositoryId = repositoryFilterChoiceBox.getValue().getId();
                initTableView();
            }
        });
    }

    private void initStatusFilterChoiceBox(){
        statusFilterChoiceBox.setTooltip(new Tooltip("Виберіть статус інвентаря"));
        statusFilterChoiceBox.getItems().addAll("Доступно", "В ремонті", "Списано");
        statusFilterChoiceBox.setValue(statusFilterChoiceBox.getItems().get(0));
        status = statusFilterChoiceBox.getValue().toString().toLowerCase();

        statusFilterChoiceBox.setOnAction(event -> {
            status = statusFilterChoiceBox.getValue().toString().toLowerCase();
            initTableView();
        });
    }

    private void initAddButton(){
        final EditPanel editPanel = new EditPanel();
        Button addButton = editPanel.getAddButton();
        addButton.setTooltip(new Tooltip("Додати інвентар"));
        filtersGridPane.add(addButton, 0, 0);

        addButton.setOnAction(event -> {
            if (! parentGridPane.getChildren().contains(controlsGridPane)) {
                parentGridPane.add(controlsGridPane, 0, 0);
            } else {
                clearEditingControls();
            }
        });
    }

    private void initSeeStateButton(){
        final EditPanel editPanel = new EditPanel(tableView.getTableView());
        Button seeStateButton = editPanel.getEyeButton();
        seeStateButton.setTooltip(new Tooltip("Переглянути місце знаходження"));
        filtersGridPane.add(seeStateButton, 1, 0);

        seeStateButton.setOnAction(event -> {
            T stockItem = tableView.getTableView().getSelectionModel().getSelectedItem();
            showStateInformation(stockItem);
        });
    }

    private void initEditButton(){
        final EditPanel editPanel = new EditPanel(tableView.getTableView());
        Button editButton = editPanel.getEditButton();
        filtersGridPane.add(editButton, 2, 0);

        editButton.setOnAction(event -> {
            T stockItem = tableView.getTableView().getSelectionModel().getSelectedItem();
            editRecord(stockItem);
        });
    }

    private void initDeleteButton(){
        final EditPanel editPanel = new EditPanel(tableView.getTableView());
        Button deleteButton = editPanel.getDeleteButton();
        filtersGridPane.add(deleteButton, 3, 0);

        deleteButton.setOnAction(event -> {
            T stockItem = tableView.getTableView().getSelectionModel().getSelectedItem();
            removeRecord(stockItem);
        });
    }

    private void setNotesTextAreaListener(){
        editingNotesTextArea.setTooltip(new Tooltip("Тут Ви можете написати будь-які нотатки"));
        editingNotesTextArea.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                if (editingNotesTextArea.getText().length() > 400) {
                    String text = editingNotesTextArea.getText().substring(0, 400);
                    editingNotesTextArea.setText(text);
                }
            }
        });
    }

    public void showStateInformation(DTOStocks item){
        DTOStockTracking dtoStockTracking;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if ((dtoStockTracking = selectIfStockIsDisable(item.getStockId(), LocalDate.now())) != null) {
            String objectsName = "";
            if (dtoStockTracking.getObjectId() != null) {
                objectsName = "на об'єкті " + selectObjectAddress(dtoStockTracking.getObjectId()) + " ";
            }
            String employeesName = "";
            if (dtoStockTracking.getEmployeesId() != null) {
                employeesName = "за працівником " + selectEmployeesShortName(dtoStockTracking.getEmployeesId());
            }
            alert.setContentText("На даний момент " + item.getName() + " закріплено\n" + objectsName + employeesName);
        } else {
            alert.setContentText("На даний момент " + item.getName() + "\nне закріплено за жодним об'єктом");
        }

        alert.setTitle("Повідомлення");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void escapeButtonAction(ActionEvent actionEvent) {
        clearEditingControls();
        parentGridPane.getChildren().remove(controlsGridPane);
    }

    public void editRecord(DTOStocks item) {
        clearEditingControls();
        if (! parentGridPane.getChildren().contains(controlsGridPane)) {
            parentGridPane.add(controlsGridPane, 0, 0);
        }
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
        if (item.getPrice() != null) {
            priceTextField.setText(item.getPrice().toString());
        }
        editingNotesTextArea.setText(item.getNotes());
    }

    public void removeRecord(DTOStocks item) {
        if (dtoStocksToUpdate != null && dtoStocksToUpdate == item) {
            clearEditingControls();
        }
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
        editingNotesTextArea.clear();
    }

    private void initSettingsCategoryButton(){
        EditPanel editPanel = new EditPanel();
        Button settingsCategoryButton = editPanel.getSettingsButton();
        settingsCategoryButton.getStylesheets().add(getClass().getResource("/stocks/SettingsButtonStyle.css").toExternalForm());
        settingsCategoryButton.setTooltip(new Tooltip("Налаштування категорій"));
        editingControlsGridPane.add(settingsCategoryButton, 3, 0);

        settingsCategoryButton.setOnAction(event -> showSettingsCategoryWindow());
    }

    private void showSettingsCategoryWindow() {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stocks/SettingsStockCategory.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsStockCategoryController settingsStockCategoryController = fxmlLoader.getController();
        settingsStockCategoryController.setWindowStocksController(this);
        settingsStockCategoryController.initTableView();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 460, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getTableView().getScene().getWindow());
        primaryStage.showAndWait();
    }

    private void initSettingsRepositoryButton(){
        EditPanel editPanel = new EditPanel();
        Button settingsRepositoryButton = editPanel.getSettingsButton();
        settingsRepositoryButton.setTooltip(new Tooltip("Налаштування складів"));
        settingsRepositoryButton.getStylesheets().add(getClass().getResource("/stocks/SettingsButtonStyle.css").toExternalForm());
        editingControlsGridPane.add(settingsRepositoryButton, 5, 0);

        settingsRepositoryButton.setOnAction(event -> showSettingsRepositoryWindow());
    }

    private void showSettingsRepositoryWindow() {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stocks/SettingsRepository.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsRepositoryController settingsRepositoryController = fxmlLoader.getController();
        settingsRepositoryController.setWindowStocksController(this);
        settingsRepositoryController.initTableView();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 460, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getTableView().getScene().getWindow());
        primaryStage.showAndWait();
    }

    public void refreshCategory(){
        categoryComboBox.getItems().clear();
        categoryComboBox.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));
        new AutoCompleteComboBoxListener<>(categoryComboBox, comboBoxListener);

        comboBoxCategoryFilter.getItems().clear();
        comboBoxCategoryFilter.setItems(FXCollections.observableArrayList(selectStockCategoryNameList()));
        comboBoxCategoryFilter.getItems().add(0, "Всі категорії");
        comboBoxCategoryFilter.setValue(comboBoxCategoryFilter.getItems().get(0));
        category = comboBoxCategoryFilter.getValue().toString();
        new AutoCompleteComboBoxListener<>(comboBoxCategoryFilter, comboBoxFilterListener);
    }

    public void refreshRepository(){
        repositoryComboBox.getItems().clear();
        repositoryComboBox.setItems(FXCollections.observableArrayList(selectRepositoryList()));

        repositoryFilterChoiceBox.getItems().clear();
        repositoryFilterChoiceBox.getItems().add(0, new DTORepository(null, "Всі склади"));
        repositoryFilterChoiceBox.getItems().addAll(selectRepositoryList());
        repositoryFilterChoiceBox.setValue(repositoryFilterChoiceBox.getItems().get(0));
    }

}
