package main;

import attendance.tracking.WindowAttendanceController;
import employees.WindowEmployeesController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import object.WindowObjectsController;
import objects.tracking.WindowObjectsTrackingController;
import stock.tracking.WindowStockTrackingController;
import stocks.WindowStocksController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.IntStream;

public class MainWindowController {

    public GridPane mainGridPane;

    public MenuItem closeMenuItem;
    public MenuItem workTracking;
    public MenuItem stockTracking;
    public MenuItem objectsTracking;
    public MenuItem employeesWindow;
    public MenuItem stocksWindow;
    public MenuItem objectsWindow;
    public MenuItem aboutMenuItem;

    private WindowAttendanceController windowAttendanceController;
    private WindowStockTrackingController windowStockTrackingController;
    private WindowObjectsTrackingController windowObjectsTrackingController;
    private WindowEmployeesController windowEmployeesController;
    private WindowStocksController windowStocksController;
    private WindowObjectsController windowObjectsController;

    @FXML
    public void initialize(){
        closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));
        workTracking.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        stockTracking.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN));
        objectsTracking.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN));
        employeesWindow.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.CONTROL_DOWN));
        stocksWindow.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.CONTROL_DOWN));
        objectsWindow.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.CONTROL_DOWN));
        aboutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F2));
    }

    public void initEmployeesWorkTracking(ActionEvent actionEvent) throws IOException {
        if (windowAttendanceController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/attendance/tracking/WindowAttendance.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowAttendanceController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            mainGridPane.add(initButtonContainer(initButtonAttendanceSave(), initButtonClose()), 1, 2);
        }
    }

    public void initStockTracking(ActionEvent actionEvent) throws IOException {
        if (windowStockTrackingController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/WindowStockTracking.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowStockTrackingController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
        }
    }

    public void initObjectsTracking(ActionEvent actionEvent) {
        if (windowObjectsTrackingController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/WindowObjectsTracking.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowObjectsTrackingController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
        }
    }

    public void initEmployeesWindow(ActionEvent actionEvent) {
        if (windowEmployeesController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees/WindowEmployees.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowEmployeesController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            Button saveButton = initButton("Зберегти");
            saveButton.setVisible(false);
            windowEmployeesController.setSaveButton(saveButton);
            mainGridPane.add(initButtonContainer(saveButton, initButtonClose()), 1, 2);
        }
    }

    public void initStocksWindow(ActionEvent actionEvent) {
        if (windowStocksController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stocks/WindowStocks.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowStocksController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
        }
    }

    public void initObjectsWindow(ActionEvent actionEvent) {
        if (windowObjectsController == null) {
            removeMainGridPaneChildren();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/WindowObjects.fxml"));
            try {
                mainGridPane.add(fxmlLoader.load(), 1, 1);
                windowObjectsController = fxmlLoader.getController();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
            Button saveButton = initButton("Зберегти");
            saveButton.setVisible(false);
            windowObjectsController.setSaveButton(saveButton);
            mainGridPane.add(initButtonContainer(saveButton, initButtonClose()), 1, 2);
        }
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

    public GridPane initButtonContainer(Button... buttons) {
        GridPane buttonContainer = new GridPane();
        buttonContainer.setAlignment(Pos.TOP_RIGHT);

        IntStream.range(0, buttons.length).forEach(i -> {
            if (i == 0 && buttons.length > 1) {
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
        for (int i = mainGridPane.getChildren().size(); i > 3; i--) {
            mainGridPane.getChildren().remove(i-1);
        }
        windowAttendanceController = null;
        windowStockTrackingController = null;
        windowObjectsTrackingController = null;
        windowEmployeesController = null;
        windowStocksController = null;
        windowObjectsController = null;
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void aboutProgram(ActionEvent actionEvent) {

    }
}
