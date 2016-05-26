package objects.tracking;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import overridden.elements.table.view.CustomTableColumn;
import overridden.elements.table.view.TableViewHolder;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import objects.tracking.dto.DTOObjectEmployees;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import static objects.tracking.ContextMenu.initContextMenu;
import static objects.tracking.ODBC_PubsBD.*;

public class WindowObjectsTrackingController<T extends DTOObjectEmployees> {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane leftSideGridPane;
    public ChoiceBox contentTypeChoiceBox;
    public StackPane stackPane;
    public Label objectLabel;
    public GridPane skillsGridPane;
    public DatePicker datePicker;
    public Button todayButton;
    public CheckBox viewOnlyActualCheckBox;
    private LocalDate oldDatePickerValue;
    private ComboBox skillsComboBox;
    private ComboBox comboBoxListener = new ComboBox();
    public TextArea employeesNameTextArea;
    public Label skillsLabel;
    public TextArea skillsTextArea;
    public Label notesLabel;
    public TextArea notesTextArea;

    private EmployeesListViewController employeesListViewController;
    private ObjectsListViewController objectsListViewController;
    private boolean successSave;
    private Integer selectedObjectId;

    private ObservableList<T> tableViewDataList = FXCollections.observableArrayList();

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> employeeNameCol = new CustomTableColumn<>("Працівники");
    public CustomTableColumn<T, String> startDateCol = new CustomTableColumn<>("Початок");
    public CustomTableColumn<T, String> finishDateCol = new CustomTableColumn<>("Закінчення");

    @FXML
    public void initialize(){
        viewOnlyActualCheckBox.setSelected(true);
        initDatePicker();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/EmployeesListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            employeesListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        initContentTypeChoiceBox();

        fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/ObjectsListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 3, 0);
            objectsListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        objectsListViewController.setEmployeesListViewController(employeesListViewController);
        objectsListViewController.setWindowObjectsTrackingController(this);
        employeesListViewController.setWindowObjectsTrackingController(this);

        setTableViewParameters();
        initSkillsControls();
        initOnlyActualCheckBox();

    }

    private void initOnlyActualCheckBox(){
        viewOnlyActualCheckBox.selectedProperty().addListener(observable -> {
            if (selectedObjectId != null) {
                initTableView(selectedObjectId);
            }
        });
    }

    private void initSkillsControls(){
        skillsComboBox = initSkillsComboBox();
        skillsGridPane.add(skillsComboBox, 0, 1);
        Button rejectDateButton = initRejectDateButton();
        skillsGridPane.add(rejectDateButton, 0, 1);
        skillsGridPane.setMargin(rejectDateButton, new Insets(0, 2, 1, 149));
    }

    private void setTableViewParameters(){
        fillCols();
        setColsDateProperties();
        tableView.getTableView().getColumns().addAll(employeeNameCol, startDateCol, finishDateCol);
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("На даному об'єкті немає жодного працівника"));

        tableView.getTableView().getSelectionModel().selectedItemProperty().addListener(event -> {
            T selectedItem;
            if ((selectedItem = tableView.getTableView().getSelectionModel().getSelectedItem()) != null) {
                clearEmployeesAboutInfo();
                initEmployeesAboutInfo(selectedItem.getEmployeeId());
            } else {
                clearEmployeesAboutInfo();
            }
        });
    }

    private void initContentTypeChoiceBox(){
        contentTypeChoiceBox.getItems().addAll("Тільки вільні працівники", "Всі працівники");
        contentTypeChoiceBox.setTooltip(new Tooltip("Якийсь текст"));

        contentTypeChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            if (newValue.equals(contentTypeChoiceBox.getItems().get(0))) {
                employeesListViewController.setAllEmployees(false);
                employeesListViewController.initList(false);
            } else {
                employeesListViewController.setAllEmployees(true);
                employeesListViewController.initList(false);
            }
        });

        contentTypeChoiceBox.setValue(contentTypeChoiceBox.getItems().get(0));
    }

    private void fillCols() {
        employeeNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        startDateCol.setCellValueFactory(new PropertyValueFactory("formatStartDate"));
        finishDateCol.setCellValueFactory(new PropertyValueFactory("formatFinishDate"));
    }

    private void setColsDateProperties() {
        employeeNameCol.setPercentWidth(118); employeeNameCol.setMinWidth(118);
        startDateCol.setPercentWidth(65); startDateCol.setMinWidth(65);
        finishDateCol.setPercentWidth(65); finishDateCol.setMinWidth(65);
    }

    public void initTableView(Integer objectId){
        this.selectedObjectId = objectId;
        objectLabel.setText(selectObjectAddress(objectId));
        tableViewDataList.clear();
        tableView.getTableView().getItems().clear();

        if (viewOnlyActualCheckBox.isSelected()) {
            tableViewDataList.addAll((Collection<? extends T>)
                    FXCollections.observableArrayList(
                            selectOnlyActualObjectEmployeesList(objectId, datePicker.getValue())));
        } else {
            tableViewDataList.addAll((Collection<? extends T>)
                    FXCollections.observableArrayList(selectObjectEmployeesList(objectId)));
        }
        tableView.getTableView().setItems(tableViewDataList);
    }

    public boolean editRecord(T item, boolean toUpdate) {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objects.tracking/EditingPromptWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingPromptWindowController editingPromptWindowController = fxmlLoader.getController();
        editingPromptWindowController.setToUpdate(toUpdate);
        editingPromptWindowController.setDtoObjectEmployees(item);
        editingPromptWindowController.setWindowObjectsTrackingController(this);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 400, 200, Color.rgb(0, 0, 0, 0)));
        editingPromptWindowController.initShortcuts();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(rootBorderPane.getScene().getWindow());
        primaryStage.showAndWait();

        return successSave;
    }

    public void removeRecord(T item) {
        deleteFromObjectEmployees(item.getId());
        objectsListViewController.initList(true);
        if (!employeesListViewController.isAllEmployees()) {
            employeesListViewController.initList(true);
        }
    }

    private ComboBox initSkillsComboBox(){
        ComboBox comboBox = new ComboBox();
        comboBox.getStylesheets().add(getClass().getResource("/objects.tracking/ComboBoxSkillsStyle.css").toExternalForm());
        comboBox.setPromptText("Введіть спеціальність");
        comboBox.setTooltip(new Tooltip("Пошук працівників за спеціальністю"));

        comboBox.setItems(FXCollections.observableArrayList(selectAllSkills()));

        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);

        comboBox.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {{
                    super.setOnMousePressed((MouseEvent event) -> {
                        comboBoxListener.setValue(comboBox.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        comboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                comboBox.getStyleClass().remove("warning");
                employeesListViewController.setEmployeesSkill(newValue.toString());
                employeesListViewController.initList(false);
                comboBoxListener.setValue(null);
            }
        });
        comboBox.getEditor().setPadding(new Insets(4, 28, 4, 4));
        return comboBox;
    }

    private Button initRejectDateButton(){
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        button.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setTooltip(new Tooltip("Відмінити пошук"));
        button.setOnAction(event -> {
            skillsComboBox.setValue(null);
            employeesListViewController.setEmployeesSkill(null);
            employeesListViewController.initList(false);
        });
        return button;
    }

    private void initDatePicker(){
        datePicker.setTooltip(new Tooltip("Дата перегляду"));
        datePicker.setValue(LocalDate.now());
        oldDatePickerValue = datePicker.getValue();
        initTodayButton();

        datePicker.valueProperty().addListener(observable -> {
            LocalDate newValue = datePicker.getValue();
            if (oldDatePickerValue.compareTo(newValue) != 0) {
                oldDatePickerValue = newValue;
                employeesListViewController.setDateView(newValue);
                employeesListViewController.initList(false);
                objectsListViewController.setDateView(newValue);
                objectsListViewController.initList(false);
            }
        });
    }

    private void initTodayButton(){
        Image image = new Image(getClass().getResourceAsStream("/icons/today_icon.png"));
        todayButton.getStylesheets().add(getClass().getResource("/styles/RejectButtonStyle.css").toExternalForm());
        todayButton.setGraphic(new ImageView(image));
        todayButton.setTooltip(new Tooltip("Сьогоднішня дата"));
        todayButton.setOnAction(event -> datePicker.setValue(LocalDate.now()));
    }

    public void initEmployeesAboutInfo(Integer employeeId){
        List<String> skillsList = selectEmployeesSkills(employeeId);
        String notes = selectEmployeesNotes(employeeId);
        employeesNameTextArea.setText(selectEmployeesName(employeeId));

        if (! skillsList.isEmpty() && skillsList!= null) {
            skillsLabel.setVisible(true);
            String textAreaText = new String();
            for (String item : skillsList) {
                textAreaText = textAreaText + item + "\n";
            }
            skillsTextArea.setText(textAreaText);
        }

        if (notes != null && ! notes.isEmpty()) {
            notesLabel.setVisible(true);
            notesTextArea.setText(notes);
        }
    }

    public void clearEmployeesAboutInfo(){
        employeesNameTextArea.clear();
        skillsLabel.setVisible(false);
        skillsTextArea.clear();
        notesLabel.setVisible(false);
        notesTextArea.clear();
    }

    public EmployeesListViewController getEmployeesListViewController() {
        return employeesListViewController;
    }

    public void setSuccessSave(boolean successSave) {
        this.successSave = successSave;
    }

}
