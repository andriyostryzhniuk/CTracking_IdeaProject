package object;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import object.dto.DTOObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static object.ODBC_PubsBD.insertIntoObject;
import static object.ODBC_PubsBD.updateObject;

public class InfoObjectsController {

    public TextField addressTextField;
    public Label addressExceptionLabel;
    public DatePicker startDateDatePicker;
    public DatePicker finishDateDatePicker;
    public TextField costTextField;
    public Label costExceptionLabel;
    public Button rejectFinishDateButton;
    public TextArea notesTextArea;
    private DTOObject dtoObject;
    private WindowObjectsController windowObjectsController;

    public void initWindow(){
        initRejectBirthDateButton();
        setListenerToNameTextField();
        setNotesTextAreaListener();
        startDateDatePicker.setOnMouseEntered(event -> setStartDateDatePickerValidation());
        finishDateDatePicker.setOnMouseEntered(event -> setFinishDateDatePickerValidation());
        setPriceTextFieldListener();
        initSaveButton();
    }

    public void initSaveButton(){
        windowObjectsController.getSaveButton().setVisible(true);
        windowObjectsController.getSaveButton().setOnAction(event -> {
            if (addressIsEmpty() || ! textFieldMatcherFind()){
                return;
            }

            BigDecimal cost;
            if (costTextField.getText() == null || costTextField.getText().isEmpty()) {
                cost = null;
            } else if (! checkPriceTextFieldValidation()) {
                return;
            } else {
                cost = new BigDecimal(costTextField.getText());
            }

            String notes;
            if (notesTextArea.getText() == null || notesTextArea.getText().isEmpty()) {
                notes = null;
            } else {
                notes = notesTextArea.getText();
            }

            dtoObject = new DTOObject(dtoObject.getObjectsId(), addressTextField.getText(),
                    startDateDatePicker.getValue(), finishDateDatePicker.getValue(), 1, cost, notes);

            if (dtoObject.getObjectsId() != null) {
                updateObject(dtoObject);
            } else {
                dtoObject.setObjectsId(insertIntoObject(dtoObject));
            }

            windowObjectsController.initTableView(true);
        });

    }

    private void setDateToControls() {
        addressTextField.setText(dtoObject.getAddress());
        startDateDatePicker.setValue(dtoObject.getStartDate());
        finishDateDatePicker.setValue(dtoObject.getFinishDate());
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

    private void setListenerToNameTextField() {
        addressTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        addressTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind ();
            addressExceptionLabel.setVisible(false);
        });

        addressTextField.setOnMouseClicked((MouseEvent event) -> {
            if (addressTextField.getStyleClass().contains("warning")) {
                addressTextField.getStyleClass().remove("warning");
                addressExceptionLabel.setVisible(true);
            }
        });

        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
        addressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                addressExceptionLabel.setVisible(true);
            } else {
                addressExceptionLabel.setVisible(false);
            }
            if (addressTextField.getText().length() > 45) {
                String text = addressTextField.getText().substring(0, 45);
                addressTextField.setText(text);
            }
        });
    }

    private boolean textFieldMatcherFind(){
        if (addressTextField.getText() == null || addressTextField.getText().isEmpty()) {
            return false;
        }
        addressTextField.setText(addressTextField.getText().trim());
        boolean right = true;
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
        addressTextField.setText(addressTextField.getText().trim());
        Matcher matcher = pattern.matcher(addressTextField.getText());
        if (matcher.find()) {
            right = false;
            if (!addressTextField.getStyleClass().contains("warning")) {
                addressTextField.getStyleClass().add("warning");
            }
        }
        return right;
    }

    private boolean addressIsEmpty(){
        boolean isEmpty = false;
        if (addressTextField.getText() == null || addressTextField.getText().isEmpty()) {
            isEmpty = true;
            if (!addressTextField.getStyleClass().contains("warning")) {
                addressTextField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
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

    private void setStartDateDatePickerValidation() {
        LocalDate maxStartDate = finishDateDatePicker.getValue();
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if ((maxStartDate != null && item.isAfter(maxStartDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        startDateDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void setFinishDateDatePickerValidation() {
        LocalDate minFinishDate = startDateDatePicker.getValue();
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if ((minFinishDate != null && item.isBefore(minFinishDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        finishDateDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void setPriceTextFieldListener() {
        Pattern pattern = Pattern.compile("[^'\'.\\d]");
        costTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        costTextField.setTooltip(new Tooltip("Закладений кошторис"));
        costTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            costTextField.setText(costTextField.getText().trim());
            costExceptionLabel.setVisible(false);
            checkPriceTextFieldValidation();
        });

        costTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                costExceptionLabel.setVisible(true);
            } else {
                costExceptionLabel.setVisible(false);
            }
        });

        costTextField.setOnMouseClicked((MouseEvent event) -> {
            if (costTextField.getStyleClass().contains("warning")) {
                costTextField.getStyleClass().remove("warning");
                costExceptionLabel.setVisible(true);
            }
        });
    }

    private boolean checkPriceTextFieldValidation(){
        boolean isRight = true;
        costTextField.getStyleClass().remove("warning");
        try {
            new BigDecimal(costTextField.getText());
        } catch (NumberFormatException e) {
            if (! costTextField.getText().isEmpty()) {
                isRight = false;
                if (! costTextField.getStyleClass().contains("warning")) {
                    costTextField.getStyleClass().add("warning");
                }
            }
        }
        return isRight;
    }

    public void setWindowObjectsController(WindowObjectsController windowObjectsController) {
        this.windowObjectsController = windowObjectsController;
    }

    public void setDtoObject(DTOObject dtoObject) {
        this.dtoObject = dtoObject;
        setDateToControls();
    }

    public DTOObject getDtoObject() {
        return dtoObject;
    }
}
