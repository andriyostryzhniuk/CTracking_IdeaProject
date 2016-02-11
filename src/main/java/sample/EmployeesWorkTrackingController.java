package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andriy on 01/30/2016.
 */
public class EmployeesWorkTrackingController {

    @FXML
    public BorderPane rootBorderPane;

    public date.picker.DatePicker datePicker;

    private int previousMonth;
    private int previousYear;

    @FXML
    private void initialize()  {

        initDatePicker();

        System.out.println(getDatePickerValue());

        initContent();
    }

    public void initContent () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EmployeesWorkTrackingContent.fxml"));
        try {
            fxmlLoader.getController();
            rootBorderPane.setCenter(fxmlLoader.load());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void initDatePicker()  {
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

                    rootBorderPane.setCenter(null);

                    initContent();

                    previousMonth = datePicker.selectedDateProperty().get().getMonth();
                    previousYear = datePicker.selectedDateProperty().get().getYear();
                }
            }
        });

        rootBorderPane.setTop(datePicker);
        rootBorderPane.setAlignment(rootBorderPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(datePicker, new Insets(0.0, 0.0, 20.0, 0.0));
    }

    public Date getDatePickerValue(){
        return datePicker.selectedDateProperty().get();
    }
}
