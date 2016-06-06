package object;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import object.dto.DTOCustomers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static object.ODBC_PubsBD.selectCustomer;

public class CustomersViewController {

    public TextField nameTextField;
    public TextArea notesTextArea;
    public Label nameExceptionLabel;
    private DTOCustomers dtoCustomers = new DTOCustomers();

    @FXML
    private void initialize(){
        setListenerToNameTextField(nameTextField, nameExceptionLabel, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&\\s-]"), 50);
        setNotesTextAreaListener();
    }


    public void initData(Integer customersId){
        if (customersId != null) {
            dtoCustomers = selectCustomer(customersId);
            nameTextField.setText(dtoCustomers.getName());
            notesTextArea.setText(dtoCustomers.getNotes());
        }
    }

    private void initDtoCustomers() {
        if (nameTextField.getText() == null || nameTextField.getText().isEmpty()) {
            dtoCustomers.setName(null);
        } else {
            dtoCustomers.setName(nameTextField.getText());
        }

        if (notesTextArea.getText() == null || notesTextArea.getText().isEmpty()) {
            dtoCustomers.setNotes(null);
        } else {
            dtoCustomers.setNotes(notesTextArea.getText());
        }
    }

    private void setListenerToNameTextField(TextField textField, Label exceptionLabel, Pattern pattern, Integer maxLength) {
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind (textField, pattern);
            exceptionLabel.setVisible(false);
        });

        textField.setOnMouseClicked((MouseEvent event) -> {
            if (textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().remove("warning");
                exceptionLabel.setVisible(true);
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setVisible(true);
            } else {
                exceptionLabel.setVisible(false);
            }
            if (textField.getText().length() > maxLength) {
                String text = textField.getText().substring(0, maxLength);
                textField.setText(text);
            }
        });
    }

    public boolean textFieldMatcherFind(TextField textField, Pattern pattern){
        if (textField.getText() == null || textField.getText().isEmpty()) {
            return false;
        }
        textField.setText(textField.getText().trim());
        boolean right = true;
        textField.setText(textField.getText().trim());
        Matcher matcher = pattern.matcher(textField.getText());
        if (matcher.find()) {
            right = false;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return right;
    }

    public boolean addressIsEmpty(TextField textField){
        boolean isEmpty = false;
        if (textField.getText() == null || textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    private void setNotesTextAreaListener(){
        notesTextArea.setTooltip(new Tooltip("Тут Ви можете написати будь-які нотатки\nпро замовника"));
        notesTextArea.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                if (notesTextArea.getText().length() > 400) {
                    String text = notesTextArea.getText().substring(0, 400);
                    notesTextArea.setText(text);
                }
            }
        });
    }

    public DTOCustomers getDtoCustomers() {
        initDtoCustomers();
        return dtoCustomers;
    }

    public TextField getNameTextField() {
        return nameTextField;
    }
}
