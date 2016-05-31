package employees;

import employees.dto.DTOEmployees;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import subsidiary.classes.EditPanel;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;

import static employees.ODBC_PubsBD.selectExemptEmployeesList;
import static employees.ODBC_PubsBD.selectWorkingEmployeesList;

public class WindowEmployeesController {

    public GridPane gridPane;
    public StackPane stackPane;
    public ListView<DTOEmployees> listView;
    public GridPane listViewGridPane;
    public Pane headerPanel;
    public ChoiceBox exemptEmployeesChoiceBox;

    private Button saveButton;

    private ComboBox comboBoxSearch = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();

    private ObservableList<DTOEmployees> employeesListViewDataList = FXCollections.observableArrayList();
    private ObservableList<String> employeesNamesList = FXCollections.observableArrayList();

    private InfoEmployeesController infoEmployeesController;

    @FXML
    private void initialize(){
        listView.setPlaceholder(new Label("Тут покищо немає жодного працівника"));
        initComboBoxSearch();
        setListContextMenu();
        initExemptEmployeesChoiceBox();
        initOpenButton();
        initAddButton();

        initListView(false);
    }

    public void initListView(boolean isNeedSelectItems){
        Integer selectedRowIndex = listView.getSelectionModel().getSelectedIndex();
        employeesListViewDataList.clear();
        listView.getItems().clear();
        employeesNamesList.clear();
        setListViewCellFactory();

        if (exemptEmployeesChoiceBox.getValue().equals("Працюючі")) {
            employeesListViewDataList.addAll(selectWorkingEmployeesList());
        } else {
            employeesListViewDataList.addAll(selectExemptEmployeesList());
        }

        employeesListViewDataList.forEach(item -> employeesNamesList.add(item.getFullName()));

        listView.setItems(employeesListViewDataList);

        if (isNeedSelectItems) {
            listView.getSelectionModel().select(selectedRowIndex);
            listView.getFocusModel().focus(selectedRowIndex);
            listView.scrollTo(selectedRowIndex);
            initInfoEmployees(listView.getSelectionModel().getSelectedItem());
        }

        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private void initExemptEmployeesChoiceBox(){
        exemptEmployeesChoiceBox.getItems().addAll("Працюючі", "Звільнені");
        exemptEmployeesChoiceBox.setTooltip(new Tooltip("Показувати: працюючі \\ звільнені працівники"));
        exemptEmployeesChoiceBox.setValue(exemptEmployeesChoiceBox.getItems().get(0));

        exemptEmployeesChoiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
            initListView(false);
        });
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

    private void setListContextMenu(){
        MenuItem infoItem = new MenuItem("Переглянути");
        listView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if (listView.getSelectionModel().getSelectedItem() == null) {
                infoItem.setDisable(true);
            } else {
                infoItem.setDisable(false);
            }
        });
        infoItem.setOnAction((ActionEvent event) -> {
            initInfoEmployees(listView.getSelectionModel().getSelectedItem());
        });
        infoItem.setDisable(true);

        MenuItem addItem = new MenuItem("Додати нового працівника");
        addItem.setOnAction((ActionEvent event) -> {
            initInfoEmployees(new DTOEmployees(null, null, null, null, null, LocalDate.now(), null, null, 8, null));
        });

        final javafx.scene.control.ContextMenu cellMenu = new javafx.scene.control.ContextMenu();
        cellMenu.getItems().addAll(infoItem, addItem);

        listView.setContextMenu(cellMenu);
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
            stackPane.getChildren().add(fxmlLoader.load());
            infoEmployeesController = fxmlLoader.getController();
            infoEmployeesController.setWindowEmployeesController(this);
            infoEmployeesController.setDtoEmployees(dtoEmployees);
            infoEmployeesController.initWindow();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private void removeGridPaneChildren() {
        saveButton.setVisible(false);
        stackPane.getChildren().clear();
        infoEmployeesController = null;
    }

    private void initAddButton(){
        final EditPanel editPanel = new EditPanel();
        Button addButton = editPanel.getAddButton();
        addButton.getStylesheets().add(getClass().getResource("/employees/ListsButtonStyle.css").toExternalForm());
        addButton.setTooltip(new Tooltip("Додати нового працівника"));
        addButton.setOnAction(event -> {
            initInfoEmployees(new DTOEmployees(null, null, null, null, null, LocalDate.now(), null, null, 8, null));
        });
        headerPanel.getChildren().add(addButton);
        addButton.setLayoutX(29);
        addButton.setLayoutY(1);
    }

    private void initOpenButton(){
        final EditPanel editPanel = new EditPanel(listView);
        Button openButton = editPanel.getOpenButton();
        openButton.getStylesheets().add(getClass().getResource("/employees/ListsButtonStyle.css").toExternalForm());
        openButton.setTooltip(new Tooltip("Переглянути інформацію про працівника"));
        openButton.setOnAction(event -> {
            initInfoEmployees(listView.getSelectionModel().getSelectedItem());
        });
        headerPanel.getChildren().add(openButton);
        openButton.setLayoutX(5);
        openButton.setLayoutY(1);
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }

}
