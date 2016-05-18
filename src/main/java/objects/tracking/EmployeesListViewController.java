package objects.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import objects.tracking.dto.DTOEmployees;

import static objects.tracking.ODBC_PubsBD.selectFreeEmployees;

public class EmployeesListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;

    ObservableList<DTOEmployees> employeesListViewDataList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        listView.getStylesheets().add(getClass().getResource("/styles/ListViewStyle.css").toExternalForm());
        initList();
    }

    private void initList(){
        employeesListViewDataList.clear();
        employeesListViewDataList.addAll(selectFreeEmployees());

        employeesListViewDataList.forEach(item -> {
            item.initPaneContainer();
            listView.getItems().add(item.getPaneContainer());
        });
    }
}
