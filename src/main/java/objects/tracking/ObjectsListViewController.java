package objects.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import objects.tracking.dto.DTOObjectEmpAddress;
import objects.tracking.dto.DTOObjects;
import objects.tracking.dto.DTOObjectEmployees;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static objects.tracking.ODBC_PubsBD.*;

public class ObjectsListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();

    private ObservableList<DTOObjects> objectsListViewDataList = FXCollections.observableArrayList();
    private ObservableList<String> objectsNamesList = FXCollections.observableArrayList();

    private WindowObjectsTrackingController windowObjectsTrackingController;
    private EmployeesListViewController employeesListViewController;
    private Integer selectedObjectId;

    @FXML
    public void initialize(){
        initComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);
        initList();
    }

    public void initList(){
        Integer selectedRowIndex = listView.getSelectionModel().getSelectedIndex();
        objectsListViewDataList.clear();
        listView.getItems().clear();
        objectsNamesList.clear();
        objectsListViewDataList.addAll(selectObjects());

        objectsListViewDataList.forEach(item -> {
            item.initPaneContainer();
            setTargetDragAndDrop(item.getPaneContainer());
            item.getPaneContainer().setOnMouseClicked(event -> {
                selectedObjectId = item.getId();
                windowObjectsTrackingController.initTableView(item.getObjectEmployeesList(), item.getAddress());
            });
            listView.getItems().add(item.getPaneContainer());
            objectsNamesList.add(item.getAddress());
            if (selectedObjectId != null && item.getId() == selectedObjectId ) {
                windowObjectsTrackingController.initTableView(item.getObjectEmployeesList(), item.getAddress());
            }
        });

        listView.getSelectionModel().select(selectedRowIndex);
        listView.getFocusModel().focus(selectedRowIndex);
        listView.scrollTo(selectedRowIndex);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private void initComboBoxSearch() {
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");

        comboBoxSearch.setItems(objectsNamesList);

        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);

        comboBoxSearch.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    super.setOnMousePressed((MouseEvent event) -> {
//                            mouse pressed
                        comboBoxListener.setValue(comboBoxSearch.getValue());
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
//                change detected
            if (newValue != null) {
                comboBoxSearch.getStyleClass().remove("warning");
                searchInListView();
                comboBoxListener.setValue(null);
            }
        });
    }

    private void searchInListView() {
        Object comboBoxListenerValue = comboBoxListener.getValue();
        Integer objectID = null;
        for (DTOObjects item : objectsListViewDataList) {
            if (item.getString().equals(comboBoxListenerValue)) {
                objectID = item.getId();
            }
        }
        if (objectID != null) {
            int i = 0;
            for (Pane pane : listView.getItems()) {
                if (Integer.parseInt(pane.getId()) == objectID) {
                    listView.getSelectionModel().select(i);
                    listView.getFocusModel().focus(i);
                    listView.scrollTo(i);
                }
                i++;
            }
        }
    }

    private void setTargetDragAndDrop(Pane pane) {
        pane.setOnDragOver(event -> {
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });

        pane.setOnDragEntered(event -> {
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                pane.setStyle("-fx-background-color: rgba(105, 105, 105, .8)");
            }

            event.consume();
        });

        pane.setOnDragExited(event -> {
            pane.setStyle("-fx-background-color: rgba(105, 105, 105, .5)");
            event.consume();
        });

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Integer objectId = Integer.parseInt(pane.getId());
                Integer employeeId = Integer.parseInt(db.getString());
                LocalDate startDate = LocalDate.now();
                LocalDate finishDate = null;

                LocalDate nextObjEmpStartDate;
                DTOObjectEmpAddress dtoObjectEmpAddress;
                if ((dtoObjectEmpAddress = selectIfEmployeeIsOnObject(employeeId)) != null) {
                    if (showDeletingConfirmation(dtoObjectEmpAddress.getAddress())) {
                        if (! terminateEmployeesJobOnObject(dtoObjectEmpAddress)) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else if ((nextObjEmpStartDate = selectNextObjEmpStartDate(employeeId, startDate)) != null) {
                    finishDate = nextObjEmpStartDate.minusDays(1);
                }

                insertIntoObjectEmployees(new DTOObjectEmployees(null, objectId, employeeId, startDate, finishDate));

                initList();
                employeesListViewController.initList();

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean showDeletingConfirmation(String objectName){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Попередження");
        alert.setHeaderText(null);
        alert.setContentText("Даний працівник уже задіяний на об'єкті \n" + objectName +
                "\nПрипинити виконання роботи цього працівника на об'єкті?");

        ButtonType okButton = new ButtonType("ОК");
        ButtonType cancelButton = new ButtonType("Скасувати");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == okButton) {
            return true;
        } else {
            return false;
        }
    }

    private boolean terminateEmployeesJobOnObject(DTOObjectEmpAddress dtoObjectEmpAddress) {
        Integer objectEmployeesId = dtoObjectEmpAddress.getId();
        LocalDate yesterdaysDate = LocalDate.now().minusDays(1);

        if (dtoObjectEmpAddress.getStartDate().isAfter(yesterdaysDate)) {
            showStartDateError(dtoObjectEmpAddress.getStartDate(), yesterdaysDate);
            return false;
        }

        LocalDate maxWorkDate;
        if ((maxWorkDate = selectMaxWorkDate(objectEmployeesId)) != null && maxWorkDate.isAfter(yesterdaysDate)) {
            showMaxWorkDateError(yesterdaysDate);
            return false;
        }
        updateFinishDate(objectEmployeesId, yesterdaysDate);
        return true;
    }

    private void showMaxWorkDateError(LocalDate localDate){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText("Неможливо закрити виконання роботи за датою "
                + localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                "\nоскільки за даною датою уже є відпрацьовані години.");
        alert.showAndWait();
    }

    private void showStartDateError(LocalDate startDate, LocalDate finishDate){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText("Неможливо закрити виконання роботи за датою "
                + finishDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                ",\nменшою ніж дата початку роботи "
                + startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");
        alert.showAndWait();
    }

    public void setEmployeesListViewController(EmployeesListViewController employeesListViewController) {
        this.employeesListViewController = employeesListViewController;
    }

    public void setWindowObjectsTrackingController(WindowObjectsTrackingController windowObjectsTrackingController) {
        this.windowObjectsTrackingController = windowObjectsTrackingController;
    }

}
