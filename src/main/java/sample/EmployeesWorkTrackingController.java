package sample;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import sample.dto.DtoEmployeesFullName;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Andriy on 01/30/2016.
 */
public class EmployeesWorkTrackingController {

    @FXML
    public DatePicker datePicker;
    @FXML
    private GridPane gridPaneCapTable;
    @FXML
    private GridPane gridPaneEmployeesData;

    @FXML
    private void initialize () throws SQLException {

        int daysOfMonthNumber = initDateLabel();

        ObservableList<DtoEmployeesFullName> employeesFullNameList = ODBC_PubsBD.selectEmployeesFullName();

        int i = 0;
        for (DtoEmployeesFullName E: employeesFullNameList){
            if (i % 2 == 0){
                for (int j = 0; j<32; j++){
                    Region region = new Region();
                    region.setStyle("-fx-background-color: LightCyan; -fx-border-color: Gainsboro; -fx-pref-height: 40;");
                    if (j == 0) {
                        region.setCursor(Cursor.DEFAULT);
                    }
                    gridPaneEmployeesData.add(region, j, i);
                }
            }
            else{
                for (int j = 0; j<32; j++){
                    Region region = new Region();
                    region.setStyle("-fx-border-color: Gainsboro; -fx-pref-height: 40;");
                    if (j == 0) {
                        region.setCursor(Cursor.DEFAULT);
                    }
                    gridPaneEmployeesData.add(region, j, i);
                }
            }
            for (int j = 1; j<32; j++) {
                CheckBox checkBox = new CheckBox();
                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                checkBox.setCursor(Cursor.HAND);
                checkBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        if (checkBox.isSelected() == true) {
                            try {
                                initWorkingHoursTextFiled(checkBox);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        else {
                            removeWorkingHoursTextFiled(checkBox);
                        }
                    }
                });
                checkBox.setId(makeCheckBoxId(E.getId(), j));
                if (j>daysOfMonthNumber) {
                    checkBox.setDisable(true);
                }
                gridPaneEmployeesData.add(checkBox, j, i);
                gridPaneEmployeesData.setHalignment(checkBox, HPos.CENTER);
                gridPaneEmployeesData.setValignment(checkBox, VPos.CENTER);

            }
            Label label = new Label(E.getSurnameAndInitials());
            label.setPadding(new Insets(0, 0, 0, 5));
            label.setFont(Font.font ("System Regular", 14));
            label.setCursor(Cursor.DEFAULT);
            label.setId(Integer.toString(E.getId()));
            gridPaneEmployeesData.add(label, 0, i);
            i++;
        }
    }

    public int initDateLabel () {
//        DateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("uk"));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM");
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
//        cal.set(2015, 3, 1);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int myMonth=cal.get(Calendar.MONTH);
        int daysOfMonthNumber = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int daysCounter = 1;
//        while (myMonth==cal.get(Calendar.MONTH)) {
        while (daysCounter<32) {
            Label label = new Label(dateFormat.format(cal.getTime()));
            label.setId(Integer.toString(daysCounter));
            if(cal.get(Calendar.MONTH) != myMonth) {
                label.setDisable(true);
            }
            gridPaneCapTable.add(label, daysCounter+1, 0);
            gridPaneCapTable.setHalignment(label, HPos.CENTER);
            gridPaneCapTable.setValignment(label, VPos.CENTER);

            cal.add(Calendar.DAY_OF_MONTH, 1);
            daysCounter++;
        }
        return daysOfMonthNumber;
    }

    public String makeCheckBoxId(int employeesId, int columnIndex) {
        return Integer.toString(employeesId)+":"+Integer.toString(columnIndex);
    }

    public void initWorkingHoursTextFiled (Node thisCheckBox) throws SQLException {
        final String checkBoxId = thisCheckBox.getId();
        TextField textField = new TextField();

        try {
            textField.setText(Integer.toString(ODBC_PubsBD.selectDefaultEmployeesWorkingHours(
                    calculateEmployeesId(checkBoxId))));
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        textField.setId("t"+checkBoxId);
        textField.setPrefHeight(16.0);
        textField.setMinHeight(16.0);
        textField.setAlignment(Pos.CENTER);
        textField.setPadding(Insets.EMPTY);
        gridPaneEmployeesData.add(textField, gridPaneEmployeesData.getColumnIndex(thisCheckBox),
                gridPaneEmployeesData.getRowIndex(thisCheckBox));
        gridPaneEmployeesData.setHalignment(textField, HPos.CENTER);
        gridPaneEmployeesData.setValignment(textField, VPos.BOTTOM);
    }

    public void removeWorkingHoursTextFiled (Node thisCheckBox) {
        String id  = "t"+thisCheckBox.getId();
        ObservableList<Node> childrens = gridPaneEmployeesData.getChildren();
        int i = 0;
        for(Node N: childrens) {
            if(Objects.equals(N.getId(), id) == true) {
                childrens.remove(i);
                break;
            }
            i++;
        }
    }

    public int calculateEmployeesId (String nodeId) {
        return Integer.parseInt(nodeId.substring(0, nodeId.indexOf(":")));
    }
}
