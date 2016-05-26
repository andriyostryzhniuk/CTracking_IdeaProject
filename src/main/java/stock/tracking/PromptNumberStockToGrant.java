package stock.tracking;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import overridden.elements.number.spinner.NumberSpinner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class PromptNumberStockToGrant {
    private boolean isException = false;
    private int numberOfStockToGrant = 0;
    private int stockCategoryId;
    private String employeesName;
    private String objectName;

    public PromptNumberStockToGrant(int stockCategoryId, String employeesName, String objectName) {
        this.stockCategoryId = stockCategoryId;
        this.employeesName = employeesName;
        this.objectName = objectName;
    }

    public int showPrompt(Window window, int maxValue) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Prompt");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Group root = new Group();
        Scene scene = new Scene(root, 320, 162, Color.rgb(0, 0, 0, 0));

        Rectangle background = initRectangle();

        VBox vBox = new VBox(2);
        vBox.setPadding(new Insets(100, 0, 0, 40));
        HBox hBox = new HBox(3);

        Label exceptionLabel = new Label("Значення неприпустиме");
        exceptionLabel.setStyle("-fx-text-fill: red");

        NumberSpinner numberSpinner = initNumberSpinner(maxValue);

        Button okButton = new Button("Прийняти");
        okButton.setOnAction((ActionEvent event) -> {
            if (!isException) {
                numberOfStockToGrant = numberSpinner.getValue().intValue();
                primaryStage.close();
            } else {
                vBox.getChildren().add(exceptionLabel);
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            vBox.getChildren().remove(exceptionLabel);
                        });
                    }
                }, 1000, 5000);
            }
        });

        Button cancelButton = new Button("Скасувати");
        cancelButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });

        Pane labelPane = initLabelPane(maxValue);

        hBox.getChildren().addAll(numberSpinner, okButton, cancelButton);
        hBox.setMargin(numberSpinner, new Insets(0, 3, 0, 0));
        vBox.getChildren().add(hBox);
        root.getChildren().addAll(background, labelPane, vBox);

        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(window);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

        return numberOfStockToGrant;
    }

    private NumberSpinner initNumberSpinner(int maxValue){
        NumberSpinner numberSpinner = new NumberSpinner();
        numberSpinner.setPrefWidth(80);
        numberSpinner.setValue(1);
        numberSpinner.setMinValue(0);
        numberSpinner.setMaxValue(maxValue);
        numberSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            isException = false;
            try {
                if (newValue.intValue() < 1) {
                    isException = true;
                }
            } catch (NullPointerException e) {
                isException = true;
            }
        });
        return numberSpinner;
    }

    private Rectangle initRectangle (){
        Color foreground = Color.rgb(255, 255, 255, 0.9);
        Rectangle background = new Rectangle(320, 162);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.rgb(0, 0, 0, 0.55));
        background.setStroke(foreground);
        background.setStrokeWidth(1.5);
        return background;
    }

    private Pane initLabelPane(int maxValue){
        String stockCategoryName = ODBC_PubsBDForStock.selectStockCategoryNameWithId(this.stockCategoryId);
        if (stockCategoryName.length() > 23) {
            stockCategoryName = stockCategoryName.replace(stockCategoryName, stockCategoryName.substring(0, 22)+"...");
        }
        Label labelStock = new Label(stockCategoryName+"\nДоступно одиниць: "+maxValue);

        String employeesName = this.employeesName;
        if (employeesName != null) {
            if (employeesName.length() > 19) {
                employeesName = employeesName.replace(employeesName, employeesName.substring(0, 19)+"...");
            }
        } else {
            employeesName = "-";
        }
        Label labelEmployee = new Label("Кому:\n"+employeesName);

        String objectName = this.objectName;
        if (objectName != null) {
            if (objectName.length() > 19) {
                objectName = objectName.replace(objectName, objectName.substring(0, 19)+"...");
            }
        } else {
            objectName = "-";
        }
        Label labelObject = new Label("Об'єкт:\n"+objectName);

        labelStock.setStyle("-fx-text-fill: white");
        labelEmployee.setStyle("-fx-text-fill: white");
        labelObject.setStyle("-fx-text-fill: white");

        Pane labelPane = new Pane();
        labelPane.getChildren().addAll(labelStock, labelEmployee, labelObject);
        labelStock.setLayoutX(10);
        labelStock.setLayoutY(10);
        labelEmployee.setLayoutX(180);
        labelEmployee.setLayoutY(10);
        labelObject.setLayoutX(180);
        labelObject.setLayoutY(50);
        return labelPane;
    }
}
