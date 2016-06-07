package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Locale;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale.setDefault(new Locale("uk"));
        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("main/MainWindow.fxml"));
        primaryStage.setTitle("CTracking");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/overridden.elements/DatePickerStyle.css");
        scene.getStylesheets().add("/styles/Stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        DB_Connector.getDataSource();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
