package stock.tracking;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

/**
 * Created by Andriy on 04/12/2016.
 */
public class GrantedStockListViewController {
    public GridPane rootGridPane;
    public ListView listView;

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/stock.tracking/GrantedStockListViewStyle.css").toExternalForm());
    }
}
