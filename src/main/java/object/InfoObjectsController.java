package object;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import object.dto.DTOObject;

public class InfoObjectsController {

    public TextField addressTextField;
    public DatePicker startDateDatePicker;
    public DatePicker finishDateDatePicker;
    public TextField costTextField;
    public Button rejectFinishDateButton;
    public TextArea notesTextArea;
    private DTOObject dtoObject;
    private WindowObjectsController windowObjectsController;

    public void initWindow(){
        initSaveButton();
        initRejectBirthDateButton();
        setNotesTextAreaListener();
    }

    public void initSaveButton(){
        windowObjectsController.getSaveButton().setVisible(true);
        windowObjectsController.getSaveButton().setOnAction(event -> {

        });
    }

    private void setDateToControls() {
        addressTextField.setText(dtoObject.getAddress());
        startDateDatePicker.setValue(dtoObject.getStartDate());
        if (dtoObject.getFinishDate() != null) {
            finishDateDatePicker.setValue(dtoObject.getFinishDate());
        }
        if (dtoObject.getEstimatedCost() != null) {
            costTextField.setText(dtoObject.getEstimatedCost().toString());
        }
        notesTextArea.setText(dtoObject.getNotes());
    }

    private void initRejectBirthDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectFinishDateButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        rejectFinishDateButton.setGraphic(new ImageView(image));
        rejectFinishDateButton.setTooltip(new Tooltip("Відмінити дату"));
        rejectFinishDateButton.setOnAction(event -> finishDateDatePicker.setValue(null));
    }

    private void setNotesTextAreaListener(){
        notesTextArea.setTooltip(new Tooltip("Тут Ви можете написати будь-які нотатки"));
        notesTextArea.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                if (notesTextArea.getText().length() > 500) {
                    String text = notesTextArea.getText().substring(0, 500);
                    notesTextArea.setText(text);
                }
            }
        });
    }

    public void setWindowObjectsController(WindowObjectsController windowObjectsController) {
        this.windowObjectsController = windowObjectsController;
    }

    public void setDtoObject(DTOObject dtoObject) {
        this.dtoObject = dtoObject;
        setDateToControls();
    }

}
