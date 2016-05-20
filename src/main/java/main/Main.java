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
        primaryStage.setTitle("Hello World! I'm little CTracking, but I'll be big smart program!");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/overridden.elements/DatePickerStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        DB_Connector.getDataSource();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
