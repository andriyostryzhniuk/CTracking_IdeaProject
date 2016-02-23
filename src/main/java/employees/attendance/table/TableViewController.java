package employees.attendance.table;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import dto.DtoEmployeesFullName;

public class TableViewController<T extends DtoEmployeesFullName> {

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

        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colSomeDate.setCellValueFactory(new PropertyValueFactory<>("id"));

        tableView.setItems(employeesFullNameList);

    }

}
