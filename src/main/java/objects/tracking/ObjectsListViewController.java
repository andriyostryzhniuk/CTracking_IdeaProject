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
import objects.tracking.dto.DTOObjects;
import objects.tracking.dto.DTOObjectEmployees;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static objects.tracking.ODBC_PubsBD.selectObjects;

public class ObjectsListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();

    private ObservableList<DTOObjects> objectsListViewDataList = FXCollections.observableArrayList();
    private ObservableList<String> objectsNamesList = FXCollections.observableArrayList();

    private List<DTOObjectEmployees> resultList = new ArrayList<>();

    private EmployeesListViewController employeesListViewController;

    @FXML
    public void initialize(){
        initComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);
        initList();
    }

    public void initList(){
        objectsListViewDataList.clear();
        listView.getItems().clear();
        objectsNamesList.clear();
        objectsListViewDataList.addAll(selectObjects());

        objectsListViewDataList.forEach(item -> {
            item.initPaneContainer();
            setTargetDragAndDrop(item.getPaneContainer());
            listView.getItems().add(item.getPaneContainer());
            objectsNamesList.add(item.getAddress());

            item.getObjectEmployeesList().forEach(list -> {
                System.out.println(list.getFullName());
            });
            System.out.println("--------------");

        });

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

                resultList.add(new DTOObjectEmployees(null, objectId, employeeId, new Date(), null));

                success = true;
            }

            employeesListViewController.setDisablePane(db.getString());

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void setResultList(List<DTOObjectEmployees> resultList) {
        this.resultList = resultList;
    }

    public void setEmployeesListViewController(EmployeesListViewController employeesListViewController) {
        this.employeesListViewController = employeesListViewController;
    }
}
