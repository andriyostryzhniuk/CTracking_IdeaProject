package stock.tracking;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import combo.box.AutoCompleteComboBoxListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import stock.tracking.dto.DtoEmployees;
import stock.tracking.dto.DtoStock;
import java.io.IOException;
import java.util.Map;

public class WindowStockTrackingController {

    public BorderPane rootBorderPane;
    private GridPane topGridPane = new GridPane();
    public GridPane gridPane;

    public StockListViewController stockListViewController;

    public ListView<Pane> liableListView;
    private ObservableList<DtoEmployees> employeesDataList = FXCollections.observableArrayList();
    private ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();

    public ObservableMap<Integer, Integer> resultMap = FXCollections.observableHashMap();

    @FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/StockListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            stockListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initTopGridPane();
        rootBorderPane.setTop(topGridPane);
        rootBorderPane.setAlignment(topGridPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(topGridPane, new Insets(0.0, 0.0, 20.0, 0.0));

        liableListView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());

        initLiableListView();
    }

    public void initTopGridPane() {
        ChoiceBox repositoryChoiceBox = initRepositoryChoiceBox();
        stockListViewController.setRepositoryChoiceBox(repositoryChoiceBox);

        ComboBox stockCategoryComboBox = initStockCategoryComboBox();
        stockListViewController.setStockCategoryComboBox(stockCategoryComboBox);

        ChoiceBox stockTypeChoiceBox = initStockTypeChoiceBox();
        stockListViewController.setStockTypeChoiceBox(stockTypeChoiceBox);

        CheckBox showDisableStockCheckBox = initShowDisableStockCheckBox();
        stockListViewController.setShowDisableStockCheckBox(showDisableStockCheckBox);

        topGridPane.add(repositoryChoiceBox, 0, 0);
        topGridPane.add(stockTypeChoiceBox, 1, 0);
        topGridPane.add(initGoBackButton(), 2, 0);
        topGridPane.add(stockCategoryComboBox, 3, 0);
        topGridPane.add(showDisableStockCheckBox, 4, 0);
    }

    public ChoiceBox initStockTypeChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("вартісні", "розхідні");
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                stockListViewController.stockTypeChoiceBox.setValue(choiceBox.getValue());

                Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

                stockListViewController.updateStockCategoryComboBoxItems();

                stockListViewController.stockCategoryComboBox.setValue(stockListViewController.stockCategoryComboBox.getItems().get(0));
                stockListViewController.comboBoxListener.setValue(stockListViewController.stockCategoryComboBox.getValue());
                new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

                if (oldStockCategoryComboBoxValue != null && oldStockCategoryComboBoxValue.equals(stockListViewController.comboBoxListener.getValue())) {
                    stockListViewController.initStockListView(choiceBox.getValue().toString(), stockListViewController.comboBoxListener.getValue().toString());
                }
            }
        });
        choiceBox.setValue(choiceBox.getItems().get(0));
        return choiceBox;
    }

    public ComboBox initStockCategoryComboBox() {
        ComboBox comboBox = new ComboBox();

        comboBox.getStylesheets().add(getClass().getResource("/stock.tracking/ComboBoxStyle.css").toExternalForm());

        comboBox.setItems(stockListViewController.stockCategoryNameList);

        new AutoCompleteComboBoxListener<>(comboBox, stockListViewController.comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
//                                mouse pressed
                                comboBox.getStyleClass().remove("warning");
                                stockListViewController.comboBoxListener.setValue(comboBox.getValue());
                            }
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

        stockListViewController.comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                if (newValue != null) {
                    comboBox.getStyleClass().remove("warning");
                    stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                            stockListViewController.comboBoxListener.getValue().toString());
                }
            }
        });
        return comboBox;
    }

    public ChoiceBox initRepositoryChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());

        choiceBox.setItems(ODBC_PubsBD.selectRepositoryName());
        choiceBox.setValue(choiceBox.getItems().get(0));

        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

                stockListViewController.updateStockCategoryComboBoxItems();

                if (oldStockCategoryComboBoxValue != null) {
                    stockListViewController.stockCategoryComboBox.setValue(oldStockCategoryComboBoxValue);
                    stockListViewController.comboBoxListener.setValue(oldStockCategoryComboBoxValue);
                }
                new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

                stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                        stockListViewController.comboBoxListener.getValue().toString());
            }
        });

        return choiceBox;
    }

    public Button initGoBackButton() {
        Button button = new Button("Back");
        button.setOnAction((ActionEvent event) -> {
            if (!stockListViewController.comboBoxListener.getValue().equals("Всі категорії")) {
                stockListViewController.stockCategoryComboBox.setValue("Всі категорії");
                stockListViewController.comboBoxListener.setValue(stockListViewController.stockCategoryComboBox.getValue());
            }
        });
        return button;
    }

    public CheckBox initShowDisableStockCheckBox(){
        CheckBox checkBox = new CheckBox("Показувати недоступні категорії");
        checkBox.setSelected(false);
        checkBox.setOnAction((ActionEvent event) -> {
            Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

            stockListViewController.updateStockCategoryComboBoxItems();

            if (oldStockCategoryComboBoxValue != null) {
                stockListViewController.stockCategoryComboBox.setValue(oldStockCategoryComboBoxValue);
                stockListViewController.comboBoxListener.setValue(oldStockCategoryComboBoxValue);
                stockListViewController.stockCategoryComboBox.getStyleClass().remove("warning");
            }
            new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

            if (stockListViewController.comboBoxListener.getValue().equals("Всі категорії")) {
                stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                        stockListViewController.comboBoxListener.getValue().toString());
            }
        });
        return checkBox;
    }

    public void initLiableListView() {
        employeesDataList.addAll(ODBC_PubsBD.selectEmployees());
        employeesDataList.forEach(item -> item.initPaneContainer());

        employeesDataList.forEach(item -> {
            setTargetDragAndDrop(item.getPaneContainer());
            liableListView.getItems().add(item.getPaneContainer());
        });
    }

    public void setTargetDragAndDrop(Pane pane) {
        pane.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragOver");
        /* data is dragged over the target */
        /* accept it only if it is not dragged from the same node
         * and if it has a string data */
                if (event.getGestureSource() != pane &&
                        event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        pane.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragEntered");
    /* the drag-and-drop gesture entered the target */
    /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != pane &&
                        event.getDragboard().hasString()) {
                    pane.setStyle("-fx-background-color: rgba(105, 105, 105, .8)");
                }

                event.consume();
            }
        });

        pane.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragExited");
        /* mouse moved away, remove the graphical cues */
                pane.setStyle("-fx-background-color: rgba(105, 105, 105, .5)");
                event.consume();
            }
        });

        pane.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragDropped");
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    if (Integer.parseInt(db.getString().substring(0, 1)) == 0) {
                        int stockId = Integer.parseInt(db.getString().substring(2));
                        resultMap.put(stockId, Integer.parseInt(pane.getId()));
                        stockListViewController.setResultMap(resultMap);
                        stockListViewController.setDisableDroppedSource(stockId);
                        setTextNumberOfStock(Integer.parseInt(pane.getId()));
                    } else {

                        int stockCategoryId = Integer.parseInt(db.getString().substring(2));

                        if (stockListViewController.repositoryChoiceBox.getValue().equals("Всі склади")) {
                            stockDataList.addAll(ODBC_PubsBD.selectStockOfCategoryWithId(stockCategoryId));
                        } else {
                            stockDataList.addAll(ODBC_PubsBD.
                                    selectStockOfCategoryWithIdInRepository(stockCategoryId,
                                            stockListViewController.repositoryChoiceBox.getValue().toString()));
                        }

                        int numberOfStockGranted = 0;
                        if (!resultMap.isEmpty()) {
                            for (DtoStock dtoStock : stockDataList) {
                                for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
                                    if (entry.getKey() == dtoStock.getId()) {
                                        numberOfStockGranted++;
                                    }
                                }
                            }
                        }
                        int numberOfAvailableStock = stockDataList.size() - numberOfStockGranted;

                        Label namesLiableLabel = (Label) pane.getChildren().get(0);
                        String namesLiable = namesLiableLabel.getText();
                        PromptOfNumberStock promptOfNumberStock = new PromptOfNumberStock(stockCategoryId, namesLiable);
                        int numberOfStockToGrant =
                                promptOfNumberStock.showPrompt(pane.getScene().getWindow(), numberOfAvailableStock);
                        if (numberOfStockToGrant != 0) {
                            int i = 0;
                            for (DtoStock item : stockDataList) {
                                if (resultMap.get(item.getId()) == null) {
                                    resultMap.put(item.getId(), Integer.parseInt(pane.getId()));
                                    i++;
                                }
                                if (i == numberOfStockToGrant) {
                                    break;
                                }
                            }
                            stockListViewController.setResultMap(resultMap);
                            stockListViewController.setTextOfAvailableStock(stockCategoryId);
                            setTextNumberOfStock(Integer.parseInt(pane.getId()));
                        }
                        stockDataList.clear();
                    }
                    success = true;
                }
        /* let the source know whether the string was successfully
         * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    public void setTextNumberOfStock(int employeesID) {
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
            if (entry.getValue() == employeesID) {
                i++;
            }
        }
        if (i != 0) {
            for (Pane pane : liableListView.getItems()) {
                if (Integer.parseInt(pane.getId()) == employeesID) {
                    Label label = (Label) pane.getChildren().get(1);
                    label.setText("Кількість: " + i);
                }
            }
        }
    }

    public void saveToDB() {
        resultMap.entrySet().stream().forEach((entry) -> {
            ODBC_PubsBD.insertIntoWorkTracking(entry.getKey(), entry.getValue());
        });
        resultMap.clear();
        stockListViewController.setResultMap(resultMap);
    }
}
