package objects.tracking;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class WindowObjectsTrackingController {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
//    public TextArea notesTextArea;

    private EmployeesListViewController employeesListViewController;

    @FXML
    public void initialize(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/EmployeesListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            employeesListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
