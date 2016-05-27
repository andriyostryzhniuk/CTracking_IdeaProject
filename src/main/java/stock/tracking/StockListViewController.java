package stock.tracking;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import stock.tracking.dto.DtoStockListView;
import java.time.LocalDate;

public class StockListViewController {
    public GridPane rootGridPane;
    public ListView<Pane> listView;
    public StackPane headerStackPane;
    public GridPane headerGridPane;
    public Pane headerPane;
    public Label headerLabel;

    private WindowStockTrackingController windowStockTrackingController;

    private String contentType;
    private String stockType;
    private String repository;
    private boolean onlyAvailableStock = true;
    private LocalDate dateView = LocalDate.now();

    public ComboBox comboBoxSearch = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public Button levelUpButton;

    public ObservableList<DtoStockListView> stockListViewDataList = FXCollections.observableArrayList();
    public ObservableList<String> stockNameList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/styles/ListViewStyle.css").toExternalForm());

        comboBoxSearch = initStockComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);

        levelUpButton = initLevelUpButton();
        headerGridPane.add(levelUpButton, 0, 0);
        headerGridPane.setMargin(levelUpButton, new Insets(0, 10, 0, 10));

        initOnSelectionAction();
    }

    public void initListView(boolean isNeedSelectItems) {
        Integer selectedRowIndex = listView.getSelectionModel().getSelectedIndex();
        stockNameList.clear();
        stockListViewDataList.clear();
        listView.getItems().clear();
        windowStockTrackingController.getOnlyAvailableStockCheckBox().setDisable(getCheckBoxDisableStatus());

        if (contentType.equals("Категорії")) {
            levelUpButton.setDisable(true);
            if (repository.equals("Всі склади")) {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.selectStockCategory(
                        stockType, onlyAvailableStock, dateView));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectStockCategoryInRepository(stockType, repository, onlyAvailableStock, dateView));
            }

            stockListViewDataList.forEach(item -> {
                item.initStockPaneContainer();

                setSourceDragAndDrop(item.getPaneContainer());
                item.getPaneContainer().setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            setContentType(item.getName());
                            initListView(false);
                        }
                    }
                });
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        } else if (contentType.equals("Весь інвентар")) {
            levelUpButton.setDisable(false);
            if (repository.equals("Всі склади")) {
                stockListViewDataList.addAll(
                        ODBC_PubsBDForStock.selectAllStockOfType(stockType, dateView, onlyAvailableStock));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectAllStockOfTypeInRepository(stockType, repository, dateView, onlyAvailableStock));
            }
            stockListViewDataList.forEach(item -> {
                item.initStockPaneContainer();
                setSourceDragAndDrop(item.getPaneContainer());
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        } else {
            levelUpButton.setDisable(false);
            if (repository.equals("Всі склади")) {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectStockOfCategory(contentType, stockType, dateView, onlyAvailableStock));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryInRepository(
                        contentType, repository, stockType, dateView, onlyAvailableStock));
            }
            stockListViewDataList.forEach(item -> {
                item.initStockPaneContainer();
                setSourceDragAndDrop(item.getPaneContainer());
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        }

        if (isNeedSelectItems) {
            listView.getSelectionModel().select(selectedRowIndex);
            listView.getFocusModel().focus(selectedRowIndex);
            listView.scrollTo(selectedRowIndex);
        }

        comboBoxSearch.setItems(stockNameList);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private boolean getCheckBoxDisableStatus(){
        return stockType.equals("Розхідні") ? true : false;
    }

    private void initOnSelectionAction(){
        listView.getSelectionModel().selectedItemProperty().addListener(event -> {
            Pane selectedItem;
            if (! contentType.equals("Категорії") &&
                    (selectedItem = listView.getSelectionModel().getSelectedItem()) != null) {
                Integer stockId = Integer.parseInt(selectedItem.getId());
                windowStockTrackingController.clearStockAboutInfo();
                windowStockTrackingController.initStockAboutInfo(stockId);
            } else {
                windowStockTrackingController.clearStockAboutInfo();
            }
        });
    }

    private void setSourceDragAndDrop(Pane pane) {
        pane.setOnDragDetected(event -> {
//                System.out.println("source.setOnDragDetected");
    /* drag was detected, start a drag-and-drop gesture*/
    /* allow any transfer mode */
            Dragboard db = pane.startDragAndDrop(TransferMode.ANY);

    /* Put a string on a dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(pane.getId());
            db.setContent(content);

            event.consume();
        });

        pane.setOnDragDone(event -> {
//                System.out.println("source.setOnDragDone");
    /* the drag and drop gesture ended */
    /* if the data was successfully moved, clear it */
//                if (event.getTransferMode() == TransferMode.MOVE) {
//                    System.out.println(resultList);
//                }
            event.consume();
        });
    }

    public ComboBox initStockComboBoxSearch() {
        ComboBox comboBox = new ComboBox();

        comboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBox.setTooltip(new Tooltip("Пошук"));
        comboBox.setPromptText("Пошук");

        comboBox.setItems(stockNameList);

        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(event -> {
//                                mouse pressed
                            comboBoxListener.setValue(comboBox.getValue());
                        });
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

        comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                if (newValue != null) {
                    comboBox.getStyleClass().remove("warning");
                    searchInListView();
                    comboBoxListener.setValue(null);
                }
            }
        });
        return comboBox;
    }

    public void searchInListView() {
        Object comboBoxListenerValue = comboBoxListener.getValue();
        Integer stockID = null;
        for (DtoStockListView item : stockListViewDataList) {
            if (item.getName().equals(comboBoxListenerValue)) {
                stockID = item.getId();
            }
        }
        if (stockID != null) {
            int i = 0;
            for (Pane pane : listView.getItems()) {
                if (Integer.parseInt(pane.getId()) == stockID) {
                    listView.getSelectionModel().select(i);
                    listView.getFocusModel().focus(i);
                    listView.scrollTo(i);
                }
                i++;
            }
        }
    }

    public Button initLevelUpButton() {
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/icons/level_up_icon.png"));
        button.getStylesheets().add(getClass().getResource("/stock.tracking/LevelUpButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setPrefWidth(10);
        button.setMaxWidth(10);
        button.setOnAction((ActionEvent event) -> {
            if (! contentType.equals("Категорії")) {
                setContentType("Категорії");
                windowStockTrackingController.getContentChoiceBox().setValue("Категорії");
                initListView(false);
            }
        });
        button.setTooltip(new Tooltip("Повернутись до категорій"));
        return button;
    }

    public void setTextToHeaderLabel(String text) {
        headerLabel.setVisible(false);
        headerLabel.setText(text);
        if (headerLabel.prefWidth(-1) <= headerPane.getWidth() - 40 || headerLabel.prefWidth(-1) == 0) {
            headerLabel.setStyle("-fx-translate-x: -21;");
        } else if (headerLabel.prefWidth(-1) > headerPane.getWidth() - 40 && headerLabel.prefWidth(-1) < headerPane.getWidth() - 5) {
            Double translateValue = (headerPane.getWidth() - headerLabel.prefWidth(-1))*0.457142857*-1;
            headerLabel.setStyle("-fx-translate-x: "+ translateValue.toString() +";");
        } else {
            headerLabel.setStyle("-fx-translate-x: 0;");
        }

        headerLabel.setVisible(true);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        setTextToHeaderLabel(contentType);
    }

    public void setWindowStockTrackingController(WindowStockTrackingController windowStockTrackingController) {
        this.windowStockTrackingController = windowStockTrackingController;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setOnlyAvailableStock(boolean onlyAvailableStock) {
        this.onlyAvailableStock = onlyAvailableStock;
    }

    public void setDateView(LocalDate dateView) {
        this.dateView = dateView;
    }
}
