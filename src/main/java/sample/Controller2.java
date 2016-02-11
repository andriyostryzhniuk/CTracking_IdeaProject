package sample;

import date.picker.DatePicker;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import sample.dto.DtoEmployeesFullName;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Andriy on 01/30/2016.
 */
public class Controller2 {

    @FXML
    public ScrollPane mainScrollPane;
    public BorderPane childBorderPane;
    @FXML
    private GridPane gridPaneCapTable;
    @FXML
    private GridPane gridPaneEmployeesData;

    private DatePicker datePicker;

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public Controller1 employeesWorkTrackingController;

    @FXML
    private void initialize() {

        initEmployeesTrackingTable();

//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/EmployeesWorkTracking.fxml"));
//        Controller1 employeesWorkTrackingController = (Controller1) fxmlLoader.getController();

        //calendar.setTime(employeesWorkTrackingController.getDatePickerValue());

//        System.out.println(employeesWorkTrackingController.getDatePickerValue());

//        Parent parent = mainScrollPane.get();

//        System.out.println(parent.getClass().getName());
//
//        ObservableList<Node> observableList = parent.getChildrenUnmodifiable();
//
//        for (Node N: observableList) {
//            System.out.println(N);
//        }
}

//here is exception
    @FXML
    public void initEmployeesTrackingTable() {

        initDateLabel();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int daysOfMonthNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthNumber);
        String lastDayOfMonth = dateFormat.format(calendar.getTime());

        System.out.println(firstDayOfMonth +" : "+ lastDayOfMonth);

        ObservableList<DtoEmployeesFullName> employeesFullNameList =
                ODBC_PubsBD.selectEmployeesFullName(firstDayOfMonth, lastDayOfMonth);

        int i = 0;
        for (DtoEmployeesFullName E : employeesFullNameList) {
            if (i % 2 == 0) {
                for (int j = 0; j < 32; j++) {
                    Region region = new Region();
                    region.setStyle("-fx-background-color: LightCyan; -fx-border-color: Gainsboro; -fx-pref-height: 40;");
                    if (j == 0) {
                        region.setCursor(Cursor.DEFAULT);
                    }
                    gridPaneEmployeesData.add(region, j, i);
                }
            } else {
                for (int j = 0; j < 32; j++) {
                    Region region = new Region();
                    region.setStyle("-fx-border-color: Gainsboro; -fx-pref-height: 40;");
                    if (j == 0) {
                        region.setCursor(Cursor.DEFAULT);
                    }
                    gridPaneEmployeesData.add(region, j, i);
                }
            }
            for (int j = 1; j < 32; j++) {
                CheckBox checkBox = new CheckBox();
                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                checkBox.setCursor(Cursor.HAND);
                checkBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        if (checkBox.isSelected() == true) {
                            initWorkingHoursTextFiled(checkBox);
                        } else {
                            removeWorkingHoursTextFiled(checkBox);
                        }
                    }
                });
                checkBox.setId(makeCheckBoxId(E.getId(), j));
                if (j > daysOfMonthNumber) {
                    checkBox.setDisable(true);
                }
                gridPaneEmployeesData.add(checkBox, j, i);
                gridPaneEmployeesData.setHalignment(checkBox, HPos.CENTER);
                gridPaneEmployeesData.setValignment(checkBox, VPos.CENTER);

            }
            Label label = new Label(E.getSurnameAndInitials());
            label.setPadding(new Insets(0, 0, 0, 5));
            label.setFont(Font.font("System Regular", 14));
            label.setCursor(Cursor.DEFAULT);
            label.setId(Integer.toString(E.getId()));
            gridPaneEmployeesData.add(label, 0, i);
            i++;
        }
    }

//here is exception
    public void initDateLabel() {
//        DateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("uk"));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM");

        Calendar calendar = Calendar.getInstance();
//        calendar.set(2015, 3, 1);

//        calendar.setTime(employeesWorkTrackingController.getDatePickerValue());
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int myMonth = calendar.get(Calendar.MONTH);
//        int daysOfMonthNumber = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int daysCounter = 1;
//        while (myMonth==cal.get(Calendar.MONTH)) {
        while (daysCounter < 32) {
            Label label = new Label(dateFormat.format(calendar.getTime()));
            label.setId(Integer.toString(daysCounter));
            if (calendar.get(Calendar.MONTH) != myMonth) {
                label.setDisable(true);
            }
            gridPaneCapTable.add(label, daysCounter + 1, 0);
            gridPaneCapTable.setHalignment(label, HPos.CENTER);
            gridPaneCapTable.setValignment(label, VPos.CENTER);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            daysCounter++;
        }
    }

    public String makeCheckBoxId(int employeesId, int columnIndex) {
        return Integer.toString(employeesId) + ":" + Integer.toString(columnIndex);
    }

    public void initWorkingHoursTextFiled(Node thisCheckBox) {
        final String checkBoxId = thisCheckBox.getId();
        TextField textField = new TextField();

        try {
            textField.setText(Integer.toString(ODBC_PubsBD.selectDefaultEmployeesWorkingHours(
                    calculateEmployeesId(checkBoxId))));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        textField.setId("t" + checkBoxId);
        textField.setPrefHeight(16.0);
        textField.setMinHeight(16.0);
        textField.setAlignment(Pos.CENTER);
        textField.setPadding(Insets.EMPTY);
        gridPaneEmployeesData.add(textField, gridPaneEmployeesData.getColumnIndex(thisCheckBox),
                gridPaneEmployeesData.getRowIndex(thisCheckBox));
        gridPaneEmployeesData.setHalignment(textField, HPos.CENTER);
        gridPaneEmployeesData.setValignment(textField, VPos.BOTTOM);
    }

    public void removeWorkingHoursTextFiled(Node thisCheckBox) {
        String id = "t" + thisCheckBox.getId();
        ObservableList<Node> childrens = gridPaneEmployeesData.getChildren();
        int i = 0;
        for (Node N : childrens) {
            if (Objects.equals(N.getId(), id) == true) {
                childrens.remove(i);
                break;
            }
            i++;
        }
    }

    public int calculateEmployeesId(String nodeId) {
        return Integer.parseInt(nodeId.substring(0, nodeId.indexOf(":")));
    }

}
