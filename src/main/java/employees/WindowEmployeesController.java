package employees;

import employees.dto.DTOEmployees;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;

import java.io.IOException;
import java.io.UncheckedIOException;

import static employees.ODBC_PubsBD.selectEmployeesList;

public class WindowEmployeesController {

    public GridPane gridPane;
    public ListView<DTOEmployees> listView;
    public GridPane listViewGridPane;

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();

    private ObservableList<DTOEmployees> employeesListViewDataList = FXCollections.observableArrayList();
    private ObservableList<String> employeesNamesList = FXCollections.observableArrayList();

    private Node nodeInfoEmployees;
    private InfoEmployeesController infoEmployeesController;

    @FXML
    private void initialize(){
        listView.setPlaceholder(new Label("Не додано жодного працівника"));
        setListViewCellFactory();
        initComboBoxSearch();

        initListView();
    }

    public void initListView(){
        employeesListViewDataList.clear();
        listView.getItems().clear();
        employeesNamesList.clear();

        employeesListViewDataList.addAll(selectEmployeesList());

        employeesListViewDataList.forEach(item -> {
            employeesNamesList.add(item.getFullName());
        });

        listView.setItems(employeesListViewDataList);

        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private void setListViewCellFactory(){
        listView.setCellFactory(listCell -> {
            final ListCell<DTOEmployees> cell = new ListCell<DTOEmployees>() {
                @Override
                protected void updateItem(DTOEmployees t, boolean b) {
                    super.updateItem(t, b);
                    if (t != null) {
                        setText(t.getFullName());
                    }
                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                initInfoEmployees(getItem());
                            }
                        }
                    });
                }
            };
            return cell;
        });
    }

    private void initComboBoxSearch() {
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");
        comboBoxSearch.setMinWidth(300);

        listViewGridPane.add(comboBoxSearch, 0, 0);

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

    private void searchInListView() {
        Object comboBoxListenerValue = comboBoxListener.getValue();

        int i = 0;
        for (DTOEmployees item : listView.getItems()) {
            if (item.getFullName().equals(comboBoxListenerValue)) {
                listView.getSelectionModel().select(i);
                listView.getFocusModel().focus(i);
                listView.scrollTo(i);
            }
            i++;
        }
    }

    private void initInfoEmployees(DTOEmployees dtoEmployees){
        removeGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees/InfoEmployees.fxml"));
        try {
            nodeInfoEmployees = fxmlLoader.load();
            gridPane.add(nodeInfoEmployees, 3, 0);
            infoEmployeesController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private void removeGridPaneChildren() {
        gridPane.getChildren().remove(nodeInfoEmployees);
        nodeInfoEmployees = null;
        infoEmployeesController = null;
    }

}
