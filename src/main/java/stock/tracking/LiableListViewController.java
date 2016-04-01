package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import stock.tracking.dto.DtoEmployees;
import stock.tracking.dto.DtoStock;

import java.util.Map;

/**
 * Created by Andriy on 04/01/2016.
 */
public class LiableListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> liableListView;

    public ObservableList<DtoEmployees> employeesDataList = FXCollections.observableArrayList();
    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    private StockListViewController stockListViewController;

    private ObservableMap<Integer, Integer> resultMap = FXCollections.observableHashMap();

    @FXML
    public void initialize() {
        liableListView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());
    }

    public void initLiableListView() {
        employeesDataList.addAll(ODBC_PubsBDForLiable.selectEmployees());
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
                            stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryWithId(stockCategoryId));
                        } else {
                            stockDataList.addAll(ODBC_PubsBDForStock.
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
                        PromptNumberStockToGrant promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, namesLiable);
                        int numberOfStockToGrant =
                                promptNumberStockToGrant.showPrompt(pane.getScene().getWindow(), numberOfAvailableStock);
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

    public ObservableMap<Integer, Integer> getResultMap() {
        return resultMap;
    }

    public void setResultMap(ObservableMap<Integer, Integer> resultMap) {
        this.resultMap = resultMap;
    }

    public void setStockListViewController(StockListViewController stockListViewController) {
        this.stockListViewController = stockListViewController;
    }
}
