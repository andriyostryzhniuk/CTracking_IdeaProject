package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import stock.tracking.dto.DtoGrantedStock;
import java.util.LinkedList;

/**
 * Created by Andriy on 04/12/2016.
 */
public class GrantedStockListViewController {
    public GridPane rootGridPane;
    public ListView<Pane> listView;

    private LinkedList<Integer> stockIdList = new LinkedList<>();
    private Integer liableId;
    private ObservableList<DtoGrantedStock> listViewDataList = FXCollections.observableArrayList();

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
                listView.getItems().add(item.getPaneContainer());
            });
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
}
