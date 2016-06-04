package object;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import object.dto.DTOObject;
import overridden.elements.date.picker.DatePicker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static object.ODBC_PubsBD.selectObjectsList;

public class WindowObjectsController<T extends DTOObject> {

    public TableView tableView;
    public TableColumn objectCol;
    public TableColumn startDateCol;
    public TableColumn finishDateCol;

    public GridPane controlsGridPane;
    public StackPane stackPane;

    private Button saveButton;

    private DatePicker datePicker = new DatePicker();
    private int previousMonth;
    private int previousYear;
    private LocalDate dateView;

    private InfoObjectsController infoObjectsController;

    private ObservableList<T> tableViewDateList = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        fillCols();
        initDatePicker();
        tableView.setPlaceholder(new Label("Покищо тут немає жодного об'єкта"));
        tableView.setItems(tableViewDateList);
        initTableView();
    }

    private void initTableView(){
        tableViewDateList.clear();
        tableViewDateList.addAll((Collection<? extends T>)
                selectObjectsList(dateView.withDayOfMonth(1), dateView.withDayOfMonth(dateView.lengthOfMonth())));
        setListViewCellFactory();
    }

    private void fillCols() {
        objectCol.setCellValueFactory(new PropertyValueFactory("address"));
        startDateCol.setCellValueFactory(new PropertyValueFactory("formatStartDate"));
        finishDateCol.setCellValueFactory(new PropertyValueFactory("formatFinishDate"));
    }

    public void initDatePicker() {
        datePicker.setTooltipText("Період перегляду");

        datePicker.getCalendarView().setStartDateObject(null);
        datePicker.getCalendarView().setFinishDateObject(null);

        datePicker.setLocale(new Locale("uk"));
        datePicker.getCalendarView().setShowWeeks(false);

        datePicker.setSelectedDate(new Date());
        datePicker.deselect();
        datePicker.setLastSelectedDate(new Date());

        setDatePickerValue();

        datePicker.selectedDateProperty().addListener(observable -> {
            boolean isException = false;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            try {
                dateFormat.format(datePicker.selectedDateProperty().get());
            } catch (NullPointerException e) {
                isException = true;
            }

            if (isException == false && previousMonth != datePicker.selectedDateProperty().get().getMonth() ||
                    isException == false && previousYear != datePicker.selectedDateProperty().get().getYear() ) {

                setDatePickerValue();
                initTableView();
            }
        });

        controlsGridPane.add(datePicker, 1, 0);
        initCalendarButton();
    }

    private void setDatePickerValue(){
        if (datePicker.selectedDateProperty().get() == null) {
            previousMonth = datePicker.getLastSelectedDate().getMonth();
            previousYear = datePicker.getLastSelectedDate().getYear();
            dateView = datePicker.getLastSelectedDate().
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            previousMonth = datePicker.selectedDateProperty().get().getMonth();
            previousYear = datePicker.selectedDateProperty().get().getYear();
            dateView = datePicker.selectedDateProperty().get().
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    private void initCalendarButton(){
        Button calendarButton = datePicker.getCalendarButton();
        controlsGridPane.add(calendarButton, 1, 0);
        controlsGridPane.setMargin(calendarButton, new Insets(1, 2, 2, 2));
        controlsGridPane.setHalignment(calendarButton, HPos.RIGHT);
    }

    private void setListViewCellFactory(){
        tableView.setRowFactory(tableRow -> {
            final TableRow<T> row = new TableRow<T>() {
                @Override
                protected void updateItem(T t, boolean b) {
                    super.updateItem(t, b);
                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                initInfoObjects(getItem());
                            }
                        }
                    });
                }
            };
            return row;
        });
    }

    private void initInfoObjects(DTOObject dtoObject){
        removeGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/InfoObjects.fxml"));
        try {
            stackPane.getChildren().add(fxmlLoader.load());
            infoObjectsController = fxmlLoader.getController();
            infoObjectsController.setWindowObjectsController(this);
            infoObjectsController.setDtoObject(dtoObject);
            infoObjectsController.initWindow();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private void removeGridPaneChildren() {
        saveButton.setVisible(false);
        stackPane.getChildren().clear();
        infoObjectsController = null;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }
}
