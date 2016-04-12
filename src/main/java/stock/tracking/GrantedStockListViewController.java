package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import stock.tracking.dto.DtoGrantedStock;
import stock.tracking.dto.DtoResult;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class GrantedStockListViewController extends LiableListViewController {
    public GridPane rootGridPane;
    public ListView<Pane> listView;

    private LinkedList<Integer> stockIdList = new LinkedList<>();
    private Integer liableId;
    private ObservableList<DtoGrantedStock> listViewDataList = FXCollections.observableArrayList();

    private List<DtoResult> resultList = new ArrayList<>();
    private Pane liablePane;
    private StockListViewController stockListViewController;

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/stock.tracking/GrantedStockListViewStyle.css").toExternalForm());
    }

    public void initListView() {
        listView.getItems().clear();
        listViewDataList.clear();
        if (!stockIdList.isEmpty()) {
            listViewDataList.addAll(ODBC_PubsBDForLiable.selectStockWithId(stockIdList));
            listViewDataList.forEach(item -> {
                item.initStockPaneContainer();
                setActionRejectStockButton(item);
                listView.getItems().add(item.getPaneContainer());
            });
        }
    }

    private void setActionRejectStockButton(DtoGrantedStock item){
        Button rejectStockButton = (Button) item.getPaneContainer().getChildren().get(1);
        rejectStockButton.setOnAction((ActionEvent event) -> {
            IntStream.range(0, resultList.size()).forEach(i -> {
                if (resultList.size() > i && resultList.get(i).getStockId() == item.getId()) {
                    resultList.remove(i);
                    listView.getItems().remove(item.getPaneContainer());
                    updateNumberOfGrantedStock();
                    stockListViewController.setTextOfAvailableStock(item.getStockCategoryId());
                }
            });
        });
    }

    private void updateNumberOfGrantedStock(){
        Label label = (Label) liablePane.getChildren().get(1);
        if (resultList.size() == 0) {
            label.setText("");
        } else {
            label.setText("Кількість: " + resultList.size());
        }
    }

    public void clearListView(){
        listView.getItems().clear();
        listViewDataList.clear();
        liableId = null;
    }

    public void setStockIdList(LinkedList<Integer> stockIdList) {
        this.stockIdList = stockIdList;
    }

    public Integer getLiableId() {
        return liableId;
    }

    public void setLiableId(Integer liableId) {
        this.liableId = liableId;
    }

    public void setResultList(List<DtoResult> resultList) {
        this.resultList = resultList;
    }

    public void setLiablePane(Pane liablePane) {
        this.liablePane = liablePane;
    }

    public void setStockListViewController(StockListViewController stockListViewController) {
        this.stockListViewController = stockListViewController;
    }
}
