package employees;

import employees.dto.DTOSkills;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.util.List;

import static employees.ODBC_PubsBD.selectSkillsList;

public class AddingEmployeesSkillController<T extends DTOSkills> {
    public ListView<T> listView;

    private ObservableList<T> skillsDataList = FXCollections.observableArrayList();

    private List<T> hasAddedSkillsList;

    @FXML
    public void initialize(){
        initContextMenu();
    }

    public void initListView(){
        skillsDataList.clear();
        listView.getItems().clear();

        selectSkillsList().forEach(item -> {
            boolean hasAdded = false;
            for (DTOSkills dtoSkills : hasAddedSkillsList) {
                if (dtoSkills.getId() == item.getId()) {
                    hasAdded = true;
                    break;
                }
            }
            if (! hasAdded) {
                skillsDataList.add((T) item);
            }
        });

        listView.setItems(skillsDataList);
    }

    public void add(ActionEvent actionEvent) {
//        showEditingRecordWindow(null);
        close();
    }

    public void closeAction(ActionEvent actionEvent) {
//        menuTableView.initDishesTypeComboBoxItems();
//        menuTableView.initTableView();
        close();
    }

    private void initContextMenu() {
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> {
//            showEditingRecordWindow(null);
        });

        MenuItem createItem = new MenuItem("Створити нову спеціальність");
        createItem.setOnAction((ActionEvent event) -> {
//            showEditingRecordWindow(null);
        });

        MenuItem editItem = new MenuItem("Редагувати спеціальність");
        editItem.setOnAction((ActionEvent event) -> {
//            DtoDishesType dtoDishesType = listView.getSelectionModel().getSelectedItem();
//            showEditingRecordWindow(dtoDishesType);
        });

        MenuItem removeItem = new MenuItem("Видалити спеціальність");
        removeItem.setOnAction((ActionEvent event) -> {
//            removeRecord();
        });
        final ContextMenu cellMenu = new ContextMenu();
        cellMenu.getItems().addAll(addItem, createItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }

    private void close(){
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

//    private void showEditingRecordWindow(DtoDishesType dtoDishesType){
//        Stage primaryStage = new Stage();
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/dishesType/PromptAddingWindow.fxml"));
//        Parent root = null;
//        try {
//            root = fxmlLoader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        PromptAddingWindowController promptAddingWindowController = fxmlLoader.getController();
//        promptAddingWindowController.setDishesTypeWindowController(this);
//        promptAddingWindowController.setDtoDishesType(dtoDishesType);
//
//        primaryStage.initStyle(StageStyle.TRANSPARENT);
//        primaryStage.setScene(new Scene(root, 300, 162, Color.rgb(0, 0, 0, 0)));
//        primaryStage.initModality(Modality.WINDOW_MODAL);
//        primaryStage.initOwner(listView.getScene().getWindow());
//        primaryStage.showAndWait();
//
//    }

//    public void removeRecord(){
//        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
//        if (! alertWindow.showDeletingWarning()) {
//            return;
//        }
//
//        DtoDishesType dtoDishesType = listView.getSelectionModel().getSelectedItem();
//        List<Object> objectList = new LinkedList<>();
//        objectList.add(dtoDishesType.getId());
//        Boolean isSuccessful = (Boolean) sendARequestToTheServer(ClientCommandTypes.DELETE_DISHES_TYPE, objectList).get(0);
//        if (! isSuccessful) {
//            alertWindow = new AlertWindow(Alert.AlertType.ERROR);
//            alertWindow.showDeletingError();
//        }
//        initListView();
//    }

//    public void setMenuTableView(MenuTableView menuTableView) {
//        this.menuTableView = menuTableView;
//    }


    public void setHasAddedSkillsList(List<T> hasAddedSkillsList) {
        this.hasAddedSkillsList = hasAddedSkillsList;
    }
}
