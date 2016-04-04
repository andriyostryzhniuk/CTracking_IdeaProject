package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import stock.tracking.dto.DtoLiableListView;
import stock.tracking.dto.DtoResult;
import stock.tracking.dto.DtoStock;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 04/01/2016.
 */
public class LiableListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> liableListView;

    public ChoiceBox liableTypeChoiceBox = new ChoiceBox();

    public ObservableList<DtoLiableListView> liableDataList = FXCollections.observableArrayList();
    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    private StockListViewController stockListViewController;

    private List<DtoResult> resultList = new ArrayList<>();

    @FXML
    public void initialize() {
        liableListView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());
    }

    public void initLiableListView() {
        liableDataList.clear();
        liableListView.getItems().clear();

        if (liableTypeChoiceBox.getValue().equals("Об'єкти")) {
            liableDataList.addAll(ODBC_PubsBDForLiable.selectObjects());
        } else {
            liableDataList.addAll(ODBC_PubsBDForLiable.selectEmployees());
        }

        liableDataList.forEach(item -> item.initPaneContainer());

        liableDataList.forEach(item -> {
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
                        resultList.add(new DtoResult(stockId, Integer.parseInt(pane.getId()), null));
                        stockListViewController.setResultList(resultList);
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
                        if (!resultList.isEmpty()) {
                            for (DtoStock dtoStock : stockDataList) {
                                for (DtoResult item : resultList) {
                                    if (item.getStockId() == dtoStock.getId()) {
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
                                if (!resultList.contains(item.getId())) {
                                    resultList.add(new DtoResult(item.getId(), Integer.parseInt(pane.getId()), null));
                                    i++;
                                }
                                if (i == numberOfStockToGrant) {
                                    break;
                                }
                            }
                            stockListViewController.setResultList(resultList);
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
        for (DtoResult item : resultList) {
            if (item.getEmployeesId() == employeesID) {
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

    public List<DtoResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<DtoResult> resultList) {
        this.resultList = resultList;
    }

    public void setStockListViewController(StockListViewController stockListViewController) {
        this.stockListViewController = stockListViewController;
    }

    public void setLiableTypeChoiceBox(ChoiceBox liableTypeChoiceBox) {
        this.liableTypeChoiceBox = liableTypeChoiceBox;
    }
}
