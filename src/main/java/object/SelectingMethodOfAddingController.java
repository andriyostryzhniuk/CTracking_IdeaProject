package object;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SelectingMethodOfAddingController {

    public Group rootGroup;

    private InfoObjectsController infoObjectsController;

    public void chooseButtonAction(ActionEvent actionEvent) {
        hide();
        showSettingsCustomersWindow();
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

    private void showSettingsCustomersWindow() {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/object/SettingsCustomers.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsCustomersController settingsCustomersController = fxmlLoader.getController();
        settingsCustomersController.setInfoObjectsController(infoObjectsController);
        settingsCustomersController.initTableView();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 460, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(infoObjectsController.getRootGridPane().getScene().getWindow());
        primaryStage.showAndWait();
    }

    public void setInfoObjectsController(InfoObjectsController infoObjectsController) {
        this.infoObjectsController = infoObjectsController;
    }
}

