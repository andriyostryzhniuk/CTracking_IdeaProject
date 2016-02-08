package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import java.io.IOException;

public class MainWindowController {

    public GridPane mainGridPane;

    public void initEmployeesWorkTracking(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EmployeesWorkTracking.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
