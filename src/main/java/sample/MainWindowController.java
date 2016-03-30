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
import java.util.stream.IntStream;

public class MainWindowController {

    public GridPane mainGridPane;

    private WindowAttendanceController windowAttendanceController;
    private WindowStockTrackingController windowStockTrackingController;

    public void initEmployeesWorkTracking(ActionEvent actionEvent) throws IOException {
        removeMainGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees.attendance.table/WindowAttendance.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            windowAttendanceController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonAttendanceSave(), initButtonClose()), 1, 2);
    }

    public void initStockTracking(ActionEvent actionEvent) throws IOException {
        removeMainGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/WindowStockTracking.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            windowStockTrackingController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonStockTrackingSave(), initButtonClose()), 1, 2);
    }

    public Button initButton(String text){
        Button button = new Button(text);
        button.setPrefHeight(25.0);
        button.setPrefWidth(85.0);
        return button;
    }

    public Button initButtonClose (){
        Button buttonClose = initButton("Закрити");
        buttonClose.setOnAction((ActionEvent event) -> {
            removeMainGridPaneChildren();
        });
        return buttonClose;
    }

    public Button initButtonAttendanceSave() {
        Button buttonSave = initButton("Зберегти");
        buttonSave.setOnAction((ActionEvent event) -> {
            windowAttendanceController.getTableViewController().saveToDB();
        });
        return buttonSave;
    }

    public Button initButtonStockTrackingSave() {
        Button buttonSave = initButton("Зберегти");
        buttonSave.setOnAction((ActionEvent event) -> {
            windowStockTrackingController.saveToDB();
        });
        return buttonSave;
    }

    public GridPane initButtonContainer(Button... buttons) {
        GridPane buttonContainer = new GridPane();
        buttonContainer.setAlignment(Pos.TOP_RIGHT);

        IntStream.range(0, buttons.length).forEach(i -> {
            if (i == 0) {
                buttonContainer.add(buttons[i], i, 0);
                buttonContainer.setMargin(buttons[i], new Insets(20, 5, 0, 0));
            } else {
                buttonContainer.add(buttons[i], i, 0);
                buttonContainer.setMargin(buttons[i], new Insets(20, 0, 0, 0));
            }
        });
        return buttonContainer;
    }

    public void removeMainGridPaneChildren(){
        for (int i = mainGridPane.getChildren().size(); i > 1; i--) {
            mainGridPane.getChildren().remove(i-1);
        }
    }
}
