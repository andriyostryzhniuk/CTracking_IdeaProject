package stock.tracking.dto;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Created by Andriy on 04/12/2016.
 */
public class DtoGrantedStock extends DtoStock {
    private int stockCategoryId;

    public DtoGrantedStock() {
    }

    public DtoGrantedStock(int id, String name, String stockCategory, int stockCategoryId) {
        super(id, name, stockCategory);
        this.stockCategoryId = stockCategoryId;
    }

    public int getStockCategoryId() {
        return stockCategoryId;
    }

    public void setStockCategoryId(int stockCategoryId) {
        this.stockCategoryId = stockCategoryId;
    }

    public void initStockPaneContainer(){
        getPaneContainer().setId("0:"+Integer.toString(getId()));
        getPaneContainer().setStyle("-fx-border-color: rgba(105, 105, 105, .5);");
        Label label = new Label(getName());
        getPaneContainer().getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);

        Button rejectStockButton = new Button("X");
        rejectStockButton.setDisable(true);
        rejectStockButton.setVisible(false);
        getPaneContainer().getChildren().add(rejectStockButton);
        rejectStockButton.setLayoutX(215);
        rejectStockButton.setLayoutY(3);

        getPaneContainer().setOnMouseEntered((MouseEvent event) -> {
            getPaneContainer().setStyle("-fx-background-color: rgba(220, 220, 220, .2); -fx-border-color: rgba(105, 105, 105, .5);");
            rejectStockButton.setDisable(false);
            rejectStockButton.setVisible(true);
        });

        getPaneContainer().setOnMouseExited((MouseEvent event) -> {
            rejectStockButton.setDisable(true);
            rejectStockButton.setVisible(false);
            getPaneContainer().setStyle("-fx-background-color: transparent; -fx-border-color: rgba(105, 105, 105, .5);");
        });

    }
}
