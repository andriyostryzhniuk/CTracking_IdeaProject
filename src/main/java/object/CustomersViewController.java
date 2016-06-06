package object;

import employees.dto.DTOTelephones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import object.dto.DTOCustomers;
import object.dto.DTOInspection;
import subsidiary.classes.EditPanel;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static object.ODBC_PubsBD.selectCustomer;
import static object.ODBC_PubsBD.selectInspectionsList;
import static object.ODBC_PubsBD.selectInspectionsTelephones;

public class CustomersViewController {

    public GridPane controlsGridPane;
    public TextField nameTextField;
    public TextArea notesTextArea;
    public Label nameExceptionLabel;

    public ListView<DTOInspection> inspectionsListView;
    public Pane headerPanel;
    public Label telephonesLabel;
    public TextField numberTextField1;
    public TextField numberTextField2;
    public TextField numberTextField3;
    private ObservableList<DTOInspection> inspectionsList = FXCollections.observableArrayList();
    private List<TextField> techTelephonesTextFieldsList = new LinkedList<>();

    private DTOCustomers dtoCustomers = new DTOCustomers();

    private InfoObjectsController infoObjectsController;

    @FXML
    private void initialize(){
        setListenerToNameTextField(nameTextField, nameExceptionLabel, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&\\s-]"), 50);
        setNotesTextAreaListener();
        initSettingsButton();
        initInspectionsListView();
        initTechTelephonesList();
    }

    public void initData(Integer customersId){
        if (customersId != null) {
            dtoCustomers = selectCustomer(customersId);
            nameTextField.setText(dtoCustomers.getName());
            notesTextArea.setText(dtoCustomers.getNotes());

            inspectionsList.clear();
            inspectionsList.addAll(selectInspectionsList(dtoCustomers.getId()));
            inspectionsList.forEach(item -> item.setDtoTelephonesList(selectInspectionsTelephones(item.getId())));
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

    private void initSettingsButton(){
        EditPanel editPanel = new EditPanel();
        Button settingsButton = editPanel.getSettingsButton();
        settingsButton.getStylesheets().add(getClass().getResource("/object/SettingsButtonStyle.css").toExternalForm());
        settingsButton.setTooltip(new Tooltip("Налаштування замовників"));
        controlsGridPane.add(settingsButton, 0, 0);
        controlsGridPane.setValignment(settingsButton, VPos.CENTER);
        controlsGridPane.setHalignment(settingsButton, HPos.RIGHT);

        settingsButton.setOnAction(event -> infoObjectsController.showSettingsCustomersWindow());
    }

    private void initInspectionsListView(){
        inspectionsListView.setPlaceholder(new Label("Додайте технічний нагляд"));
        inspectionsListView.setItems(inspectionsList);
        initInspectionsListContextMenu();


    }

    private void initInspectionsListContextMenu(){
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> {

        });

        MenuItem editItem = new MenuItem("Редагувати");
        editItem.setOnAction((ActionEvent event) -> {

        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити");
        removeItem.setOnAction((ActionEvent event) -> {

        });
        removeItem.setDisable(true);

        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(addItem, editItem, removeItem);

        inspectionsListView.getSelectionModel().selectedItemProperty().addListener(event -> {
            DTOInspection dtoInspection;
            if ((dtoInspection = inspectionsListView.getSelectionModel().getSelectedItem()) == null) {
                setDisableInspections(true);
            } else {
                setDisableInspections(false);
                if (dtoInspection.getDtoTelephonesList() != null) {
                    int i = 0;
                    for (DTOTelephones item: dtoInspection.getDtoTelephonesList()) {
                        techTelephonesTextFieldsList.get(i).setText(item.getNumber());
                        techTelephonesTextFieldsList.get(i).setDisable(false);
                        techTelephonesTextFieldsList.get(i).setVisible(true);
                        i++;
                    }
                }
            }
        });

        inspectionsListView.setContextMenu(cellMenu);
    }

    private void setDisableInspections(boolean isDisable){
//        editButton.setDisable(isDisable);
//        removeButton.setDisable(isDisable);
        telephonesLabel.setVisible(!isDisable);
        inspectionsListView.getContextMenu().getItems().get(1).setDisable(isDisable);
        inspectionsListView.getContextMenu().getItems().get(2).setDisable(isDisable);
        techTelephonesTextFieldsList.forEach(item -> {
            item.getText().getClass();
            item.setDisable(true);
            item.setVisible(false);
        });
    }

    private void initTechTelephonesList(){
        techTelephonesTextFieldsList.add(numberTextField1);
        techTelephonesTextFieldsList.add(numberTextField2);
        techTelephonesTextFieldsList.add(numberTextField3);
    }

    public DTOCustomers getDtoCustomers() {
        initDtoCustomers();
        return dtoCustomers;
    }

    public TextField getNameTextField() {
        return nameTextField;
    }

    public void setInfoObjectsController(InfoObjectsController infoObjectsController) {
        this.infoObjectsController = infoObjectsController;
    }
}
