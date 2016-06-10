package employees;

import employees.dto.DTOEmployees;
import employees.dto.DTOSkills;
import employees.dto.DTOTelephones;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;
import overridden.elements.number.spinner.NumberSpinner;
import subsidiary.classes.AlertWindow;
import subsidiary.classes.EditPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static employees.ODBC_PubsBD.*;

public class InfoEmployeesController {

    public ImageView imageView;

    private final FileChooser fileChooser = new FileChooser();
    public GridPane photosButtonsGridPane;
    public Pane photosPane;

    public TextField surnameTextField;
    public TextField nameTextField;
    public TextField middleNameTextField;
    public VBox workingHoursVBox;
    public Pane headerPanel;
    public TableColumn colTelephone;
    private NumberSpinner workingHoursNumberSpinner = new NumberSpinner();
    public DatePicker birthDateDatePicker;
    public Button rejectBirthDateButton;
    public DatePicker firstDateDatePicker;
    public Label lastDateLabel;
    public DatePicker lastDateDatePicker;
    public Button standOffButton;
    public Button rejectLastDateButton;
    public TableView<DTOTelephones> telephonesTableView;
    public Button addTelephoneButton;
    public Label telephonesExceptionLabel;
    public TextArea notesTextArea;

    public ListView<DTOSkills> skillsListView;
    public Label surnameExceptionLabel;
    public Label nameExceptionLabel;
    public Label middleNameExceptionLabel;
    public Label workingHoursExceptionLabel;

    private DTOEmployees dtoEmployees;
    private WindowEmployeesController windowEmployeesController;
    private ObservableList<DTOSkills> skillsList = FXCollections.observableArrayList();
    private ObservableList<DTOTelephones> telephonesList = FXCollections.observableArrayList();

    private List<DTOSkills> skillsListToAdding = new LinkedList<>();
    private List<DTOSkills> skillsListToRemoving = new LinkedList<>();
    private List<DTOTelephones> telephonesListToRemoving = new LinkedList<>();
    private File photoFile;
    private String newPhotosPath;

    public void initWindow(){
        setPhotoHoverAction();
        initNumberSpinner();
        initRejectBirthDateButton();
        initRejectLastDateButton();
        initTelephones();
        if (dtoEmployees.getImagesURL() != null) {
            loadPhoto(getPhotosPath()+"\\"+dtoEmployees.getImagesURL());
        } else {
            photosButtonsGridPane.add(getAddingPhotoButton(), 0, 0);
        }
        initSkillsListView();
        setControlsListeners();
        initSaveButton();
    }

    private void setPhotoHoverAction(){
        photosPane.setOnMouseEntered(event -> {
            photosButtonsGridPane.setVisible(true);
            photosPane.setStyle("-fx-background-color: rgb(0, 0, 0, .2)");
        });

        photosPane.setOnMouseExited(event -> {
            photosButtonsGridPane.setVisible(false);
            photosPane.setStyle("-fx-background-color: transparent");
        });
    }

    private void addPhoto() {
        File file;
        if ((file = fileChooser.showOpenDialog(imageView.getScene().getWindow())) != null) {
            String extension = FilenameUtils.getExtension(file.getName());
            if (! extension.equals("png") && ! extension.equals("jpg")) {
                alterAddingPhotoError();
                return;
            }

            newPhotosPath = dtoEmployees.getId().toString()+".png";
            this.photoFile = file;
            loadPhoto(photoFile.getPath());
        }
    }

    private void loadPhoto(String path){
        File file;
        if ((file = new File(path)) != null ) {
            imageView.setImage(new Image(file.toURI().toString()));

            photosButtonsGridPane.getChildren().clear();
            Button changingPhotoButton = getChangingPhotoButton();
            photosButtonsGridPane.add(changingPhotoButton, 0, 0);
            photosButtonsGridPane.setMargin(changingPhotoButton, new Insets(0, 0, 5, 0));
            Button removingPhotoButton = getRemovingPhotoButton();
            photosButtonsGridPane.add(removingPhotoButton, 0, 1);
            photosButtonsGridPane.setMargin(removingPhotoButton, new Insets(5, 0, 0, 0));
        }
    }

    private void removePhoto(){
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }

        newPhotosPath = null;
        this.photoFile = new File(getPhotosPath()+"\\"+dtoEmployees.getImagesURL());
        imageView.setImage(new Image(getClass().getResourceAsStream("/icons/no-photo.png")));
        photosButtonsGridPane.getChildren().clear();
        photosButtonsGridPane.add(getAddingPhotoButton(), 0, 0);
    }

    private void setDateToControls(){
        surnameTextField.setText(dtoEmployees.getSurname());
        nameTextField.setText(dtoEmployees.getName());
        middleNameTextField.setText(dtoEmployees.getMiddleName());
        workingHoursNumberSpinner.setValue(dtoEmployees.getWorkingHours());
        if (dtoEmployees.getBirthDate() != null) {
            birthDateDatePicker.setValue(dtoEmployees.getBirthDate());
        }
        firstDateDatePicker.setValue(dtoEmployees.getFirstDate());
        if (dtoEmployees.getLastDate() != null) {
            lastDateDatePicker.setValue(dtoEmployees.getLastDate());
            setLastDateControlsState(true, false);
        }
        notesTextArea.setText(dtoEmployees.getNotes());
        skillsList.addAll(selectEmployeesSkills(dtoEmployees.getId()));
    }

    private void initRejectBirthDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectBirthDateButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        rejectBirthDateButton.setGraphic(new ImageView(image));
        rejectBirthDateButton.setTooltip(new Tooltip("Відмінити дату"));
        rejectBirthDateButton.setOnAction(event -> birthDateDatePicker.setValue(null));
    }

    private void initRejectLastDateButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        rejectLastDateButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        rejectLastDateButton.setGraphic(new ImageView(image));
        rejectLastDateButton.setTooltip(new Tooltip("Прийняти"));
        rejectLastDateButton.setOnAction(event -> {
            lastDateDatePicker.setValue(null);
            setLastDateControlsState(false, true);
        });
    }

    private void initSkillsListView(){
        skillsListView.setItems(skillsList);
        initAddSkillsButton();
        initRemoveSkillsButton();

        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> showAddingSkillsWindow());
        MenuItem removeItem = new MenuItem("Видалити");
        skillsListView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if (skillsListView.getSelectionModel().getSelectedItem() == null) {
                removeItem.setDisable(true);
            } else {
                removeItem.setDisable(false);
            }
        });
        removeItem.setOnAction((ActionEvent event) -> removeSkills());
        removeItem.setDisable(true);
        final javafx.scene.control.ContextMenu cellMenu = new javafx.scene.control.ContextMenu();
        cellMenu.getItems().addAll(addItem, removeItem);

        skillsListView.setContextMenu(cellMenu);
    }

    private void removeSkills() {
        DTOSkills skill;
        if ((skill = skillsListView.getSelectionModel().getSelectedItem()) == null) {
            return;
        }
        if (skill.getSkillsEmployeesId() != null) {
            skillsListToRemoving.add(skill);
        } else {
            skillsListToAdding.remove(skill);
        }
        skillsList.remove(skill);
    }

    private void showAddingSkillsWindow() {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees/AddingEmployeesSkill.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AddingEmployeesSkillController addingEmployeesSkillController = fxmlLoader.getController();
        addingEmployeesSkillController.setInfoEmployeesController(this);
        addingEmployeesSkillController.setHasAddedSkillsList(skillsList);
        addingEmployeesSkillController.initListView();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 460, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(skillsListView.getScene().getWindow());
        primaryStage.showAndWait();
    }

    private Button getAddingPhotoButton(){
        Button button = new Button("Додати фото");
        button.setTooltip(new Tooltip("Завантажити фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> addPhoto());
        return button;
    }

    private Button getChangingPhotoButton(){
        Button button = new Button("Змінити");
        button.setTooltip(new Tooltip("Завантажити нове фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> addPhoto());
        return button;
    }

    private Button getRemovingPhotoButton(){
        Button button = new Button("Видалити");
        button.setTooltip(new Tooltip("Видалити фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> removePhoto());
        return button;
    }

    private String getPhotosPath(){
        String path = "C:\\Users\\"+ System.getProperty("user.name") +"\\Documents\\CTracking";
        File directory = new File(path);
        if (! directory.exists()) {
            directory.mkdir();
        }
        path = path + "\\photos";
        directory = new File(path);
        if (! directory.exists()) {
            directory.mkdir();
        }
        return path;
    }

    private void savePhoto(){
        if (newPhotosPath == null) {
            photoFile.delete();
        } else {
            new File(getPhotosPath()+"\\"+ newPhotosPath).delete();
            try {
                java.nio.file.Files.copy(photoFile.toPath(), Paths.get(getPhotosPath()+"\\"+ newPhotosPath));
            } catch (NoSuchFileException n) {
                n.printStackTrace();
                alterLoadingPhotoError();
                newPhotosPath = null;
            } catch (IOException e) {
                e.printStackTrace();
                alterLoadingPhotoError();
                newPhotosPath = null;
            }
        }
        dtoEmployees.setImagesURL(newPhotosPath);
    }

    private void initNumberSpinner(){
        workingHoursNumberSpinner.setMinWidth(80);
        workingHoursNumberSpinner.setPrefWidth(80);
        workingHoursNumberSpinner.setMaxWidth(80);
        workingHoursNumberSpinner.setMinValue(1);
        workingHoursNumberSpinner.setMaxValue(24);
        workingHoursVBox.getChildren().add(workingHoursNumberSpinner);
    }

    private void setControlsListeners(){
        setTextFieldsListener(surnameTextField, surnameExceptionLabel);
        setTextFieldsListener(nameTextField, nameExceptionLabel);
        setTextFieldsListener(middleNameTextField, middleNameExceptionLabel);
        setIntegerListener(workingHoursNumberSpinner, workingHoursExceptionLabel);
        addTextAreaLimiter(notesTextArea, 200);
        notesTextArea.setTooltip(new Tooltip("Тут Ви можете написати будь-які нотатки\nпро працівника"));
        birthDateDatePicker.setOnMouseEntered(event -> setBirthDateDatePickerValidation());
        firstDateDatePicker.setOnMouseEntered(event -> setFirstDateDatePickerValidation());
        lastDateDatePicker.setOnMouseEntered(event -> setLastDateDatePickerValidation());
    }

    public void initSaveButton(){
        windowEmployeesController.getSaveButton().setVisible(true);
        windowEmployeesController.getSaveButton().setOnAction(event -> {

            if (isEmpty(surnameTextField) ||
                    ! textFieldMatcherFind(surnameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
                return;
            }

            if (isEmpty(nameTextField) ||
                    ! textFieldMatcherFind(nameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
                return;
            }

            if (isEmpty(middleNameTextField) ||
                    ! textFieldMatcherFind(middleNameTextField, Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]"))){
                return;
            }

            if (! checkWorkingHoursValidation(workingHoursNumberSpinner, workingHoursExceptionLabel)) {
                return;
            }

            for (DTOTelephones item : telephonesList) {
                if (! textFieldMatcherFind(item.getTextField(), Pattern.compile("[^\\d]"))) {
                    telephonesExceptionLabel.setVisible(true);
                    return;
                }
            }

            if (photoFile != null) {
                savePhoto();
            }

            dtoEmployees = new DTOEmployees(dtoEmployees.getId(), nameTextField.getText(), surnameTextField.getText(),
                    middleNameTextField.getText(), birthDateDatePicker.getValue(), firstDateDatePicker.getValue(),
                    lastDateDatePicker.getValue(), notesTextArea.getText(),
                    workingHoursNumberSpinner.getValue().intValue(), dtoEmployees.getImagesURL());

            if (dtoEmployees.getId() != null) {
                updateEmployees(dtoEmployees);
            } else {
                dtoEmployees.setId(insertIntoEmployees(dtoEmployees));
            }

            if (! skillsListToAdding.isEmpty()) {
                skillsListToAdding.forEach(item -> item.setEmployeesId(dtoEmployees.getId()));
                insertIntoSkillsEmployees(skillsListToAdding);
            }

            if (! skillsListToRemoving.isEmpty()) {
                deleteFromSkillsEmployees(skillsListToRemoving);
            }

            if (! telephonesListToRemoving.isEmpty()) {
                deleteTelephones(telephonesListToRemoving);
            }

            telephonesList.forEach(item -> {
                item.setNumber(item.getTextField().getText());
                if (item.getRecordId() == null) {
                    item.setSubscriberId(dtoEmployees.getId());
                    insertTelephones(item);
                } else {
                    updateTelephones(item);
                }
            });

            windowEmployeesController.initListView(true);
        });
    }

    private void setTextFieldsListener(TextField textField, Label exceptionLabel) {
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^а-яА-ЯіІїЇєЄ'-]");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(textField, pattern);
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
            if (textField.getText().length() > 45) {
                String text = textField.getText().substring(0, 45);
                textField.setText(text);
            }
        });
    }

    private void setIntegerListener(TextField textField, Label exceptionLabel){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkWorkingHoursValidation(textField, exceptionLabel);
        });
    }

    private boolean checkWorkingHoursValidation(TextField textField, Label exceptionLabel){
        Integer hours;
        if (textField.getText() == null || textField.getText().isEmpty()) {
            exceptionLabel.setVisible(true);
            return false;
        }
        try {
            hours = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            exceptionLabel.setVisible(true);
            return false;
        }
        if (hours > 24 || hours < 1) {
            exceptionLabel.setVisible(true);
            return false;
        }
        exceptionLabel.setVisible(false);
        return true;
    }

    private boolean textFieldMatcherFind(TextField textField, Pattern pattern){
        if (textField.getText() == null || textField.getText().isEmpty()) {
            return false;
        }
        textField.setText(textField.getText().trim());
        textField.setText(textField.getText().substring(0, 1).toUpperCase() + textField.getText().substring(1));
        Matcher matcher = pattern.matcher(textField.getText());
        if (matcher.find()) {
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
            return false;
        }
        return true;
    }

    private boolean isEmpty(TextField textField){
        boolean isEmpty = false;
        if (textField.getText() == null || textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    private void addTextAreaLimiter(final TextArea textArea, final int maxLength) {
        textArea.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textArea.getText().length() > maxLength) {
                String text = textArea.getText().substring(0, maxLength);
                textArea.setText(text);
            }
        });
    }

    private void initAddSkillsButton(){
        final EditPanel editPanel = new EditPanel();
        Button addButton = editPanel.getAddButton();
        addButton.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        addButton.setTooltip(new Tooltip("Додати спеціальність"));
        addButton.setOnAction(event -> showAddingSkillsWindow());
        headerPanel.getChildren().add(addButton);
        addButton.setLayoutX(5);
        addButton.setLayoutY(1);
    }

    private void initRemoveSkillsButton(){
        final EditPanel editPanel = new EditPanel(skillsListView);
        Button deleteButton = editPanel.getDeleteButton();
        deleteButton.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        deleteButton.setTooltip(new Tooltip("Видалити спеціальність"));
        deleteButton.setOnAction(event -> removeSkills());
        headerPanel.getChildren().add(deleteButton);
        deleteButton.setLayoutX(29);
        deleteButton.setLayoutY(1);
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

        telephonesList.addAll(selectEmployeesTelephones(dtoEmployees.getId()));
        telephonesList.forEach(item -> setTelephonesTextFieldListener(item));
        initAddTelephoneButton();
    }

    private void initAddTelephoneButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/plus_icon.png"));
        addTelephoneButton.getStylesheets().add(getClass().getResource("/styles/PlusButtonStyle.css").toExternalForm());
        addTelephoneButton.setGraphic(new ImageView(image));
        addTelephoneButton.setTooltip(new Tooltip("Додати телефон"));
        addTelephoneButton.setOnAction(event -> {
            DTOTelephones dtoTelephones = new DTOTelephones("");
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
                    textField.setText(null);
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
                    telephonesListToRemoving.add(dtoTelephones);
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

    public void standOffAction(ActionEvent actionEvent) {
        String notClosedWork;
        if ((notClosedWork = selectNotClosedWork(dtoEmployees.getId())) != null) {
            alterStandOffError(notClosedWork);
            return;
        }
        lastDateDatePicker.setValue(LocalDate.now());
        setLastDateControlsState(true, false);
    }

    private void setLastDateControlsState(boolean datePickerState, boolean buttonState) {
        standOffButton.setDisable(datePickerState);
        standOffButton.setVisible(buttonState);
        lastDateLabel.setVisible(datePickerState);
        lastDateDatePicker.setDisable(buttonState);
        lastDateDatePicker.setVisible(datePickerState);
    }

    private void alterAddingPhotoError(){
        String contentText = "Зображення повинне відповідати формату *.png, або *.jpg.";
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR, null, contentText);
        alertWindow.showError();
    }

    private void alterLoadingPhotoError(){
        String contentText = "Не вдалось завантажити фото.\nМожливо фото було переміщено, або видалено.";
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR, null, contentText);
        alertWindow.showError();
    }

    private void alterStandOffError(String notClosedWork){
        String contentText = "Не можливо звільнити працівника, оскільки у нього" +
                "\nне закрита робота на об'єкті\n" + notClosedWork +
                ".\nЗавершіть воконання роботи на цьому об'єкті та спробуйте ще раз.";
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR, null, contentText);
        alertWindow.showError();
    }

    private void setBirthDateDatePickerValidation() {
        LocalDate maxBirthDate = firstDateDatePicker.getValue().minusYears(16);
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isAfter(maxBirthDate)) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        birthDateDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void setFirstDateDatePickerValidation() {
        LocalDate minFirstDate = null;
        if (birthDateDatePicker.getValue() != null) {
            minFirstDate = birthDateDatePicker.getValue().plusYears(16);
        }

        LocalDate maxFirstDate = lastDateDatePicker.getValue();
        LocalDate minObjEmpDate;
        if ((minObjEmpDate = selectMinObjEmpDate(dtoEmployees.getId())) != null) {
            maxFirstDate = minObjEmpDate;
        }
        LocalDate minEmpStockDate;
        if ((minEmpStockDate = selectMinEmpStockDate(dtoEmployees.getId())) != null) {
            if (maxFirstDate == null || minEmpStockDate.isBefore(maxFirstDate)) {
                maxFirstDate = minEmpStockDate;
            }
        }

        LocalDate finalMinFirstDate = minFirstDate;
        LocalDate finalMaxFirstDate = maxFirstDate;
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if ((finalMinFirstDate != null && item.isBefore(finalMinFirstDate)) ||
                                (finalMaxFirstDate != null && item.isAfter(finalMaxFirstDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        firstDateDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void setLastDateDatePickerValidation() {
        LocalDate minLastDate = firstDateDatePicker.getValue();
        LocalDate maxObjEmpDate;
        if ((maxObjEmpDate = selectMaxObjEmpDate(dtoEmployees.getId())) != null) {
            minLastDate = maxObjEmpDate;
        }
        LocalDate maxEmpStockDate;
        if ((maxEmpStockDate = selectMaxEmpStockDate(dtoEmployees.getId())) != null) {
            if (minLastDate == null || maxEmpStockDate.isAfter(minLastDate)) {
                minLastDate = maxEmpStockDate;
            }
        }

        LocalDate finalMinLastDate = minLastDate;
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if ((item.isBefore(finalMinLastDate))) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        lastDateDatePicker.setDayCellFactory(dayCellFactory);
    }

    public void setDtoEmployees(DTOEmployees dtoEmployees) {
        this.dtoEmployees = dtoEmployees;
        setDateToControls();
    }

    public void setWindowEmployeesController(WindowEmployeesController windowEmployeesController) {
        this.windowEmployeesController = windowEmployeesController;
    }

    public List<DTOSkills> getSkillsListToAdding() {
        return skillsListToAdding;
    }

}
