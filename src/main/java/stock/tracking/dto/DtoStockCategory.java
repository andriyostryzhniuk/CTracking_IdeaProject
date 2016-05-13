package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 03/28/2016.
 */
public class DtoStockCategory extends DtoStockListView {
    private int id;
    private String name;
    private int numberOfStock;
    private Pane paneContainer = new Pane();

    public DtoStockCategory() {
    }

    public DtoStockCategory(int id, String name, int numberOfStock) {
        this.id = id;
        this.name = name;
        this.numberOfStock = numberOfStock;
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

    public int getNumberOfStock() {
        return numberOfStock;
    }

    public void setNumberOfStock(int numberOfStock) {
        this.numberOfStock = numberOfStock;
    }

    public Pane getPaneContainer() {
        return paneContainer;
    }

    public void setPaneContainer(Pane paneContainer) {
        this.paneContainer = paneContainer;
    }

    public void initCategoryPaneContainer(int numberOfStockGranted){
        paneContainer.setId("1:"+Integer.toString(id));
        paneContainer.setStyle("-fx-background-color: rgba(105, 105, 105, .5);");
        Label label = new Label(name);
        paneContainer.getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);

        Label numberOfStockLabel = new Label("Кількість: " + Integer.toString(numberOfStock - numberOfStockGranted));
        paneContainer.getChildren().add(numberOfStockLabel);
        numberOfStockLabel.setLayoutX(210);
        numberOfStockLabel.setLayoutY(30);
    }

    @Override
    public void initStockPaneContainer() {

    }
}
