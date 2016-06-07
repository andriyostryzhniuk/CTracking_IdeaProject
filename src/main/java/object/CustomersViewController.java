package object;

import employees.dto.DTOTelephones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.dto.DTOCustomers;
import object.dto.DTOInspection;
import subsidiary.classes.EditPanel;

import java.io.IOException;
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
    private List<DTOInspection> inspectionsToRemovingList = new LinkedList<>();
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
            setListViewCellFactory();
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
        initAddInspectionButton();
        initEditInspectionButton();
        initRemoveInspectionButton();
    }

    public void setListViewCellFactory(){
        inspectionsListView.setCellFactory(listCell -> {
            final ListCell<DTOInspection> cell = new ListCell<DTOInspection>() {
                @Override
                protected void updateItem(DTOInspection t, boolean b) {
                    super.updateItem(t, b);
                    if (t != null) {
                        setText(t.getFullName());
                    }

                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                if (inspectionsListView.getSelectionModel().getSelectedItem() != null) {
                                    showEditingTechInspectionWindow(
                                            inspectionsListView.getSelectionModel().getSelectedItem());
                                }
                            }
                        }
                    });
                }
            };
            return cell;
        });
    }

    private void initInspectionsListContextMenu(){
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> {
            showEditingTechInspectionWindow(new DTOInspection(null, dtoCustomers.getId(), null, null, null));
        });

        MenuItem editItem = new MenuItem("Редагувати");
        editItem.setOnAction((ActionEvent event) -> {
            showEditingTechInspectionWindow(inspectionsListView.getSelectionModel().getSelectedItem());
        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити");
        removeItem.setOnAction((ActionEvent event) -> {
            removeInspection(inspectionsListView.getSelectionModel().getSelectedItem());
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
                initTelephonesLabels(dtoInspection);
            }
        });

        inspectionsListView.setContextMenu(cellMenu);
    }

    public void initTelephonesLabels(DTOInspection dtoInspection){
        if (dtoInspection.getDtoTelephonesList() != null && ! dtoInspection.getDtoTelephonesList().isEmpty()) {
            int i = 0;
            for (DTOTelephones item: dtoInspection.getDtoTelephonesList()) {
                techTelephonesTextFieldsList.get(i).setText(item.getNumber());
                techTelephonesTextFieldsList.get(i).setDisable(false);
                techTelephonesTextFieldsList.get(i).setVisible(true);
                i++;
            }
        }
    }

    public void setDisableInspections(boolean isDisable){
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

    public void showEditingTechInspectionWindow(DTOInspection dtoInspection) {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/EditingTechInspection.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingTechInspectionController editingTechInspectionController = fxmlLoader.getController();
        editingTechInspectionController.setCustomersViewController(this);
        editingTechInspectionController.setDtoInspection(dtoInspection);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 360, 390, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(controlsGridPane.getScene().getWindow());
        primaryStage.showAndWait();
    }

    private void removeInspection(DTOInspection dtoInspection){
        if (dtoInspection.getId() != null) {
            inspectionsToRemovingList.add(dtoInspection);
        }
        inspectionsList.remove(dtoInspection);
        setListViewCellFactory();
    }

    public DTOCustomers getDtoCustomers() {
        initDtoCustomers();
        return dtoCustomers;
    }

    private void initAddInspectionButton(){
        final EditPanel editPanel = new EditPanel();
        Button addButton = editPanel.getAddButton();
        addButton.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        addButton.setTooltip(new Tooltip("Додати технічний нагляд"));
        addButton.setOnAction(event -> {
            showEditingTechInspectionWindow(new DTOInspection(null, dtoCustomers.getId(), null, null, null));
        });
        headerPanel.getChildren().add(addButton);
        addButton.setLayoutX(5);
        addButton.setLayoutY(1);
    }

    private void initEditInspectionButton(){
        final EditPanel editPanel = new EditPanel(inspectionsListView);
        Button editButton = editPanel.getEditButton();
        editButton.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        editButton.setTooltip(new Tooltip("Редагувати технічний нагляд"));
        editButton.setOnAction(event -> {
            showEditingTechInspectionWindow(inspectionsListView.getSelectionModel().getSelectedItem());
        });
        headerPanel.getChildren().add(editButton);
        editButton.setLayoutX(29);
        editButton.setLayoutY(1);
    }

    private void initRemoveInspectionButton(){
        final EditPanel editPanel = new EditPanel(inspectionsListView);
        Button deleteButton = editPanel.getDeleteButton();
        deleteButton.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        deleteButton.setTooltip(new Tooltip("Видалити технічний нагляд"));
        deleteButton.setOnAction(event -> removeInspection(inspectionsListView.getSelectionModel().getSelectedItem()));
        headerPanel.getChildren().add(deleteButton);
        deleteButton.setLayoutX(53);
        deleteButton.setLayoutY(1);
    }

    public TextField getNameTextField() {
        return nameTextField;
    }

    public void setInfoObjectsController(InfoObjectsController infoObjectsController) {
        this.infoObjectsController = infoObjectsController;
    }

    public ObservableList<DTOInspection> getInspectionsList() {
        return inspectionsList;
    }

    public ListView<DTOInspection> getInspectionsListView() {
        return inspectionsListView;
    }

    public List<DTOInspection> getInspectionsToRemovingList() {
        return inspectionsToRemovingList;
    }
}
