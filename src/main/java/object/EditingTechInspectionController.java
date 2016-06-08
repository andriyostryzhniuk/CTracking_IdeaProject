package object;

import employees.dto.DTOTelephones;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import object.dto.DTOInspection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditingTechInspectionController {

    public TextField surnameTextField;
    public TextField nameTextField;
    public TextField middleNameTextField;
    public Label exceptionLabel;

    public TableView telephonesTableView;
    public TableColumn colTelephone;
    public Button addTelephoneButton;
    public Label telephonesExceptionLabel;
    private ObservableList<DTOTelephones> telephonesList = FXCollections.observableArrayList();

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
        initTelephones();
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

        for (DTOTelephones item : telephonesList) {
            if (! textFieldMatcherFind(item.getTextField(), Pattern.compile("[^\\d]"))) {
                telephonesExceptionLabel.setVisible(true);
                return;
            }
        }

        telephonesList.forEach(item -> item.setNumber(item.getTextField().getText()));
        dtoInspection.setDtoTelephonesList(telephonesList);

        if (dtoInspection.getSurname() == null) {
            setDataToDTOInspection();
            customersViewController.getInspectionsList().add(dtoInspection);
        } else {
            setDataToDTOInspection();
            dtoInspection.setFullName(dtoInspection.initFullName());
            customersViewController.getInspectionsListView().refresh();
        }
        customersViewController.setListViewCellFactory();
        DTOInspection selectedDTOInspection;
        if ((selectedDTOInspection =
                customersViewController.getInspectionsListView().getSelectionModel().getSelectedItem()) != null) {
            customersViewController.setDisableInspections(false);
            customersViewController.initTelephonesLabels(selectedDTOInspection);
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

    private void initTelephones(){
        telephonesTableView.widthProperty().addListener((ov, t, t1) -> {
            Pane header = (Pane)telephonesTableView.lookup("TableHeaderRow");
            if(header!=null && header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
                header.setManaged(false);
            }
        });
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("gridPane"));
        telephonesTableView.setPlaceholder(new Label());
        telephonesTableView.setItems(telephonesList);

        telephonesList.addListener((ListChangeListener<DTOTelephones>) pChange -> {
            addTelephoneButton.setDisable(telephonesList.size() > 2 ? true : false);
        });

        initAddTelephoneButton();
    }

    private void initAddTelephoneButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/plus_icon.png"));
        addTelephoneButton.getStylesheets().add(getClass().getResource("/styles/PlusButtonStyle.css").toExternalForm());
        addTelephoneButton.setGraphic(new ImageView(image));
        addTelephoneButton.setTooltip(new Tooltip("Додати телефон"));
        addTelephoneButton.setOnAction(event -> {
            DTOTelephones dtoTelephones = new DTOTelephones(null, dtoInspection.getId(), "");
            telephonesList.add(dtoTelephones);
            setTelephonesTextFieldListener(dtoTelephones);
        });
    }

    private void setTelephonesTextFieldListener(DTOTelephones dtoTelephones){
        TextField textField = dtoTelephones.getTextField();
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^\\d]");

        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            textFieldMatcherFind(textField, pattern);
            if (! newPropertyValue) {
                textField.getStyleClass().remove("focused");
                textField.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(230, 230, 230);");
                if (textField.getText() == null || textField.getText().isEmpty()) {
                    telephonesList.remove(dtoTelephones);
                }
            } else {
                if (! textField.getStyleClass().contains("focused")) {
                    textField.getStyleClass().add("focused");
                }
                textField.setStyle("");
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                telephonesList.remove(dtoTelephones);
                if (dtoTelephones.getRecordId() != null) {
                    dtoInspection.getDtoTelephonesToRemovingList().add(dtoTelephones);
                }
            } else {
                Matcher matcher = pattern.matcher(newValue);
                if (matcher.find()) {
                    telephonesExceptionLabel.setVisible(true);
                } else {
                    telephonesExceptionLabel.setVisible(false);
                }
                if (textField.getText().length() > 20) {
                    String text = textField.getText().substring(0, 20);
                    textField.setText(text);
                }
            }
        });
    }

    private void setDataToControls(){
        if (dtoInspection.getSurname() != null) {
            surnameTextField.setText(dtoInspection.getSurname());
            nameTextField.setText(dtoInspection.getName());
            middleNameTextField.setText(dtoInspection.getMiddleName());

            if (dtoInspection.getDtoTelephonesList() != null && ! dtoInspection.getDtoTelephonesList().isEmpty()) {
                telephonesList.addAll(dtoInspection.getDtoTelephonesList());
                telephonesList.forEach(item -> setTelephonesTextFieldListener(item));
            }
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
