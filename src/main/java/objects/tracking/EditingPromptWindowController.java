package objects.tracking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.tracking.dto.DTOObjectEmployees;
import objects.tracking.dto.DTOObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import static objects.tracking.ODBC_PubsBD.*;

public class EditingPromptWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditingPromptWindowController.class);

    private DTOObjectEmployees dtoObjectEmployees;
    private DTOObjects dtoObjects;

    public Group rootGroup;
    public Label employeeLabel;
    public Label objectLabel;
    public DatePicker startDatePicker;
    public DatePicker finishDatePicker;
    public Button cancelButton;
    public Button saveButton;
    public Button rejectDateButton;

    private WindowObjectsTrackingController windowObjectsTrackingController;

    @FXML
    public void initialize(){
        startDatePicker.setOnMouseEntered(event -> setStartDatePickerValidation());
        finishDatePicker.setOnMouseEntered(event -> setFinishDatePickerValidation());
        initRejectDateButton();
    }

    private void initRejectDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectDateButton.getStylesheets().add(getClass().getResource("/objects.tracking/RejectButtonStyle.css").toExternalForm());
        rejectDateButton.setGraphic(new ImageView(image));
        rejectDateButton.setTooltip(new Tooltip("Відмінити дату"));
        rejectDateButton.setOnAction(event -> finishDatePicker.setValue(null));
    }

    public void setStartDatePickerValidation(){
        LocalDate minStartDate = determineMinStartDate();
        LocalDate maxStartDate = determineMaxStartDate();

        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        // Show Weekends in blue color
//                        DayOfWeek day = DayOfWeek.from(item);
//                        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
//                            this.setTextFill(Color.BLUE);
//                        }
                        if (item.isBefore(minStartDate) || (maxStartDate != null && item.isAfter(maxStartDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };

        startDatePicker.setDayCellFactory(dayCellFactory);
    }

    public void setFinishDatePickerValidation(){
        LocalDate minFinishDate = determineMinFinishDate();
        LocalDate maxFinishDate = determineMaxFinishDate();
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (item.isBefore(minFinishDate) ||
                                (maxFinishDate != null && item.compareTo(maxFinishDate) >= 0)) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };

        finishDatePicker.setDayCellFactory(dayCellFactory);
    }

    private LocalDate determineMinStartDate(){
        LocalDate minStartDate = dtoObjects.getStartDate();
        LocalDate lastObjEmpFinishDate =
                selectLastObjEmpFinishDate(dtoObjectEmployees.getEmployeeId(), startDatePicker.getValue());
        if (lastObjEmpFinishDate != null && lastObjEmpFinishDate.compareTo(minStartDate) >= 0) {
            minStartDate = lastObjEmpFinishDate.plusDays(1);
        }
        return minStartDate;
    }

    private LocalDate determineMaxStartDate(){
        LocalDate maxStartDate = null;
        if (finishDatePicker.getValue() != null) {
            maxStartDate = finishDatePicker.getValue();
        }
        LocalDate minWorkDate = selectMinWorkDate(dtoObjectEmployees.getId());
        if (maxStartDate != null && minWorkDate != null && minWorkDate.isBefore(maxStartDate)) {
            maxStartDate = minWorkDate;
        } else if (maxStartDate == null && minWorkDate != null) {
            maxStartDate = minWorkDate;
        }
        return maxStartDate;
    }

    private LocalDate determineMinFinishDate(){
        LocalDate minFinishDate = determineMinStartDate();
        if (startDatePicker != null) {
            minFinishDate = startDatePicker.getValue();
        }
        LocalDate maxWorkDate = selectMaxWorkDate(dtoObjectEmployees.getId());
        if (maxWorkDate != null && minFinishDate.isBefore(maxWorkDate)) {
            minFinishDate = maxWorkDate;
        }
        return minFinishDate;
    }

    private LocalDate determineMaxFinishDate(){
        LocalDate nextObjEmpStartDate =
                selectNextObjEmpStartDate(dtoObjectEmployees.getEmployeeId(), startDatePicker.getValue());
        if (nextObjEmpStartDate == null) {
            return null;
        }
        return nextObjEmpStartDate;
    }

    public void escape(ActionEvent actionEvent) {
        close();
    }

    public void save(ActionEvent actionEvent) {
        dtoObjectEmployees.setStartDate(Date.from(startDatePicker.getValue().
                atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dtoObjectEmployees.initFormatStartDate();
        if (finishDatePicker.getValue() != null) {
            dtoObjectEmployees.setFinishDate(Date.from(finishDatePicker.getValue().
                    atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            dtoObjectEmployees.setFinishDate(null);
        }
        dtoObjectEmployees.initFormatFinishDate();
        updateObjectEmployees(dtoObjectEmployees);
        windowObjectsTrackingController.initTableView(windowObjectsTrackingController.getDtoObjectEmployeesList(),
                objectLabel.getText());
        if (! windowObjectsTrackingController.getEmployeesListViewController().isAllEmployees()) {
            windowObjectsTrackingController.getEmployeesListViewController().initList();
        }
        close();
    }

    public void close(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
//        menuTableView.initTableView();
        stage.close();
    }

    public void initShortcuts(){
        cancelButton.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> cancelButton.fire());
        saveButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> saveButton.fire());
    }

    public void setDtoObjectEmployees(DTOObjectEmployees dtoObjectEmployees) {
        this.dtoObjectEmployees = dtoObjectEmployees;
        setControlsValues();
    }

    private void setControlsValues(){
        employeeLabel.setText(dtoObjectEmployees.getFullName());

        startDatePicker.setValue(dtoObjectEmployees.getStartDate().
                toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if (dtoObjectEmployees.getFinishDate() != null) {
            finishDatePicker.setValue(dtoObjectEmployees.getFinishDate().
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        dtoObjects = selectObject(dtoObjectEmployees.getObjectId());
        objectLabel.setText(dtoObjects.getAddress());
    }

    public void setWindowObjectsTrackingController(WindowObjectsTrackingController windowObjectsTrackingController) {
        this.windowObjectsTrackingController = windowObjectsTrackingController;
    }

}
