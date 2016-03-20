package date.picker;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Calendar;
import java.util.Date;

/**
 * The year view shows the months.
 */
final class YearView extends DatePane {

    private static final String CSS_CALENDAR_YEAR_VIEW = "calendar-year-view";
    private static final String CSS_CALENDAR_MONTH_BUTTON = "calendar-month-button";

    public YearView(final CalendarView calendarView) {
        super(calendarView);

        getStyleClass().add(CSS_CALENDAR_YEAR_VIEW);

        // When the locale changes, update the contents (month names).
        calendarView.localeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateContent();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildContent() {

        // Get the number of months. I read, there are some lunar calendars, with more than 12 months.
        int numberOfMonths = calendarView.getCalendar().getMaximum(Calendar.MONTH) + 1;

        int numberOfColumns = 3;

        for (int i = 0; i < numberOfMonths; i++) {
            final int j = i;
            Button button = new Button();
            button.getStyleClass().add(CSS_CALENDAR_MONTH_BUTTON);

            // Make the button stretch.
            button.setMinHeight(58.0);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(button, Priority.ALWAYS);
            GridPane.setHgrow(button, Priority.ALWAYS);

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (calendarView.currentlyViewing.get() == Calendar.YEAR) {
                        calendarView.getCalendar().set(Calendar.MONTH, j);
                        calendarView.currentlyViewing.set(Calendar.YEAR);
                        calendarView.selectedDate.set(calendarView.getCalendar().getTime());
                        calendarView.setOldSelectedDate(calendarView.selectedDate.get());
                    }
                }
            });
            int rowIndex = i % numberOfColumns;
            int colIndex = (i - rowIndex) / numberOfColumns;
            add(button, rowIndex, colIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateContent() {
        Date month = new Date();
        int numberOfMonths = calendarView.getCalendar().getMaximum(Calendar.MONTH) + 1;

        for (int i = 0; i < numberOfMonths; i++) {
            Button button = (Button) getChildren().get(i);
//            button.setDisable(true);
            month.setMonth(i);
            button.setText(calendarView.convertMonthName(month));
        }
        title.set(getDateFormat("yyyy").format(calendarView.getCalendar().getTime()));
    }
}
