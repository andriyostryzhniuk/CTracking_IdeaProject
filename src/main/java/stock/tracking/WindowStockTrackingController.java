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

import static stock.tracking.ODBC_PubsBD.insertIntoWorkTracking;
import static stock.tracking.ODBC_PubsBD.selectStockCategoryName;

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
        ComboBox stockCategoryComboBox = initStockCategoryComboBox();
        stockListViewController.setStockCategoryComboBox(stockCategoryComboBox);
        ChoiceBox stockTypeChoiceBox = initStockTypeChoiceBox();
        stockListViewController.setStockTypeChoiceBox(stockTypeChoiceBox);
        topGridPane.add(stockTypeChoiceBox, 0, 0);
        topGridPane.add(initGoBackButton(), 1, 0);
        topGridPane.add(stockCategoryComboBox, 2, 0);
    }

    public ChoiceBox initStockTypeChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("вартісні", "розхідні");
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                stockListViewController.stockCategoryNameList.clear();
                stockListViewController.stockCategoryNameList.addAll(selectStockCategoryName(choiceBox.getValue().toString()));
                stockListViewController.stockCategoryComboBox.setItems(stockListViewController.stockCategoryNameList);
                stockListViewController.stockCategoryComboBox.setValue(stockListViewController.stockCategoryComboBox.getItems().get(0));
                new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);
                stockListViewController.initStockListView(choiceBox.getValue().toString(), stockListViewController.stockCategoryComboBox.getValue().toString());
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
                stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                        stockListViewController.stockCategoryComboBox.getValue().toString());
            }
        });
        return comboBox;
    }

    public Button initGoBackButton() {
        Button button = new Button("Back");
        button.setOnAction((ActionEvent event) -> {
            if (!stockListViewController.stockCategoryComboBox.getValue().equals("Всі категорії")) {
                stockListViewController.stockCategoryComboBox.setValue("Всі категорії");
                stockListViewController.comboBoxListener.setValue(stockListViewController.stockCategoryComboBox.getValue());
            }
        });
        return button;
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
                        stockDataList.addAll(ODBC_PubsBD.selectStockOfCategoryWithId(stockCategoryId));
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
            insertIntoWorkTracking(entry.getKey(), entry.getValue());
        });
        resultMap.clear();
        stockListViewController.setResultMap(resultMap);
    }
}
