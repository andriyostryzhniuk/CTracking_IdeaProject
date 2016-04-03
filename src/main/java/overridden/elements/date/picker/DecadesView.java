package overridden.elements.date.picker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Shows the years of several decades.
 */
final class DecadesView extends DatePane {

    private static final String CSS_CALENDAR_DECADES_VIEW = "calendar-decades-view";


    private final static int NUMBER_OF_DECADES = 2;

    public DecadesView(final CalendarView calendarView) {
        super(calendarView);
        getStyleClass().add(CSS_CALENDAR_DECADES_VIEW);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildContent() {

        final Calendar calendar = calendarView.getCalendar();

        for (int i = 0; i < NUMBER_OF_DECADES * 10; i++) {

            final Button button = new Button();
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            setVgrow(button, Priority.ALWAYS);
            setHgrow(button, Priority.ALWAYS);

            button.getStyleClass().add("calendar-year-button");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (calendarView.currentlyViewing.get() == Calendar.ERA) {
                        calendar.set(Calendar.YEAR, (Integer) button.getUserData());
                        calendarView.currentlyViewing.set(Calendar.YEAR);
                        calendarView.calendarDate.set(calendar.getTime());
                    }
                }
            }

            );
            int rowIndex = i % 5;
            int colIndex = (i - rowIndex) / 5;

            add(button, rowIndex, colIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateContent() {
        final Calendar calendar = calendarView.getCalendar();

        int year = calendar.get(Calendar.YEAR);
        int a = year % 10;
        if (a < 5) {
            a += 10;
        }
        int startYear = year - a;

        java.sql.Date startDateObject = calendarView.getStartDateObject();
        java.sql.Date finishDateObject = calendarView.getFinishDateObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");

        for (int i = 0; i < NUMBER_OF_DECADES * 10; i++) {
            final int y = i + startYear;
            Button button = (Button) getChildren().get(i);

            button.getStyleClass().remove("forObject");

            if (startDateObject != null) {
                int startYearOfObject = Integer.parseInt(dateFormat.format(startDateObject));
                if (finishDateObject == null) {
                    if (y >= startYearOfObject) {
                        button.getStyleClass().add("forObject");
                    }
                } else {
                    if (y >= startYearOfObject && y <= Integer.parseInt(dateFormat.format(finishDateObject))) {
                        button.getStyleClass().add("forObject");
                    }
                }
            }

            button.setText(Integer.toString(y));
            button.setUserData(y);
        }

        title.set(String.format("%s - %s", startYear, startYear + 10 * NUMBER_OF_DECADES - 1));
    }
}
