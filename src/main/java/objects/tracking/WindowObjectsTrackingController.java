package objects.tracking;


import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import objects.tracking.dto.DTOResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static objects.tracking.ODBC_PubsBD.insertIntoObjectEmployees;
import static objects.tracking.ODBC_PubsBD.selectAllEmployees;
import static objects.tracking.ODBC_PubsBD.selectFreeEmployees;

public class WindowObjectsTrackingController {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
    public ChoiceBox contentTypeChoiceBox;
//    public TextArea notesTextArea;

    private EmployeesListViewController employeesListViewController;
    private ObjectsListViewController objectsListViewController;

    private List<DTOResult> resultList = new ArrayList<>();

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

        employeesListViewController.setResultList(resultList);
        objectsListViewController.setResultList(resultList);
        objectsListViewController.setEmployeesListViewController(employeesListViewController);
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
        insertIntoObjectEmployees(resultList);
        resultList.clear();
        employeesListViewController.initList();
    }
}
