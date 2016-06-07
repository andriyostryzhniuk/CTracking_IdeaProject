package object;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import object.dto.DTOInspection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditingTechInspectionController {

    public TextField surnameTextField;
    public TextField nameTextField;
    public TextField middleNameTextField;
    public Label exceptionLabel;

    private DTOInspection dtoInspection;

    private CustomersViewController customersViewController;

    @FXML
    private void initialize(){
        setListenerToNameTextField(surnameTextField,
                "Прізвище не може містити інших символів крім кириличних та символа - ");
        setListenerToNameTextField(nameTextField,
                "Ім'я не може містити інших символів крім кириличних та символа - ");
        setListenerToNameTextField(middleNameTextField,
                "Ім'я по батькові не може містити інших символів крім кириличних та символа - ");
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void close(){
        Stage stage = (Stage) surnameTextField.getScene().getWindow();
        stage.close();
    }

    public void saveButtonAction(ActionEvent actionEvent) {
        if (nameIsEmpty(surnameTextField) ||
                ! textFieldMatcherFind(surnameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
            return;
        }

        if (nameIsEmpty(nameTextField) ||
                ! textFieldMatcherFind(nameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
            return;
        }

        if (nameIsEmpty(middleNameTextField) ||
                ! textFieldMatcherFind(middleNameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
            return;
        }

        if (dtoInspection.getSurname() == null) {
            setDataToDTOInspection();
            customersViewController.getInspectionsList().add(dtoInspection);
        } else {
            setDataToDTOInspection();
            dtoInspection.setFullName(dtoInspection.initFullName());
            customersViewController.getInspectionsListView().refresh();
        }

        close();
    }

    private void setDataToDTOInspection(){
        dtoInspection.setSurname(surnameTextField.getText());
        dtoInspection.setName(nameTextField.getText());
        dtoInspection.setMiddleName(middleNameTextField.getText());
    }

    private void setListenerToNameTextField(TextField textField, String exceptionText) {
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind (textField, pattern);
            exceptionLabel.setText("");
        });

        textField.setOnMouseClicked((MouseEvent event) -> {
            if (textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().remove("warning");
                exceptionLabel.setText(exceptionText);
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText(exceptionText);
            } else {
                exceptionLabel.setText("");
            }
            if (textField.getText().length() > 45) {
                String text = textField.getText().substring(0, 45);
                textField.setText(text);
            }
        });
    }

    public boolean textFieldMatcherFind(TextField textField, Pattern pattern){
        if (textField.getText() == null || textField.getText().isEmpty()) {
            return false;
        }
        textField.setText(textField.getText().trim());
        textField.setText(textField.getText().substring(0, 1).toUpperCase() + textField.getText().substring(1));
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

    public boolean nameIsEmpty(TextField textField){
        boolean isEmpty = false;
        if (textField.getText() == null || textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    private void setDataToControls(){
        if (dtoInspection.getSurname() != null) {
            surnameTextField.setText(dtoInspection.getSurname());
            nameTextField.setText(dtoInspection.getName());
            middleNameTextField.setText(dtoInspection.getMiddleName());
        }
    }

    public void setCustomersViewController(CustomersViewController customersViewController) {
        this.customersViewController = customersViewController;
    }

    public void setDtoInspection(DTOInspection dtoInspection) {
        this.dtoInspection = dtoInspection;
        setDataToControls();
    }
}
