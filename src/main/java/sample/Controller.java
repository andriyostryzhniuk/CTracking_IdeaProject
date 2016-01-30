package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import java.io.IOException;

public class Controller {

    public GridPane mainGridPane;

    public void createEditTableTracking(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employeesWorkTracking.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

//        DateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("uk"));
//        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
//        Date date = new Date();
//
//        mainGridPane.add(new Label(dayFormat.format(date).toString().substring(0, 1).toUpperCase()+
//                dayFormat.format(date).toString().substring(1)+"\n"+
//                dateFormat.format(date)), 3, 1);
    }
}
