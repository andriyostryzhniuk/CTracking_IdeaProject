package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.io.UncheckedIOException;

public class MainWindowController {

    public GridPane mainGridPane;

    public void initEmployeesWorkTracking(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees.attendance.table/WindowAttendance.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
