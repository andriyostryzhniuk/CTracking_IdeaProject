package object;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import subsidiary.classes.AlertWindow;
import subsidiary.classes.EditPanel;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static object.ODBC_PubsBD.deleteFromObject;
import static object.ODBC_PubsBD.selectObjectsList;

public class WindowObjectsController<T extends DTOObject> {

    public TableView<T> tableView;
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
        initControlsPanel();
        initContextMenu();
        tableView.setPlaceholder(new Label("Покищо тут немає жодного об'єкта"));
        tableView.setItems(tableViewDateList);
        initTableView(false);
    }

    public void initTableView(boolean isNeedSelectItems){
        Integer selectedRowIndex = tableView.getSelectionModel().getSelectedIndex();
        tableViewDateList.clear();
        tableViewDateList.addAll((Collection<? extends T>)
                selectObjectsList(dateView.withDayOfMonth(1), dateView.withDayOfMonth(dateView.lengthOfMonth())));
        setListViewCellFactory();

        if (isNeedSelectItems && selectedRowIndex != -1) {
            tableView.getSelectionModel().select(selectedRowIndex);
            tableView.getFocusModel().focus(selectedRowIndex);
            tableView.scrollTo(selectedRowIndex);
            initInfoObjects(tableView.getSelectionModel().getSelectedItem());
        }

    }

    private void fillCols() {
        objectCol.setCellValueFactory(new PropertyValueFactory("address"));
        startDateCol.setCellValueFactory(new PropertyValueFactory("formatStartDate"));
        finishDateCol.setCellValueFactory(new PropertyValueFactory("formatFinishDate"));
    }

    private void initContextMenu(){
        MenuItem infoItem = new MenuItem("Переглянути об'єкт");
        infoItem.setOnAction((ActionEvent event) -> {
            initInfoObjects(tableView.getSelectionModel().getSelectedItem());
        });
        infoItem.setDisable(true);

        MenuItem deleteItem = new MenuItem("Видалити об'єкт");
        deleteItem.setOnAction((ActionEvent event) -> {
            removeRecord(tableView.getSelectionModel().getSelectedItem());
        });
        deleteItem.setDisable(true);

        MenuItem addItem = new MenuItem("Додати новий об'єкт");
        addItem.setOnAction((ActionEvent event) -> {
            initInfoObjects(new DTOObject(null, null, LocalDate.now(), null, null, null, null));
        });

        final javafx.scene.control.ContextMenu cellMenu = new javafx.scene.control.ContextMenu();
        cellMenu.getItems().addAll(infoItem, deleteItem, addItem);

        tableView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                infoItem.setDisable(true);
                deleteItem.setDisable(true);
            } else {
                infoItem.setDisable(false);
                deleteItem.setDisable(false);
            }
        });

        tableView.setContextMenu(cellMenu);
    }

    private void initDatePicker() {
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
                initTableView(false);
            }
        });

        controlsGridPane.add(datePicker, 4, 0);
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
        controlsGridPane.add(calendarButton, 4, 0);
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

    private void initInfoObjects(DTOObject item){
        removeGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/InfoObjects.fxml"));
        try {
            stackPane.getChildren().add(fxmlLoader.load());
            infoObjectsController = fxmlLoader.getController();
            infoObjectsController.setWindowObjectsController(this);
            infoObjectsController.setDtoObject(item);
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

    private void initControlsPanel(){
        initDatePicker();
        initAddButton();
        initDeleteButton();
        initOpenButton();
    }

    private void initAddButton(){
        final EditPanel editPanel = new EditPanel();
        Button addButton = editPanel.getAddButton();
        addButton.setTooltip(new Tooltip("Додати об'єкт"));
        controlsGridPane.add(addButton, 0, 0);

        addButton.setOnAction(event -> {
            initInfoObjects(new DTOObject(null, null, LocalDate.now(), null, null, null, null));
        });
    }

    private void initOpenButton(){
        final EditPanel editPanel = new EditPanel(tableView);
        Button openButton = editPanel.getOpenButton();
        openButton.setTooltip(new Tooltip("Переглянути інформацію про об'єкт"));
        openButton.setOnAction(event -> {
            initInfoObjects(tableView.getSelectionModel().getSelectedItem());
        });
        controlsGridPane.add(openButton, 2, 0);
    }

    private void initDeleteButton(){
        final EditPanel editPanel = new EditPanel(tableView);
        Button deleteButton = editPanel.getDeleteButton();
        deleteButton.setTooltip(new Tooltip("Видалити об'єкт"));
        controlsGridPane.add(deleteButton, 1, 0);

        deleteButton.setOnAction(event -> {
            removeRecord(tableView.getSelectionModel().getSelectedItem());
        });
    }

    private void removeRecord(T item) {
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }

        if (infoObjectsController != null && infoObjectsController.getDtoObject().getObjectsId() == item.getObjectsId()) {
            removeGridPaneChildren();
        }
        deleteFromObject(item);
        initTableView(false);
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }
}
