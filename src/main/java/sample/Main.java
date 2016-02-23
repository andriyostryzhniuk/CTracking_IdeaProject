package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("MainWindow.fxml"));
        primaryStage.setTitle("Hello World! I'm little CTracking, but I'll be big smart program!");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("DatePickerStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        DB_Connector.getDataSource();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
