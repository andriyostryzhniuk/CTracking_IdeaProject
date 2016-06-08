package employees.dto;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class DTOTelephones {

    private Integer recordId;
    private Integer subscriberId;
    private String number;
    private GridPane gridPane;
    private TextField textField = new TextField();
    private Button rejectButton;

    public DTOTelephones() {
    }

    public DTOTelephones(Integer recordId, Integer subscriberId, String number) {
        this.recordId = recordId;
        this.subscriberId = subscriberId;
        this.number = number;
    }

    public DTOTelephones(String number) {
        this.number = number;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Integer subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public GridPane getGridPane() {
        if (gridPane == null) {
            initGridPane();
        }
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    private void initGridPane() {
        if (recordId == null) {
            Platform.runLater(() -> textField.requestFocus());
        }
        gridPane = new GridPane();
        gridPane.setMinWidth(170);
        gridPane.setMaxWidth(170);
        textField.setMinWidth(170);
        textField.setMaxWidth(170);
        textField.setPromptText("Введіть номер телефону");
        textField.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(230, 230, 230);");
        textField.setText(number);
        gridPane.add(textField, 0 ,0);
        rejectButton = initRejectButton();
        gridPane.add(rejectButton, 0, 0);
        gridPane.setMargin(rejectButton, new Insets(0, 0, 0, 146));

        gridPane.setOnMouseEntered(event -> {
            rejectButton.setVisible(true);
            if (! textField.getStyleClass().contains("focused")) {
                textField.setStyle("-fx-background-color: rgb(90, 93, 95); -fx-text-fill: rgb(230, 230, 230);");
            }
        });
        gridPane.setOnMouseExited(event -> {
            if (! textField.getStyleClass().contains("focused")) {
                textField.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(230, 230, 230);");
            }
            rejectButton.setVisible(false);
        });

        rejectButton.setOnAction(event -> textField.setText(null));
    }

    private Button initRejectButton(){
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon_light.png"));
        button.getStylesheets().add(getClass().getResource("/employees/RejectButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setTooltip(new Tooltip("Видалити телефон"));
        button.setVisible(false);
        return button;
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getRejectButton() {
        return rejectButton;
    }
}
