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
import stock.tracking.dto.DtoStock;

import java.time.LocalDate;

import static stock.tracking.ODBC_PubsBDForLiable.*;
import static stock.tracking.ODBC_PubsBDForStock.selectStockName;

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
    public DatePicker givingDatePicker;
    public DatePicker returningDatePicker;
    public Button cancelButton;
    public Button saveButton;
    public Button rejectDateButton;

    private WindowStockTrackingController windowStockTrackingController;

    @FXML
    public void initialize(){
        givingDatePicker.setOnMouseEntered(event -> setGivingDatePickerValidation());
        returningDatePicker.setOnMouseEntered(event -> setReturningDatePickerValidation());
        initRejectDateButton();
    }

    private void initRejectDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectDateButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        rejectDateButton.setGraphic(new ImageView(image));
        rejectDateButton.setTooltip(new Tooltip("Відмінити дату"));
        rejectDateButton.setOnAction(event -> returningDatePicker.setValue(null));
    }

    public void setGivingDatePickerValidation(){
        LocalDate minGivingDate = determineMinGivingDate();
        LocalDate maxGivingDate = determineMaxGivingDate();

        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(minGivingDate) || (maxGivingDate != null && item.isAfter(maxGivingDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };

        givingDatePicker.setDayCellFactory(dayCellFactory);
    }

    public void setReturningDatePickerValidation(){
        LocalDate minReturningDate = determineMinReturningDate();
        LocalDate maxReturningDate = determineMaxReturningDate();
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (item.isBefore(minReturningDate) ||
                                (maxReturningDate != null && item.compareTo(maxReturningDate) >= 0)) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };

        returningDatePicker.setDayCellFactory(dayCellFactory);
    }

    private LocalDate determineMinGivingDate(){
        LocalDate minGivingDate;
        if (dtoObjects != null) {
            minGivingDate = dtoObjects.getStartDate();
        } else {
            minGivingDate = dtoEmployeesFullInfo.getFirstDate();
        }

        LocalDate lastStockUsingDate =
                selectLastStockUsingDate(dtoStockTracking.getStockId(), givingDatePicker.getValue());
        if (lastStockUsingDate != null && lastStockUsingDate.compareTo(minGivingDate) >= 0) {
            minGivingDate = lastStockUsingDate.plusDays(1);
        }

        return minGivingDate;
    }

    private LocalDate determineMaxGivingDate(){
        LocalDate maxGivingDate;
        if (returningDatePicker.getValue() != null) {
            maxGivingDate = returningDatePicker.getValue();
        } else if (dtoObjects != null) {
            maxGivingDate = dtoObjects.getFinishDate();
        } else {
            maxGivingDate = dtoEmployeesFullInfo.getLastDate();
        }

        LocalDate nextStockUsingDate =
                selectNextStockUsingDate(dtoStockTracking.getStockId(), dtoStockTracking.getGivingDate());
        if (maxGivingDate != null && nextStockUsingDate != null && nextStockUsingDate.isBefore(maxGivingDate)) {
            maxGivingDate = nextStockUsingDate.minusDays(1);
        } else if (maxGivingDate == null && nextStockUsingDate != null) {
            maxGivingDate = nextStockUsingDate.minusDays(1);
        }
        return maxGivingDate;
    }

    private LocalDate determineMinReturningDate(){
        LocalDate minReturningDate = determineMinGivingDate();
        if (givingDatePicker != null) {
            minReturningDate = givingDatePicker.getValue();
        }
        return minReturningDate;
    }

    private LocalDate determineMaxReturningDate(){
        LocalDate maxReturningDate;
        if (dtoObjects != null) {
            maxReturningDate = dtoObjects.getFinishDate();
        } else {
            maxReturningDate = dtoEmployeesFullInfo.getLastDate();
        }

        LocalDate nextStockUsingDate =
                selectNextStockUsingDate(dtoStockTracking.getStockId(), dtoStockTracking.getGivingDate());
        if (maxReturningDate != null && nextStockUsingDate != null && nextStockUsingDate.isBefore(maxReturningDate)) {
            maxReturningDate = nextStockUsingDate;
        } else if (maxReturningDate == null && nextStockUsingDate != null) {
            maxReturningDate = nextStockUsingDate;
        }
        return maxReturningDate;
    }

    public void escape(ActionEvent actionEvent) {
        close(false);
    }

    public void save(ActionEvent actionEvent) {
        dtoStockTracking.setGivingDate(givingDatePicker.getValue());
        dtoStockTracking.setReturnDate(returningDatePicker.getValue());

        if (toUpdate) {
            updateStockTracking(dtoStockTracking);
        } else {
            insertIntoStockTracking(dtoStockTracking);
        }

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
        DtoStock dtoStock = selectStockName(dtoStockTracking.getStockId());
        stockNameLabel.setText(dtoStock.getName());
        stockCategoryLabel.setText(dtoStock.getStockCategory());
        givingDatePicker.setValue(dtoStockTracking.getGivingDate());

        if (dtoStockTracking.getReturnDate() != null) {
            returningDatePicker.setValue(dtoStockTracking.getReturnDate());
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
