package objects.tracking;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import objects.tracking.dto.DTOObjectEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZoneId;
import java.util.Date;

import static objects.tracking.ODBC_PubsBD.selectObjectName;

public class EditingPromptWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditingPromptWindowController.class);

    private DTOObjectEmployees dtoObjectEmployees;

    public Group rootGroup;
    public Label employeeLabel;
    public Label objectLabel;
    public DatePicker startDateDatePicker;
    public DatePicker finishDateDatePicker;
    public Button cancelButton;
    public Button saveButton;

    private WindowObjectsTrackingController windowObjectsTrackingController;

    public void escape(ActionEvent actionEvent) {
        close();
    }

    public void save(ActionEvent actionEvent) {
        dtoObjectEmployees.setStartDate(Date.from(startDateDatePicker.getValue().
                atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dtoObjectEmployees.initFormatStartDate();
        if (finishDateDatePicker.getValue() != null) {
            dtoObjectEmployees.setFinishDate(Date.from(finishDateDatePicker.getValue().
                    atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            dtoObjectEmployees.setFinishDate(null);
        }
        dtoObjectEmployees.initFormatFinishDate();
        windowObjectsTrackingController.getUpdateResultList().add(dtoObjectEmployees);
        windowObjectsTrackingController.initTableView(windowObjectsTrackingController.getDtoObjectEmployeesList());
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

        startDateDatePicker.setValue(dtoObjectEmployees.getStartDate().
                toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if (dtoObjectEmployees.getFinishDate() != null) {
            finishDateDatePicker.setValue(dtoObjectEmployees.getFinishDate().
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        objectLabel.setText(selectObjectName(dtoObjectEmployees.getObjectId()));
    }

    public void setWindowObjectsTrackingController(WindowObjectsTrackingController windowObjectsTrackingController) {
        this.windowObjectsTrackingController = windowObjectsTrackingController;
    }
}
