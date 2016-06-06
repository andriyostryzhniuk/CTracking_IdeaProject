package object;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.stage.Stage;

public class SelectingMethodOfAddingController {

    public Group rootGroup;

    private InfoObjectsController infoObjectsController;

    public void chooseButtonAction(ActionEvent actionEvent) {
        hide();
        infoObjectsController.showSettingsCustomersWindow();
        close();
    }

    public void createButtonAction(ActionEvent actionEvent) {
        infoObjectsController.initCustomersView(null);
        close();
    }

    public void escapeButtonAction(ActionEvent actionEvent) {
        close();
    }

    private void close(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        stage.close();
    }

    private void hide(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        stage.toBack();
        rootGroup.setDisable(true);
        rootGroup.setVisible(false);
    }

    public void setInfoObjectsController(InfoObjectsController infoObjectsController) {
        this.infoObjectsController = infoObjectsController;
    }
}

