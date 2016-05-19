package objects.tracking;


import employees.attendance.table.CustomTableColumn;
import employees.attendance.table.TableViewHolder;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import objects.tracking.dto.DTOObjectEmployees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static objects.tracking.ContextMenu.initContextMenu;
import static objects.tracking.ODBC_PubsBD.deleteFromObjectEmployees;
import static objects.tracking.ODBC_PubsBD.insertIntoObjectEmployees;
import static objects.tracking.ODBC_PubsBD.selectEmployeesById;

public class WindowObjectsTrackingController<T extends DTOObjectEmployees> {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
    public ChoiceBox contentTypeChoiceBox;
    public StackPane stackPane;
//    public TextArea notesTextArea;

    private EmployeesListViewController employeesListViewController;
    private ObjectsListViewController objectsListViewController;

    private List<DTOObjectEmployees> insertResultList = new ArrayList<>();
    private List<Integer> deleteResultList = new ArrayList<>();
    private ObservableList<T> tableViewDataList = FXCollections.observableArrayList();
    private List<T> dtoObjectEmployeesList;

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> employeeNameCol = new CustomTableColumn<>("Працівники");
    public CustomTableColumn<T, String> startDateNameCol = new CustomTableColumn<>("Початок");
    public CustomTableColumn<T, String> finishDateNameCol = new CustomTableColumn<>("Закінчення");

    @FXML
    public void initialize(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/EmployeesListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            employeesListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        initContentTypeChoiceBox();

        fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/ObjectsListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 3, 0);
            objectsListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        employeesListViewController.setInsertResultList(insertResultList);
        objectsListViewController.setInsertResultList(insertResultList);
        objectsListViewController.setEmployeesListViewController(employeesListViewController);
        objectsListViewController.setWindowObjectsTrackingController(this);

        setTableViewParameters();
    }

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("Немає жодного працівника"));
    }

    private void initContentTypeChoiceBox(){
        contentTypeChoiceBox.getItems().addAll("Тільки вільні працівники", "Всі працівники");
        contentTypeChoiceBox.setTooltip(new Tooltip("Якийсь текст"));

        contentTypeChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            if (newValue.equals(contentTypeChoiceBox.getItems().get(0))) {
                employeesListViewController.setAllEmployees(false);
                employeesListViewController.initList();
            } else {
                employeesListViewController.setAllEmployees(true);
                employeesListViewController.initList();
            }
        });

        contentTypeChoiceBox.setValue(contentTypeChoiceBox.getItems().get(0));
    }

    public void saveToDB() {
        insertIntoObjectEmployees(insertResultList);
        insertResultList.clear();
        deleteFromObjectEmployees(deleteResultList);
        deleteResultList.clear();
        employeesListViewController.getJustReleasedEmployeesList().clear();
        employeesListViewController.initList();
    }

    private void fillCols() {
        employeeNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        startDateNameCol.setCellValueFactory(new PropertyValueFactory("formatStartDate"));
        finishDateNameCol.setCellValueFactory(new PropertyValueFactory("formatFinishDate"));
    }

    private void setColsDateProperties() {
        employeeNameCol.setPercentWidth(118); employeeNameCol.setMinWidth(118);
        startDateNameCol.setPercentWidth(65); startDateNameCol.setMinWidth(65);
        finishDateNameCol.setPercentWidth(65); finishDateNameCol.setMinWidth(65);
    }

    private void fillTableView(){
        tableView.getTableView().getColumns().addAll(employeeNameCol, startDateNameCol, finishDateNameCol);
    }

    public void initTableView(List<T> dtoObjectEmployeesList){
        this.dtoObjectEmployeesList = dtoObjectEmployeesList;
        tableViewDataList.clear();
        tableView.getTableView().getItems().clear();

        tableViewDataList.addAll(dtoObjectEmployeesList);
        tableView.getTableView().setItems(tableViewDataList);
    }

    public void editRecord() {

    }

    public void removeRecord(T item) {
        if (item.getId() == null) {
            insertResultList.remove(item);
            employeesListViewController.setDisablePane(item.getEmployeeId().toString(), false);
        } else {
            if (item.getFinishDate() == null || item.getFinishDate().after(new Date())) {
                deleteResultList.add(item.getId());
                employeesListViewController.getJustReleasedEmployeesList().add(selectEmployeesById(item.getEmployeeId()));
                if (! employeesListViewController.isAllEmployees()) {
                    employeesListViewController.initList();
                }
            }
        }
        removeItemFromLists(item);
    }

    private void removeItemFromLists(T item) {
        tableViewDataList.remove(item);
        dtoObjectEmployeesList.remove(item);
    }
}
