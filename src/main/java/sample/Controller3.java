package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.dto.DtoEmployeesFullName;

public class Controller3<T extends DtoEmployeesFullName> {

    @FXML
    public TableColumn<T, String> colName;
    @FXML
    public TableColumn<T, String> colSomeDate;
    @FXML
    private TableView<T> tableView;

    private final ObservableList<T> employeesFullNameList = FXCollections.observableArrayList();

    public ObservableList<T> getEmployeesFullNameList() {
        return employeesFullNameList;
    }

    @FXML
    public void initialize() {

//        columnId.setCellValueFactory(new PropertyValueFactory("students_id"));

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSomeDate.setCellValueFactory(new PropertyValueFactory<>("surname"));

        tableView.setItems(employeesFullNameList);

    }

}
