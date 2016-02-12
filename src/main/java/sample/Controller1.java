package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import sample.dto.DtoEmployeesFullName;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andriy on 01/30/2016.
 */
public class Controller1 {

    @FXML
    public BorderPane rootBorderPane;

//    @FXML
//    private Controller2 controller2;

    public date.picker.DatePicker datePicker;

    private int previousMonth;
    private int previousYear;
    private Controller3 controller3;

    @FXML
    private void initialize() {

        initDatePicker();

        System.out.println(getDatePickerValue());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/forController3.fxml"));
        try {
            rootBorderPane.setCenter(fxmlLoader.load());
            controller3 = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

//        initContent();
//    }

    public void initContent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EmployeesWorkTrackingContent.fxml"));
        try {
            rootBorderPane.setCenter(fxmlLoader.load());
            Controller2 controller2 = fxmlLoader.getController();
            controller2.setDatePicker(datePicker);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
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

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(datePicker.selectedDateProperty().get());

                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

                    int daysOfMonthNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    String firstDayOfMonth = dateFormat2.format(calendar.getTime());
                    calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthNumber);
                    String lastDayOfMonth = dateFormat2.format(calendar.getTime());

                    controller3.getEmployeesFullNameList().clear();
                    controller3.getEmployeesFullNameList().addAll(ODBC_PubsBD.selectEmployeesFullName(firstDayOfMonth, lastDayOfMonth));
//                    rootBorderPane.setCenter(null);
//                    initContent();

                    previousMonth = datePicker.selectedDateProperty().get().getMonth();
                    previousYear = datePicker.selectedDateProperty().get().getYear();
                }
            }
        });

        rootBorderPane.setTop(datePicker);
        rootBorderPane.setAlignment(rootBorderPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(datePicker, new Insets(0.0, 0.0, 20.0, 0.0));
    }

    public Date getDatePickerValue() {
        return datePicker.selectedDateProperty().get();
    }
}
