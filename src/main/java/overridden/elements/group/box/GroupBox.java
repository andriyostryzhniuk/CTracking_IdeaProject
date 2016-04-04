package overridden.elements.group.box;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class GroupBox extends StackPane {
    public GroupBox(Node content, String titleString, int titleTranslateX) {
        Label title = new Label(" " + titleString + " ");
        title.getStyleClass().add("bordered-titled-title");
        title.setStyle("-fx-translate-x: "+ Integer.toString(titleTranslateX) +";");
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        StackPane contentPane = new StackPane();
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);

        getStyleClass().add("bordered-titled-border");
        getChildren().addAll(title, contentPane);
    }
}
