package attendance.tracking;

import attendance.tracking.dto.DtoObject;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import attendance.tracking.dto.DtoEmployeesFullName;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import overridden.elements.date.picker.DatePicker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.IntStream;

public class WindowAttendanceController<T extends DtoEmployeesFullName> {

    @FXML
    public BorderPane rootBorderPane;
    public DatePicker datePicker;
    public ComboBox comboBox;

    private int previousMonth;
    private int previousYear;
    private Date previousDate;
    private TableViewController tableViewController;
    private GridPane topGridPane = new GridPane();
    public ComboBox comboBoxListener = new ComboBox();

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxSearchListener = new ComboBox();

    private ObservableMap<String, DtoObject> objectMap = FXCollections.observableHashMap();

    @FXML
    private void initialize() {

        datePicker = initDatePicker();
        topGridPane.add(datePicker, 0, 0);

        Label label = new Label("Вибрати об'єкт:");
        topGridPane.add(label, 1, 0);
        topGridPane.setMargin(label, new Insets(0, 10, 0, 80));

        comboBox = initComboBox();
        topGridPane.add(comboBox, 2, 0);

        Label searchingLabel = new Label("Пошук:");
        topGridPane.add(searchingLabel, 3, 0);
        topGridPane.setMargin(searchingLabel, new Insets(0, 10, 0, 150));

        initComboBoxSearch();
        topGridPane.add(comboBoxSearch, 4, 0);

        rootBorderPane.setTop(topGridPane);
        rootBorderPane.setAlignment(topGridPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(topGridPane, new Insets(0.0, 0.0, 20.0, 0.0));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/attendance/tracking/TableView.fxml"));
        try {
            rootBorderPane.setCenter(fxmlLoader.load());
            tableViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        updateEmployeesFullNameList();
    }

    public DatePicker initDatePicker() {

        DatePicker datePicker = new DatePicker();
        datePicker.setTooltipText("Період виконання робіт");

        datePicker.getCalendarView().setStartDateObject(null);
        datePicker.getCalendarView().setFinishDateObject(null);

        datePicker.setLocale(new Locale("uk"));
        datePicker.getCalendarView().setShowWeeks(false);

        datePicker.setSelectedDate(new Date());
        datePicker.deselect();
        datePicker.setLastSelectedDate(new Date());

        if (datePicker.selectedDateProperty().get() == null) {
            previousMonth = datePicker.getLastSelectedDate().getMonth();
            previousYear = datePicker.getLastSelectedDate().getYear();
            previousDate = datePicker.getLastSelectedDate();
        } else {
            previousMonth = datePicker.selectedDateProperty().get().getMonth();
            previousYear = datePicker.selectedDateProperty().get().getYear();
            previousDate = datePicker.selectedDateProperty().get();
        }

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

                fillComboBoxItems (comboBox);

                Object comboBoxValue = comboBox.getValue();
                if (comboBox.getItems().size() == 1) {
                    Label notificationLabel = new Label("Не знайдено жодного об'єкта за даний період");
                    notificationLabel.setStyle("-fx-text-fill: red;");
                    topGridPane.add(notificationLabel, 5, 0);
                    topGridPane.setMargin(notificationLabel, new Insets(0, 0, 0, 30));
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                topGridPane.getChildren().remove(notificationLabel);
                            });
                        }
                    }, 3000, 1000);

                    datePicker.setSelectedDate(previousDate);
                    fillComboBoxItems (comboBox);

                } else if (!comboBoxValue.equals("Всі об'єкти")) {
                    if (comboBox.getItems().indexOf(comboBoxValue) == -1) {
                        comboBox.setValue(comboBox.getItems().get(0));
                        comboBoxListener.setValue(comboBox.getValue());

                        Label notificationLabel = new Label("На вибраному об'єкті не проводилось робіт за даний період");
                        notificationLabel.setStyle("-fx-text-fill: red;");
                        topGridPane.add(notificationLabel, 5, 0);
                        topGridPane.setMargin(notificationLabel, new Insets(0, 0, 0, 30));
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    topGridPane.getChildren().remove(notificationLabel);
                                });
                            }
                        }, 3000, 1000);
                    } else {
                        updateEmployeesFullNameList();
                    }
                } else {
                    updateEmployeesFullNameList();
                }
            }
        });
        return datePicker;
    }

    public ComboBox initComboBox(){
        ComboBox comboBox = new ComboBox();
        Tooltip.install(comboBox, new Tooltip("Вибрати об'єкт"));

        comboBox.getStylesheets().add(getClass().getResource("/attendance/tracking/ComboBoxStyle.css").toExternalForm());

        comboBoxListener.setValue("Всі об'єкти");

        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(event -> {
//                                mouse pressed
                            comboBox.getStyleClass().remove("warning");
                            comboBoxListener.setValue(comboBox.getValue());
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        comboBoxListener.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            comboBox.getStyleClass().remove("warning");
            new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);
            java.sql.Date startDateObject = null;
            java.sql.Date finishDateObject = null;

            if (!comboBox.getValue().equals("Всі об'єкти")){
                startDateObject = objectMap.get(comboBox.getValue().toString()).getStartDate();
                finishDateObject = objectMap.get(comboBox.getValue().toString()).getFinishDate();
            }
            datePicker.getCalendarView().setStartDateObject(startDateObject);
            datePicker.getCalendarView().setFinishDateObject(finishDateObject);
            datePicker.getCalendarView().updateContent();
            updateEmployeesFullNameList();
        });
        fillComboBoxItems(comboBox);
        return comboBox;
    }

    public void updateEmployeesFullNameList () {
        Calendar calendar = Calendar.getInstance();

        if (datePicker.selectedDateProperty().get() == null) {
            calendar.setTime(datePicker.getLastSelectedDate());
        } else {
            calendar.setTime(datePicker.selectedDateProperty().get());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int daysOfMonthNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthNumber);
        String lastDayOfMonth = dateFormat.format(calendar.getTime());

        tableViewController.getEmployeesFullNameList().clear();
        tableViewController.getInitialAttendanceDataMap().clear();
        Object comboBoxValue = comboBox.getValue();
        if (comboBoxValue.equals("Всі об'єкти")) {
            tableViewController.setObjectId(-1);
            tableViewController.getEmployeesFullNameList().
                    addAll(ODBC_PubsBD.selectEmployeesFullNameOnAnyObject(firstDayOfMonth, lastDayOfMonth));
        } else {
            int objectId = objectMap.get(comboBoxValue.toString()).getId();
            tableViewController.setObjectId(objectId);
            tableViewController.getEmployeesFullNameList().
                    addAll(ODBC_PubsBD.selectEmployeesFullNameOnSomeObject(firstDayOfMonth, lastDayOfMonth, objectId));
        }

        updateDateInTableHeader (calendar);
        tableViewController.initCheckBox();
        tableViewController.initAttendanceDataMap(firstDayOfMonth);
        tableViewController.checkEmployeesAttendance();

        int i = 0;
        for (Object item : tableViewController.getEmployeesFullNameList()) {
            DtoEmployeesFullName employeesFullName = (DtoEmployeesFullName)item;
            employeesFullName.getSumOfWorkingHoursLabel().setText(tableViewController.sumWorkingHours(i).toString());
            employeesFullName.setGridPaneFullName(employeesFullName.initGridPaneFullName());
            i++;
        }

        if (datePicker.selectedDateProperty().get() == null) {
            previousMonth = datePicker.getLastSelectedDate().getMonth();
            previousYear = datePicker.getLastSelectedDate().getYear();
            previousDate = datePicker.getLastSelectedDate();
        } else {
            previousMonth = datePicker.selectedDateProperty().get().getMonth();
            previousYear = datePicker.selectedDateProperty().get().getYear();
            previousDate = datePicker.selectedDateProperty().get();
        }

        setItemsToComboBoxSearch();
    }

    public void updateDateInTableHeader (Calendar calendar) {
//        tableViewController.getCalendar().setTime(datePicker.selectedDateProperty().get());

        java.sql.Date startDateObject = null;
        java.sql.Date finishDateObject = null;

        if (!comboBox.getValue().equals("Всі об'єкти")){
            startDateObject = objectMap.get(comboBox.getValue().toString()).getStartDate();
            finishDateObject = objectMap.get(comboBox.getValue().toString()).getFinishDate();
        }

        DateFormat dateFormatForText = new SimpleDateFormat("dd.MM");
        DateFormat dateFormatForId = new SimpleDateFormat("yyyy-MM-dd");

        ObservableList<TableColumn<T, ?>> tableColumns = tableViewController.getTableView().getColumns();
        int i = 0;

        int thisMonth = calendar.get(Calendar.MONTH);

        Calendar dayIncrement = calendar;
        dayIncrement.set(Calendar.DAY_OF_MONTH, 1);

        for (TableColumn<T, ?> tableColumn: tableColumns) {
            if (i > 0) {
                IntStream.range(0, tableColumn.getStyleClass().size()).forEach(j ->{
                    if (tableColumn.getStyleClass().get(j).equals("disable")) {
                        tableColumn.getStyleClass().remove(j);
                    }
                });

                tableColumn.setText(dateFormatForText.format(dayIncrement.getTime()));
                tableColumn.setId(dateFormatForId.format(dayIncrement.getTime()));

                if (startDateObject != null) {
                    if (dayIncrement.getTime().before(startDateObject)) {
                        tableColumn.getStyleClass().add("disable");
                    }
                }
                if(finishDateObject != null) {
                    if (dayIncrement.getTime().after(finishDateObject)) {
                        tableColumn.getStyleClass().add("disable");
                    }
                }
                if (dayIncrement.get(Calendar.MONTH) != thisMonth) {
                    tableColumn.getStyleClass().add("disable");
                }

//                if (dayIncrement.get(Calendar.MONTH) != thisMonth) {
//                    tableColumn.getStyleClass().addButtonAction("disable");
//                }
                dayIncrement.add(Calendar.DAY_OF_MONTH, 1);
            }
            i++;
        }
    }

    public TableViewController getTableViewController() {
        return tableViewController;
    }

    public void fillComboBoxItems (ComboBox comboBox) {
        Calendar calendar = Calendar.getInstance();

        if (datePicker.selectedDateProperty().get() == null) {
            calendar.setTime(datePicker.getLastSelectedDate());
        } else {
            calendar.setTime(datePicker.selectedDateProperty().get());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int daysOfMonthNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthNumber);
        String lastDayOfMonth = dateFormat.format(calendar.getTime());

        Object lastComboBoxValue = comboBoxListener.getValue();
        comboBox.getItems().clear();
        objectMap.clear();
        comboBox.getItems().add("Всі об'єкти");
        ODBC_PubsBD.selectObjectList(firstDayOfMonth, lastDayOfMonth).forEach(item -> {
            objectMap.put(item.getAddress(), item);
            comboBox.getItems().add(item.getAddress());
        });

        if(lastComboBoxValue == null) {
            comboBox.setValue(comboBox.getItems().get(0));
        } else {
            comboBox.setValue(lastComboBoxValue);
        }
        comboBox.getStyleClass().remove("warning");
        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);
    }

    public void initComboBoxSearch() {
        comboBoxSearch.setMinWidth(200);
        comboBoxSearch.setMaxWidth(200);
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");

//        setItemsToComboBoxSearch();

        comboBoxSearch.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() { {
                    super.setOnMousePressed((MouseEvent event) -> {
                        comboBoxSearchListener.setValue(comboBoxSearch.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        comboBoxSearchListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                comboBoxSearch.getStyleClass().remove("warning");
                searchInTableView();
                comboBoxSearchListener.setValue(null);
            }
        });
    }

    public void searchInTableView() {
        Object comboBoxListenerValue = comboBoxSearchListener.getValue();

        int i = 0;
        for (DtoEmployeesFullName item : (Collection<? extends T>) tableViewController.getTableView().getItems()) {
            if (item.getFullName().equals(comboBoxListenerValue)) {
                tableViewController.getTableView().getSelectionModel().select(i);
                tableViewController.getTableView().getFocusModel().focus(i);
                tableViewController.getTableView().scrollTo(i);
            }
            i++;
        }
    }

    private void setItemsToComboBoxSearch(){
        ObservableList<String> employeesNamesList = FXCollections.observableArrayList();
        ObservableList<DtoEmployeesFullName> dtoEmployeesFullNameList = tableViewController.getEmployeesFullNameList();

        dtoEmployeesFullNameList.forEach(item -> employeesNamesList.add(item.getFullName()));

        comboBoxSearch.setItems(employeesNamesList);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxSearchListener);
    }
}
