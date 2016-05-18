package objects.tracking;


import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

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
    }

    private void initContentTypeChoiceBox(){
        contentTypeChoiceBox.getItems().addAll("Тільки вільні працівники", "Всі працівники");
        contentTypeChoiceBox.setTooltip(new Tooltip("Якийсь текст"));

        contentTypeChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            if (newValue.equals(contentTypeChoiceBox.getItems().get(0))) {
                employeesListViewController.initList(selectFreeEmployees());
            } else {
                employeesListViewController.initList(selectAllEmployees());
            }
        });

        contentTypeChoiceBox.setValue(contentTypeChoiceBox.getItems().get(0));
    }
}
