package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Controller {

    public GridPane mainGridPane;

    public void init(ActionEvent actionEvent) {
        ObservableList<String> usersData = FXCollections.observableArrayList();
        usersData.add("yjryryhrty");
        usersData.add("tytyujryuryu");
        ListView listView = new ListView(usersData);
        mainGridPane.add(listView, 2, 2);

        DateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("uk"));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date date = new Date();

        mainGridPane.add(new Label(dayFormat.format(date).toString().substring(0, 1).toUpperCase()+
                dayFormat.format(date).toString().substring(1)+"\n"+
                dateFormat.format(date)), 3, 1);
    }
}
