package objects.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import objects.tracking.dto.DTOEmployees;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import static objects.tracking.ODBC_PubsBD.selectAllEmployees;
import static objects.tracking.ODBC_PubsBD.selectFreeEmployees;

public class EmployeesListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();

    private ObservableList<DTOEmployees> employeesListViewDataList = FXCollections.observableArrayList();
    private ObservableList<String> employeesNamesList = FXCollections.observableArrayList();

    private boolean isAllEmployees;

    @FXML
    public void initialize(){
        initComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);
    }

    public void initList(){
        Integer selectedRowIndex = listView.getSelectionModel().getSelectedIndex();
        employeesListViewDataList.clear();
        listView.getItems().clear();
        employeesNamesList.clear();

        if (isAllEmployees == false) {
            employeesListViewDataList.addAll(selectFreeEmployees());
        } else {
            employeesListViewDataList.addAll(selectAllEmployees());
        }

        employeesListViewDataList.forEach(item -> {
            item.initPaneContainer();
            setSourceDragAndDrop(item.getPaneContainer());
            listView.getItems().add(item.getPaneContainer());
            employeesNamesList.add(item.getFullName());
        });

        listView.getSelectionModel().select(selectedRowIndex);
        listView.getFocusModel().focus(selectedRowIndex);
        listView.scrollTo(selectedRowIndex);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private void initComboBoxSearch(){
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");

        comboBoxSearch.setItems(employeesNamesList);

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

    public void searchInListView() {
        Object comboBoxListenerValue = comboBoxListener.getValue();
        Integer employeeID = null;
        for (DTOEmployees item : employeesListViewDataList) {
            if (item.getString().equals(comboBoxListenerValue)) {
                employeeID = item.getId();
            }
        }
        if (employeeID != null) {
            int i = 0;
            for (Pane pane : listView.getItems()) {
                if (Integer.parseInt(pane.getId()) == employeeID) {
                    listView.getSelectionModel().select(i);
                    listView.getFocusModel().focus(i);
                    listView.scrollTo(i);
                }
                i++;
            }
        }
    }

    private void setSourceDragAndDrop(Pane pane) {
        pane.setOnDragDetected(event -> {
            Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(pane.getId());
            db.setContent(content);
            event.consume();
        });

        pane.setOnDragDone(event -> {
            event.consume();
        });
    }

    public void setAllEmployees(boolean allEmployees) {
        isAllEmployees = allEmployees;
    }

    public boolean isAllEmployees() {
        return isAllEmployees;
    }

}
