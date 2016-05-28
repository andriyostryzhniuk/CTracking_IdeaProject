package overridden.elements.date.picker;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Calendar;


/**
 * This is the main stack photosPane, which holds the three views ({@link YearView}, {@link DecadesView}.
 * It is responsible for showing and hiding them.
 *
 */
final class MainStackPane extends StackPane {

    private AnimatedStackPane animatedStackPaneYearView;
    private AnimatedStackPane animatedStackPaneDecadesView;
    private CalendarView calendarView;
    private YearView yearView1;
    private YearView yearView2;
    private DecadesView decadesView1;
    private DecadesView decadesView2;


    public MainStackPane(final CalendarView calendarView) {

        this.calendarView = calendarView;

        yearView1 = new YearView(calendarView);
        yearView2 = new YearView(calendarView);

        decadesView1 = new DecadesView(calendarView);
        decadesView2 = new DecadesView(calendarView);

        animatedStackPaneYearView = new AnimatedStackPane(yearView1, yearView2);
        animatedStackPaneDecadesView = new AnimatedStackPane(decadesView1, decadesView2);

        getChildren().addAll(animatedStackPaneYearView, animatedStackPaneDecadesView);

        calendarView.title.bind(animatedStackPaneYearView.actualPane.titleProperty());

        animatedStackPaneDecadesView.setVisible(false);

        // When the view changes.
        calendarView.currentlyViewing.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {

                calendarView.title.unbind();
                switch (oldNumber.intValue()) {
                    case Calendar.YEAR:
                        switch (newNumber.intValue()) {
                            // Switch from decades to month.
                            case Calendar.ERA:
                                showOrHide(animatedStackPaneDecadesView, true);
                                calendarView.title.bind(animatedStackPaneDecadesView.actualPane.titleProperty());
                                break;
                        }
                        break;
                    case Calendar.ERA:
                        switch (newNumber.intValue()) {
                            // Switch from decades to year.
                            case Calendar.YEAR:
                                showOrHide(animatedStackPaneDecadesView, false);
                                calendarView.title.bind(animatedStackPaneYearView.actualPane.titleProperty());
                                break;

                        }
                        break;
                }
            }
        });
    }

    /**
     * Shows or hides the photosPane with an animation.
     *
     * @param stackPane The StackPane, which is shown or hidden.
     * @param show      True, when shown, false when hidden.
     */
    private void showOrHide(final AnimatedStackPane stackPane, final boolean show) {
        stackPane.setVisible(true);

        calendarView.ongoingTransitions.set(calendarView.ongoingTransitions.get() + 1);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), stackPane);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), stackPane);

        setClip(new Rectangle(stackPane.getBoundsInLocal().getWidth(), stackPane.getBoundsInLocal().getHeight()));

        if (show) {
            translateTransition.setFromY(-getBoundsInLocal().getHeight());
            translateTransition.setToY(0);
            fadeTransition.setToValue(1);
            fadeTransition.setFromValue(0);

        } else {
            translateTransition.setToY(-getBoundsInLocal().getHeight());
            translateTransition.setFromY(0);
            fadeTransition.setToValue(0);
            fadeTransition.setFromValue(1);
        }

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().add(translateTransition);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.play();
        parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if (!show) {
                    calendarView.mainNavigationPane.titleButton.requestFocus();
                    stackPane.setVisible(false);
                }
                calendarView.ongoingTransitions.set(calendarView.ongoingTransitions.get() - 1);
                if (calendarView.ongoingTransitions.get() == 0) {
                    setClip(null);
                }
            }
        });
    }

    public void updateContent(){
        yearView1.updateContent();
        decadesView1.updateContent();
    }
}
