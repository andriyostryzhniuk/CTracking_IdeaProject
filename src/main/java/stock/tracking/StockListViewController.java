package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import stock.tracking.dto.DtoResult;
import stock.tracking.dto.DtoStock;
import stock.tracking.dto.DtoStockListView;
import java.util.ArrayList;
import java.util.List;

public class StockListViewController {
    public GridPane rootGridPane;
    public ListView<Pane> listView;
    public StackPane headerStackPane;
    public GridPane headerGridPane;
    public Pane headerPane;
    public Label headerLabel;

    public ChoiceBox contentChoiceBox = new ChoiceBox();
    public ChoiceBox stockTypeChoiceBox = new ChoiceBox();
    public ComboBox comboBoxSearch = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public ChoiceBox repositoryChoiceBox = new ChoiceBox();
    public CheckBox showDisableStockCheckBox = new CheckBox();
    public Button levelUpButton;
    private String listViewDateParameter;

    public ObservableList<DtoStockListView> stockListViewDataList = FXCollections.observableArrayList();
    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    public ObservableList<String> stockNameList = FXCollections.observableArrayList();

    private List<DtoResult> resultList = new ArrayList<>();

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());
        levelUpButton = initLevelUpButton();

        headerGridPane.add(levelUpButton, 0, 0);
        headerGridPane.setMargin(levelUpButton, new Insets(0, 10, 0, 10));
    }

    public void initListView(String stockType) {
        stockNameList.clear();
        stockListViewDataList.clear();
        listView.getItems().clear();
        showDisableStockCheckBox.setDisable(true);

        if (listViewDateParameter.equals("Категорії")) {
            showDisableStockCheckBox.setDisable(false);
            levelUpButton.setDisable(true);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.selectStockCategory(stockType, showDisableStockCheckBox.isSelected()));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectStockCategoryInRepository(stockType, repositoryChoiceBox.getValue().toString(),
                                showDisableStockCheckBox.isSelected()));
            }

            stockListViewDataList.forEach(item -> {
//            count of stock available in each category
                int numberOfStockGranted = countStockOfCategory(item.getId());
                item.initCategoryPaneContainer(numberOfStockGranted);

                setSourceDragAndDrop(item.getPaneContainer());
                item.getPaneContainer().setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                setListViewDateParameter(item.getName());
//                                objectId = item.getId();
                                initListView(stockTypeChoiceBox.getValue().toString());
                            }
                        }
                    }
                });
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        } else if (listViewDateParameter.equals("Весь інвентар")) {
            levelUpButton.setDisable(false);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.selectAllStockOfType(stockType));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectAllStockOfTypeInRepository(stockType, repositoryChoiceBox.getValue().toString()));
            }
            stockListViewDataList.forEach(item -> {
                item.initStockPaneContainer();
                setSourceDragAndDrop(item.getPaneContainer());
                checkDisable(item.getPaneContainer());
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        } else {
            levelUpButton.setDisable(false);
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategory(listViewDateParameter));
            } else {
                stockListViewDataList.addAll(ODBC_PubsBDForStock.
                        selectStockOfCategoryInRepository(listViewDateParameter, repositoryChoiceBox.getValue().toString()));
            }
            stockListViewDataList.forEach(item -> {
                item.initStockPaneContainer();
                setSourceDragAndDrop(item.getPaneContainer());
                checkDisable(item.getPaneContainer());
                listView.getItems().add(item.getPaneContainer());
                stockNameList.add(item.getName());
            });
        }
        comboBoxSearch.setItems(stockNameList);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
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
//                    System.out.println(resultList);
                }
                event.consume();
            }
        });
    }

    public void checkDisable(Pane pane) {
        int paneId = Integer.parseInt(pane.getId().substring(2));
        resultList.forEach(item -> {
            if (item.getStockId() == paneId) {
                pane.setDisable(true);
            }
        });
    }

    public void setDisableDroppedSource(int nodeId) {
        listView.getItems().forEach(item -> {
            if (Integer.parseInt(item.getId().substring(2)) == nodeId) {
                item.setDisable(true);
            }
        });
    }

    public int countStockOfCategory(int stockCategoryId) {
        stockDataList.clear();
        int numberOfStockGranted = 0;
        if (!resultList.isEmpty()) {
            if (repositoryChoiceBox.getValue().equals("Всі склади")) {
                stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryWithId(stockCategoryId));
            } else {
                stockDataList.addAll(ODBC_PubsBDForStock.
                        selectStockOfCategoryWithIdInRepository(stockCategoryId,
                                repositoryChoiceBox.getValue().toString()));
            }

            for (DtoStock dtoStock : stockDataList) {
                for(DtoResult item : resultList) {
                    if (item.getStockId() == dtoStock.getId()) {
                        numberOfStockGranted++;
                    }
                }
            }
            stockDataList.clear();
        }
        return numberOfStockGranted;
    }

    public void setTextOfAvailableStock (int stockCategoryId){
        stockListViewDataList.stream().filter(item -> item.getId() == stockCategoryId).forEach(item -> {
            Label numberOfStockLabel = (Label) item.getPaneContainer().getChildren().get(1);
            numberOfStockLabel.setText("Кількість: " +
                    Integer.toString(item.getNumberOfStock() - countStockOfCategory(stockCategoryId)));
        });
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
                if (Integer.parseInt(pane.getId().substring(2)) == stockID) {
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
        Image image = new Image(getClass().getResourceAsStream("/image/level_up_icon.png"));
        button.getStylesheets().add(getClass().getResource("/stock.tracking/ButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setPrefWidth(10);
        button.setMaxWidth(10);
        button.setOnAction((ActionEvent event) -> {
            if (!listViewDateParameter.equals("Категорії")) {
                setListViewDateParameter("Категорії");
                contentChoiceBox.setValue("Категорії");
                initListView(stockTypeChoiceBox.getValue().toString());
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

    public void setResultList(List<DtoResult> resultList) {
        this.resultList = resultList;
    }

    public void setComboBoxSearch(ComboBox comboBoxSearch) {
        this.comboBoxSearch = comboBoxSearch;
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

    public void setContentChoiceBox(ChoiceBox contentChoiceBox) {
        this.contentChoiceBox = contentChoiceBox;
    }

    public String getListViewDateParameter() {
        return listViewDateParameter;
    }

    public void setListViewDateParameter(String listViewDateParameter) {
        this.listViewDateParameter = listViewDateParameter;
        setTextToHeaderLabel(listViewDateParameter);
    }

}
