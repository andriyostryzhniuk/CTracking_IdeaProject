package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import stock.tracking.dto.DtoStock;
import stock.tracking.dto.DtoStockCategory;
import java.util.Map;

public class StockListViewController extends ListView {
    public GridPane rootGridPane;
    public ListView<Pane> stockListView;

    public ChoiceBox stockTypeChoiceBox = new ChoiceBox();
    public ComboBox stockCategoryComboBox = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public ChoiceBox repositoryChoiceBox = new ChoiceBox();
    public CheckBox showDisableStockCheckBox = new CheckBox();
    public Button goBackButton = new Button();

    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    public ObservableList<String> stockCategoryNameList = FXCollections.observableArrayList();
    public ObservableList<DtoStockCategory> stockCategoryDataList = FXCollections.observableArrayList();

    private ObservableMap<Integer, Integer> resultMap = FXCollections.observableHashMap();

    @FXML
    public void initialize() {
        stockListView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());
    }

    public void initStockListView(String stockType, String stockCategory) {
        stockDataList.clear();
        stockCategoryDataList.clear();
        stockListView.getItems().clear();

        stockCategoryComboBox.setValue(comboBoxListener.getValue());
        stockCategoryComboBox.getStyleClass().remove("warning");

        if (stockCategory.equals("Всі категорії")) {
            goBackButton.setDisable(true);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockCategoryDataList.addAll(ODBC_PubsBDForStock.selectStockCategory(stockType, showDisableStockCheckBox.isSelected()));
            } else {
                stockCategoryDataList.addAll(ODBC_PubsBDForStock.
                        selectStockCategoryInRepository(stockType, repositoryChoiceBox.getValue().toString(),
                                showDisableStockCheckBox.isSelected()));
            }
//            count of stock available in each category
            stockCategoryDataList.forEach(item -> {
                int numberOfStockGranted = countStockOfCategory(item.getId());
                item.initPaneContainer(numberOfStockGranted);
            });
            stockCategoryDataList.forEach(item -> {
                setSourceDragAndDrop(item.getPaneContainer());
                item.getPaneContainer().setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                stockCategoryComboBox.setValue(item.getName());
                                comboBoxListener.setValue(stockCategoryComboBox.getValue());
                            }
                        }
                    }
                });
                stockListView.getItems().add(item.getPaneContainer());
            });
        } else if (stockCategory.equals("Весь інвентар")) {
            goBackButton.setDisable(false);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockDataList.addAll(ODBC_PubsBDForStock.selectAllStockOfType(stockType));
            } else {
                stockDataList.addAll(ODBC_PubsBDForStock.
                        selectAllStockOfTypeInRepository(stockType, repositoryChoiceBox.getValue().toString()));
            }
            stockDataList.forEach(item -> item.initPaneContainer());
            stockDataList.forEach(item -> {
                setSourceDragAndDrop(item.getPaneContainer());
                checkDisable(item.getPaneContainer());
                stockListView.getItems().add(item.getPaneContainer());
            });
        } else {
            goBackButton.setDisable(false);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategory(stockCategory));
            } else {
                stockDataList.addAll(ODBC_PubsBDForStock.
                        selectStockOfCategoryInRepository(stockCategory, repositoryChoiceBox.getValue().toString()));
            }
            stockDataList.forEach(item -> item.initPaneContainer());
            stockDataList.forEach(item -> {
                setSourceDragAndDrop(item.getPaneContainer());
                checkDisable(item.getPaneContainer());
                stockListView.getItems().add(item.getPaneContainer());
            });
        }
    }

    public void setSourceDragAndDrop(Pane pane) {
        pane.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
//                System.out.println("source.setOnDragDetected");
        /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
                Dragboard db = pane.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(pane.getId());
                db.setContent(content);

                event.consume();
            }
        });

        pane.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("source.setOnDragDone");
        /* the drag and drop gesture ended */
        /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
//                    System.out.println(resultMap.entrySet());
                }
                event.consume();
            }
        });
    }

    public void checkDisable(Pane pane) {
        int paneId = Integer.parseInt(pane.getId().substring(2));
        resultMap.entrySet().stream().forEach((entry) -> {
            if (entry.getKey() == paneId) {
                pane.setDisable(true);
            }
        });
    }

    public void setDisableDroppedSource(int nodeId) {
        stockListView.getItems().forEach(item -> {
            if (Integer.parseInt(item.getId().substring(2)) == nodeId) {
                item.setDisable(true);
            }
        });
    }

    public int countStockOfCategory(int stockCategoryId) {
        int numberOfStockGranted = 0;
        if (!resultMap.isEmpty()) {
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryWithId(stockCategoryId));
            } else {
                stockDataList.addAll(ODBC_PubsBDForStock.
                        selectStockOfCategoryWithIdInRepository(stockCategoryId,
                                repositoryChoiceBox.getValue().toString()));
            }

            for (DtoStock dtoStock : stockDataList) {
                for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
                    if (entry.getKey() == dtoStock.getId()) {
                        numberOfStockGranted++;
                    }
                }
            }
            stockDataList.clear();
        }
        return numberOfStockGranted;
    }

    public void setTextOfAvailableStock (int stockCategoryId){
        stockCategoryDataList.stream().filter(item -> item.getId() == stockCategoryId).forEach(item -> {
            Label numberOfStockLabel = (Label) item.getPaneContainer().getChildren().get(1);
            numberOfStockLabel.setText("Кількість: " +
                    Integer.toString(item.getNumberOfStock() - countStockOfCategory(stockCategoryId)));
        });
    }

    public void updateStockCategoryComboBoxItems() {
        stockCategoryNameList.clear();
        if (repositoryChoiceBox.getValue().equals("Всі склади")) {
            stockCategoryNameList.addAll(ODBC_PubsBDForStock.
                    selectStockCategoryName(stockTypeChoiceBox.getValue().toString(),
                            showDisableStockCheckBox.isSelected()));
        } else {
            stockCategoryNameList.addAll(ODBC_PubsBDForStock.
                    selectStockCategoryNameInRepository(stockTypeChoiceBox.getValue().toString(),
                            repositoryChoiceBox.getValue().toString(),
                            showDisableStockCheckBox.isSelected()));
        }

        stockCategoryComboBox.setItems(stockCategoryNameList);
    }

    public ObservableMap<Integer, Integer> getResultMap() {
        return resultMap;
    }

    public void setResultMap(ObservableMap<Integer, Integer> resultMap) {
        this.resultMap = resultMap;
    }

    public void setStockCategoryComboBox(ComboBox stockCategoryComboBox) {
        this.stockCategoryComboBox = stockCategoryComboBox;
    }

    public void setStockTypeChoiceBox(ChoiceBox stockTypeChoiceBox) {
        this.stockTypeChoiceBox = stockTypeChoiceBox;
    }

    public void setRepositoryChoiceBox(ChoiceBox repositoryChoiceBox) {
        this.repositoryChoiceBox = repositoryChoiceBox;
    }

    public void setShowDisableStockCheckBox(CheckBox showDisableStockCheckBox) {
        this.showDisableStockCheckBox = showDisableStockCheckBox;
    }

    public void setGoBackButton(Button goBackButton) {
        this.goBackButton = goBackButton;
    }
}
