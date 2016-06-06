package employees;

import employees.dto.DTOSkills;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import subsidiary.classes.AlertWindow;

import java.io.IOException;
import java.util.List;

import static employees.ODBC_PubsBD.deleteFromSkills;
import static employees.ODBC_PubsBD.selectSkillsList;

public class AddingEmployeesSkillController<T extends DTOSkills> {

    public ListView<T> listView;
    public Button addButton;
    public Button editButton;
    public Button removeButton;

    private ObservableList<T> skillsDataList = FXCollections.observableArrayList();
    private InfoEmployeesController infoEmployeesController;

    private List<T> hasAddedSkillsList;

    @FXML
    public void initialize(){
        initContextMenu();

        listView.getSelectionModel().selectedItemProperty().addListener(event -> {
            if ((listView.getSelectionModel().getSelectedItem()) != null) {
                setButtonsDisable(false);
            } else {
                setButtonsDisable(true);
            }
        });
    }

    public void initListView(){
        skillsDataList.clear();
        listView.getItems().clear();

        selectSkillsList().forEach(item -> {
            boolean hasAdded = false;
            for (DTOSkills dtoSkills : hasAddedSkillsList) {
                if (dtoSkills.getSkillsId() == item.getSkillsId()) {
                    hasAdded = true;
                    break;
                }
            }
            if (! hasAdded) {
                skillsDataList.add((T) item);
            }
        });

        listView.setItems(skillsDataList);
        setListViewCellFactory();
    }

    private void addSkill(){
        T skill = listView.getSelectionModel().getSelectedItem();
        hasAddedSkillsList.add(skill);
        infoEmployeesController.getSkillsListToAdding().add(skill);
        close();
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void initContextMenu() {
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> addSkill());
        addItem.setDisable(true);

        MenuItem createItem = new MenuItem("Створити нову спеціальність");
        createItem.setOnAction((ActionEvent event) -> showEditingSkillsWindow(null));

        MenuItem editItem = new MenuItem("Редагувати спеціальність");
        editItem.setOnAction((ActionEvent event) -> {
            showEditingSkillsWindow(listView.getSelectionModel().getSelectedItem());
        });
        editItem.setDisable(true);

        MenuItem removeItem = new MenuItem("Видалити спеціальність");
        removeItem.setOnAction((ActionEvent event) -> removeSkill());

        removeItem.setDisable(true);
        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(addItem, createItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }

    public void addButtonAction(ActionEvent actionEvent) {
        addSkill();
    }

    public void createButtonAction(ActionEvent actionEvent) {
        showEditingSkillsWindow(null);
    }

    public void editButtonAction(ActionEvent actionEvent) {
        showEditingSkillsWindow(listView.getSelectionModel().getSelectedItem());
    }

    public void removeButtonAction(ActionEvent actionEvent) {
        removeSkill();
    }

    private void close(){
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    private void showEditingSkillsWindow(DTOSkills dtoSkills){
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employees/EditingSkills.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditingSkillsController editingSkillsController = fxmlLoader.getController();
        editingSkillsController.setAddingEmployeesSkillController(this);
        editingSkillsController.setDtoSkills(dtoSkills);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 300, 162, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(listView.getScene().getWindow());
        primaryStage.showAndWait();

    }

    public void removeSkill(){
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }
        deleteFromSkills(listView.getSelectionModel().getSelectedItem());
        initListView();
    }

    private void setButtonsDisable(boolean isDisable){
        addButton.setDisable(isDisable);
        editButton.setDisable(isDisable);
        removeButton.setDisable(isDisable);
        listView.getContextMenu().getItems().get(0).setDisable(isDisable);
        listView.getContextMenu().getItems().get(2).setDisable(isDisable);
        listView.getContextMenu().getItems().get(3).setDisable(isDisable);
    }

    private void setListViewCellFactory(){
        listView.setCellFactory(listCell -> {
            final ListCell<T> cell = new ListCell<T>() {
                @Override
                protected void updateItem(T t, boolean b) {
                    super.updateItem(t, b);
                    if (t != null) {
                        setText(t.getSkill());
                    }

                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                addSkill();
                            }
                        }
                    });
                }
            };
            return cell;
        });
    }

    public void setHasAddedSkillsList(List<T> hasAddedSkillsList) {
        this.hasAddedSkillsList = hasAddedSkillsList;
    }

    public void setInfoEmployeesController(InfoEmployeesController infoEmployeesController) {
        this.infoEmployeesController = infoEmployeesController;
    }

}
