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
            System.out.println("mouse detected");
        });

        getPaneContainer().setOnMouseExited((MouseEvent event) -> {
            System.out.println("mouse exited");
        });
    }
}
