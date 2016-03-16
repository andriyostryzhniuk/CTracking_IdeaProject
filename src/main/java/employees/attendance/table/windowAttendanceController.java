package employees.attendance.table;

import dto.DtoEmployeesFullName;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import sample.ODBC_PubsBD;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WindowAttendanceController<T extends DtoEmployeesFullName> {

    @FXML
    public BorderPane rootBorderPane;

    public date.picker.DatePicker datePicker;

    private int previousMonth;
    private int previousYear;
    private TableViewController tableViewController;

    @FXML
    private void initialize() {

        initDatePicker();

//        System.out.println(getDatePickerValue());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees.attendance.table/TableView.fxml"));
        try {
            rootBorderPane.setCenter(fxmlLoader.load());
            tableViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        updateEmployeesFullNameList();
    }

    public void initDatePicker() {
        datePicker = new date.picker.DatePicker();

        datePicker.setLocale(new Locale("uk"));
        datePicker.getCalendarView().setShowWeeks(false);

        datePicker.setSelectedDate(new Date());
        datePicker.deselectSelection();

        previousMonth = datePicker.selectedDateProperty().get().getMonth();
        previousYear = datePicker.selectedDateProperty().get().getYear();

        datePicker.selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                boolean isException = false;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                try {
                    dateFormat.format(datePicker.selectedDateProperty().get());
                } catch (NullPointerException e) {
                    isException = true;
                }
                if (isException == false && previousMonth != datePicker.selectedDateProperty().get().getMonth() ||
                        isException == false && previousYear != datePicker.selectedDateProperty().get().getYear()) {

                    updateEmployeesFullNameList ();

                }
            }
        });

        rootBorderPane.setTop(datePicker);
        rootBorderPane.setAlignment(rootBorderPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(datePicker, new Insets(0.0, 0.0, 20.0, 0.0));
    }

    public void updateEmployeesFullNameList () {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePicker.selectedDateProperty().get());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int daysOfMonthNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthNumber);
        String lastDayOfMonth = dateFormat.format(calendar.getTime());

        tableViewController.getEmployeesFullNameList().clear();
        tableViewController.getInitialAttendanceDataMap().clear();
        tableViewController.getEmployeesFullNameList().addAll(ODBC_PubsBD.selectEmployeesFullName(firstDayOfMonth, lastDayOfMonth));

        updateDateInTableHeader (calendar);
        tableViewController.initCheckBox();
        tableViewController.initAttendanceDataMap(firstDayOfMonth);
        tableViewController.checkEmployeesAttendance();

        previousMonth = datePicker.selectedDateProperty().get().getMonth();
        previousYear = datePicker.selectedDateProperty().get().getYear();
    }

    public void updateDateInTableHeader (Calendar calendar) {
//        tableViewController.getCalendar().setTime(datePicker.selectedDateProperty().get());
        DateFormat dateFormatForText = new SimpleDateFormat("dd.MM");
        DateFormat dateFormatForId = new SimpleDateFormat("yyyy-MM-dd");

        ObservableList<TableColumn<T, ?>> tableColumns = tableViewController.getTableView().getColumns();
        int i = 0;

        int thisMonth = calendar.get(Calendar.MONTH);

        Calendar dayIncrement = calendar;
        dayIncrement.set(Calendar.DAY_OF_MONTH, 1);

        for (TableColumn<T, ?> C: tableColumns) {
            if (i > 0) {
                C.getStyleClass().remove("disable");
                C.setText(dateFormatForText.format(dayIncrement.getTime()));
                C.setId(dateFormatForId.format(dayIncrement.getTime()));
                if (dayIncrement.get(Calendar.MONTH) != thisMonth) {
                    C.getStyleClass().add("disable");
                }
                dayIncrement.add(Calendar.DAY_OF_MONTH, 1);
            }
            i++;
        }
    }

    public TableViewController getTableViewController() {
        return tableViewController;
    }
}
