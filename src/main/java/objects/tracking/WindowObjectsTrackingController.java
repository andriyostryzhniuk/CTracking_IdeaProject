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
import java.util.List;
import static objects.tracking.ContextMenu.initContextMenu;
import static objects.tracking.ODBC_PubsBD.*;

public class WindowObjectsTrackingController<T extends DTOObjectEmployees> {

    public BorderPane rootBorderPane;
    public GridPane gridPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
    public ChoiceBox contentTypeChoiceBox;
    public StackPane stackPane;
    public Label objectLabel;
    public GridPane skillsGridPane;
    public DatePicker datePicker;
    public Button todayButton;
    private LocalDate oldDatePickerValue;
    private ComboBox skillsComboBox;
    private ComboBox comboBoxListener = new ComboBox();
//    public TextArea notesTextArea;

    private EmployeesListViewController employeesListViewController;
    private ObjectsListViewController objectsListViewController;
    private boolean successSave;

    private ObservableList<T> tableViewDataList = FXCollections.observableArrayList();
    private List<T> dtoObjectEmployeesList;

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> employeeNameCol = new CustomTableColumn<>("Працівники");
    public CustomTableColumn<T, String> startDateNameCol = new CustomTableColumn<>("Початок");
    public CustomTableColumn<T, String> finishDateNameCol = new CustomTableColumn<>("Закінчення");

    @FXML
    public void initialize(){
        initDatePicker();
        initTodayButton();

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

        setTableViewParameters();
        initSkillsControls();

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
        fillTableView();
        stackPane.getChildren().add(tableView);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        initContextMenu(tableView.getTableView(), this);
        tableView.getTableView().setPlaceholder(new Label("Немає жодного працівника"));
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
        startDateNameCol.setCellValueFactory(new PropertyValueFactory("formatStartDate"));
        finishDateNameCol.setCellValueFactory(new PropertyValueFactory("formatFinishDate"));
    }

    private void setColsDateProperties() {
        employeeNameCol.setPercentWidth(118); employeeNameCol.setMinWidth(118);
        startDateNameCol.setPercentWidth(65); startDateNameCol.setMinWidth(65);
        finishDateNameCol.setPercentWidth(65); finishDateNameCol.setMinWidth(65);
    }

    private void fillTableView(){
        tableView.getTableView().getColumns().addAll(employeeNameCol, startDateNameCol, finishDateNameCol);
    }

    public void initTableView(List<T> dtoObjectEmployeesList, String objectName){
        this.dtoObjectEmployeesList = dtoObjectEmployeesList;
        objectLabel.setText(objectName);
        tableViewDataList.clear();
        tableView.getTableView().getItems().clear();

        tableViewDataList.addAll(dtoObjectEmployeesList);
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
        button.getStylesheets().add(getClass().getResource("/objects.tracking/RejectButtonStyle.css").toExternalForm());
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
        todayButton.getStylesheets().add(getClass().getResource("/objects.tracking/RejectButtonStyle.css").toExternalForm());
        todayButton.setGraphic(new ImageView(image));
        todayButton.setTooltip(new Tooltip("Сьогоднішня дата"));
        todayButton.setOnAction(event -> datePicker.setValue(LocalDate.now()));
    }

    public List<T> getDtoObjectEmployeesList() {
        return dtoObjectEmployeesList;
    }

    public EmployeesListViewController getEmployeesListViewController() {
        return employeesListViewController;
    }

    public void setSuccessSave(boolean successSave) {
        this.successSave = successSave;
    }
}
