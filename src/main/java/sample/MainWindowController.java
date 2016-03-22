package sample;

import employees.attendance.table.WindowAttendanceController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import stock.tracking.WindowStockTrackingController;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MainWindowController {

    public GridPane mainGridPane;

    private WindowAttendanceController windowAttendanceController;
    private WindowStockTrackingController windowStockTrackingController;

    public void initEmployeesWorkTracking(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees.attendance.table/WindowAttendance.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            windowAttendanceController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initAttendanceButtons(), 1, 2);
    }

    public Button initButtonClose (){
        Button buttonClose = new Button("Закрити");
        buttonClose.setPrefHeight(25.0);
        buttonClose.setPrefWidth(85.0);
        buttonClose.setOnAction((ActionEvent event) -> {
            mainGridPane.getChildren().remove(1, 3);
        });
        return buttonClose;
    }

    public Button initButtonAttendanceSave () {
        Button buttonSave = new Button("Зберегти");
        buttonSave.setPrefHeight(25.0);
        buttonSave.setPrefWidth(85.0);
        buttonSave.setOnAction((ActionEvent event) -> {
            windowAttendanceController.getTableViewController().saveToDB();
        });
        return buttonSave;
    }

    public GridPane initAttendanceButtons () {
        GridPane buttonContainer = new GridPane();
        buttonContainer.setAlignment(Pos.TOP_RIGHT);

        Button buttonSave = initButtonAttendanceSave();
        buttonContainer.add(buttonSave, 0, 0);
        buttonContainer.setMargin(buttonSave, new Insets(20, 5, 0, 0));

        Button buttonClose = initButtonClose();
        buttonContainer.add(buttonClose, 1, 0);
        buttonContainer.setMargin(buttonClose, new Insets(20, 0, 0, 0));

        return buttonContainer;
    }

    public void initStockTracking(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/WindowStockTracking.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            windowStockTrackingController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
