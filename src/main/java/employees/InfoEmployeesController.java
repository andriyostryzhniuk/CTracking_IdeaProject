package employees;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalTime;

public class InfoEmployeesController {

    public Label infoLabel;

    @FXML
    private void initialize(){
        infoLabel.setText(LocalTime.now().toString());
    }

}
