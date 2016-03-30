package stock.tracking;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import numberSpinner.NumberSpinner;

/**
 * Created by Andriy on 03/29/2016.
 */
public class PromptOfNumberStock {
    private boolean isException = false;

    public void startPrompt(Window window) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Prompt");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Group root = new Group();
        Scene scene = new Scene(root, 320, 112, Color.rgb(0, 0, 0, 0));

        Color foreground = Color.rgb(255, 255, 255, 0.9);

        Rectangle background = new Rectangle(320, 112);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.rgb(0, 0, 0, 0.55));
        background.setStroke(foreground);
        background.setStrokeWidth(1.5);

        HBox hBox = new HBox(3);
        hBox.setPadding(new Insets(10, 0, 10, 10));

        NumberSpinner numberSpinner = new NumberSpinner();
        numberSpinner.setPrefWidth(80);
        numberSpinner.setValue(1);
        numberSpinner.setMinValue(0);
        numberSpinner.setOnAction((ActionEvent event) -> {
            System.out.println(numberSpinner.getValue());
        });
        numberSpinner.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observableValue, Number oldValue, Number newValue) {
//                change detected
                isException = false;
                try {
                    if (newValue.intValue() < 1) {
                        isException = true;
                    }
                } catch (NullPointerException e) {
                    isException = true;
                    System.out.println(e);
                }
            }
        });

        Button okButton = new Button("Прийняти");
        okButton.setOnAction((ActionEvent event) -> {
            if (!isException) {
                System.out.println(numberSpinner.getValue());
            } else {
                System.out.println("exception");
            }
        });

        Button cancelButton = new Button("Скасувати");
        cancelButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });

        hBox.getChildren().addAll(numberSpinner, okButton, cancelButton);
        root.getChildren().addAll(background, hBox);

        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(window);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }
}
