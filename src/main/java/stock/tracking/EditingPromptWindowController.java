package stock.tracking;

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
import objects.tracking.dto.DTOObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stock.tracking.dto.DTOEmployeesFullInfo;
import stock.tracking.dto.DTOStockTracking;
import java.time.LocalDate;

import static stock.tracking.ODBC_PubsBDForLiable.*;

public class EditingPromptWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditingPromptWindowController.class);

    private boolean toUpdate;
    private DTOStockTracking dtoStockTracking;
    private DTOEmployeesFullInfo dtoEmployeesFullInfo;
    private DTOObjects dtoObjects;

    public Group rootGroup;
    public Label stockCategoryLabel;
    public Label stockNameLabel;
    public Label employeeLabel;
    public Label objectLabel;
    public DatePicker startDatePicker;
    public DatePicker finishDatePicker;
    public Button cancelButton;
    public Button saveButton;
    public Button rejectDateButton;

    private WindowStockTrackingController windowStockTrackingController;

    @FXML
    public void initialize(){
        startDatePicker.setOnMouseEntered(event -> setStartDatePickerValidation());
//        finishDatePicker.setOnMouseEntered(event -> setFinishDatePickerValidation());
        initRejectDateButton();
    }

    private void initRejectDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectDateButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
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
                        if (item.isBefore(minStartDate) || (maxStartDate != null && item.isAfter(maxStartDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };

        startDatePicker.setDayCellFactory(dayCellFactory);
    }

//    public void setFinishDatePickerValidation(){
//        LocalDate minFinishDate = determineMinFinishDate();
//        LocalDate maxFinishDate = determineMaxFinishDate();
//        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
//            public DateCell call(final DatePicker datePicker) {
//                return new DateCell() {
//                    @Override
//                    public void updateItem(LocalDate item, boolean empty)
//                    {
//                        super.updateItem(item, empty);
//                        if (item.isBefore(minFinishDate) ||
//                                (maxFinishDate != null && item.compareTo(maxFinishDate) >= 0)) {
//                            this.setDisable(true);
//                        }
//                    }
//                };
//            }
//        };
//
//        finishDatePicker.setDayCellFactory(dayCellFactory);
//    }

    private LocalDate determineMinStartDate(){
        LocalDate minGivingDate;
        if (dtoObjects != null) {
            minGivingDate = dtoObjects.getStartDate();
        } else {
            minGivingDate = dtoEmployeesFullInfo.getFirstDate();
        }

        LocalDate lastStockUsingDate =
                selectLastStockUsingDate(dtoStockTracking.getStockId(), startDatePicker.getValue());
        if (lastStockUsingDate != null && lastStockUsingDate.compareTo(minGivingDate) >= 0) {
            minGivingDate = lastStockUsingDate.plusDays(1);
        }

        return minGivingDate;
    }

    private LocalDate determineMaxStartDate(){
        LocalDate maxGivingDate = null;
        if (finishDatePicker.getValue() != null) {
            maxGivingDate = finishDatePicker.getValue();
        }
        LocalDate nextStockUsingDate = selectNextStockUsingDate(dtoStockTracking.getStockId(), startDatePicker.getValue());
        if (maxGivingDate != null && nextStockUsingDate != null && nextStockUsingDate.isBefore(maxGivingDate)) {
            maxGivingDate = nextStockUsingDate;
        } else if (maxGivingDate == null && nextStockUsingDate != null) {
            maxGivingDate = nextStockUsingDate;
        }
        return maxGivingDate;
    }

//    private LocalDate determineMinFinishDate(){
//        LocalDate minFinishDate = determineMinStartDate();
//        if (startDatePicker != null) {
//            minFinishDate = startDatePicker.getValue();
//        }
//        LocalDate maxWorkDate = selectMaxWorkDate(dtoStockTracking.getId());
//        if (maxWorkDate != null && minFinishDate.isBefore(maxWorkDate)) {
//            minFinishDate = maxWorkDate;
//        }
//        return minFinishDate;
//    }
//
//    private LocalDate determineMaxFinishDate(){
//        LocalDate nextObjEmpStartDate =
//                selectNextObjEmpStartDate(dtoStockTracking.getEmployeeId(), startDatePicker.getValue());
//        if (nextObjEmpStartDate == null) {
//            return null;
//        }
//        return nextObjEmpStartDate;
//    }

    public void escape(ActionEvent actionEvent) {
        close(false);
    }

    public void save(ActionEvent actionEvent) {
        dtoStockTracking.setGivingDate(startDatePicker.getValue());
        dtoStockTracking.setReturnDate(finishDatePicker.getValue());

//        if (toUpdate) {
//            updateObjectEmployees(dtoStockTracking);
//        } else {
//            insertIntoObjectEmployees(dtoStockTracking);
//        }

        updateStockTracking(dtoStockTracking);

        windowStockTrackingController.initTableView(dtoStockTracking.getObjectId(), dtoStockTracking.getEmployeesId());
        windowStockTrackingController.getStockListViewController().initListView(true);

        close(true);
    }

    public void close(boolean success){
        windowStockTrackingController.setSuccessSave(success);
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        stage.close();
    }

    public void initShortcuts(){
        cancelButton.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> cancelButton.fire());
        saveButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> saveButton.fire());
    }

    public void setDtoStockTracking(DTOStockTracking dtoStockTracking) {
        this.dtoStockTracking = dtoStockTracking;
        setControlsValues();
    }

    private void setControlsValues(){
        stockNameLabel.setText(dtoStockTracking.getStockName());
        stockCategoryLabel.setText(dtoStockTracking.getStockCategory());
        startDatePicker.setValue(dtoStockTracking.getGivingDate());

        if (dtoStockTracking.getReturnDate() != null) {
            finishDatePicker.setValue(dtoStockTracking.getReturnDate());
        }

        if (dtoStockTracking.getEmployeesId() != null) {
            dtoEmployeesFullInfo = selectEmployeesFullInfo(dtoStockTracking.getEmployeesId());
            employeeLabel.setText(dtoEmployeesFullInfo.getFullName());
        }

        if (dtoStockTracking.getObjectId() != null) {
            dtoObjects = selectObject(dtoStockTracking.getObjectId());
            objectLabel.setText(dtoObjects.getAddress());
        }
    }

    public void setWindowStockTrackingController(WindowStockTrackingController windowStockTrackingController) {
        this.windowStockTrackingController = windowStockTrackingController;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }
}
