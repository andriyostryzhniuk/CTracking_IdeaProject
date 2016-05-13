package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 03/23/2016.
 */
public class DtoStock extends DtoStockListView {
    private int id;
    private String name;
    private String stockCategory;
    private String notes;
    private Pane paneContainer = new Pane();

    public DtoStock() {
    }

    public DtoStock(int id, String name, String stockCategory, String notes) {
        this.id = id;
        this.name = name;
        this.stockCategory = stockCategory;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Pane getPaneContainer() {
        return paneContainer;
    }

    public void setPaneContainer(Pane paneContainer) {
        this.paneContainer = paneContainer;
    }

    public void initStockPaneContainer(){
        paneContainer.setId("0:"+Integer.toString(id));
        paneContainer.setStyle("-fx-background-color: rgba(105, 105, 105, .5);");
        Label label = new Label(name);
        paneContainer.getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);
    }

    @Override
    public int getNumberOfStock() {
        return 0;
    }

    @Override
    public void initCategoryPaneContainer(int numberOfStockGranted) {

    }


}
