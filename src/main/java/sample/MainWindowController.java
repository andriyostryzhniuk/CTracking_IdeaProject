package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
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
        initButtonClose();
    }

    public void initButtonClose (){
        Button buttonClose = new Button("Закрити");
        buttonClose.setPrefHeight(25.0);
        buttonClose.setPrefWidth(85.0);
        buttonClose.setOnAction((ActionEvent event) -> {
            mainGridPane.getChildren().remove(1, 3);
        });
        mainGridPane.add(buttonClose, 1, 2);
        mainGridPane.setHalignment(buttonClose, HPos.RIGHT);
        mainGridPane.setValignment(buttonClose, VPos.TOP);
        mainGridPane.setMargin(buttonClose, new Insets(20, 0, 0, 0));
    }
}
