package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 03/23/2016.
 */
public class DtoStock {
    private int id;
    private String name;
    private String stockCategory;
    private Pane paneContainer = new Pane();

    public DtoStock() {
    }

    public DtoStock(int id, String name, String stockCategory) {
        this.id = id;
        this.name = name;
        this.stockCategory = stockCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockCategory() {
        return stockCategory;
    }

    public void setStockCategory(String stockCategory) {
        this.stockCategory = stockCategory;
    }

    public Pane getPaneContainer() {
        return paneContainer;
    }

    public void setPaneContainer(Pane paneContainer) {
        this.paneContainer = paneContainer;
    }

    public void initPaneContainer (){
        paneContainer.setId("0:"+Integer.toString(id));
        paneContainer.setStyle("-fx-background-color: rgba(105, 105, 105, .5);");
        Label label = new Label(name);
        paneContainer.getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);
    }
}
