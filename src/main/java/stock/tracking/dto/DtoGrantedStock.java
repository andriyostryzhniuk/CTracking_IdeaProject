package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Created by Andriy on 04/12/2016.
 */
public class DtoGrantedStock extends DtoStock {

    public DtoGrantedStock() {
    }

    public DtoGrantedStock(int id, String name, String stockCategory) {
        super(id, name, stockCategory);
    }

    public void initStockPaneContainer(){
        getPaneContainer().setId("0:"+Integer.toString(getId()));
        getPaneContainer().setStyle("-fx-border-color: rgba(105, 105, 105, .5);");
        Label label = new Label(getName());
        getPaneContainer().getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);

        getPaneContainer().setOnMouseEntered((MouseEvent event) -> {
            getPaneContainer().setStyle("-fx-background-color: rgba(220, 220, 220, .2); -fx-border-color: rgba(105, 105, 105, .5);");
        });

        getPaneContainer().setOnMouseExited((MouseEvent event) -> {
            getPaneContainer().setStyle("-fx-background-color: transparent; -fx-border-color: rgba(105, 105, 105, .5);");
        });
    }
}
