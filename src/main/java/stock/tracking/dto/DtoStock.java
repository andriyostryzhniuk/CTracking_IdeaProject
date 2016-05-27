package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.math.BigDecimal;

public class DtoStock extends DtoStockListView {
    private int id;
    private String name;
    private String stockCategory;
    private BigDecimal price;
    private String notes;
    private String repositoryName;
    private Pane paneContainer = new Pane();

    public DtoStock() {
    }

    public DtoStock(int id, String name, String stockCategory, String notes) {
        this.id = id;
        this.name = name;
        this.stockCategory = stockCategory;
        this.notes = notes;
    }

    public DtoStock(String name, String stockCategory) {
        this.name = name;
        this.stockCategory = stockCategory;
    }

    public DtoStock(int id, String name, String stockCategory, BigDecimal price, String notes, String repositoryName) {
        this.id = id;
        this.name = name;
        this.stockCategory = stockCategory;
        this.price = price;
        this.notes = notes;
        this.repositoryName = repositoryName;
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
        paneContainer.setId(Integer.toString(id));
        paneContainer.setStyle("-fx-background-color: rgba(105, 105, 105, .5);");
        Label label = new Label(name);
        paneContainer.getChildren().add(label);
        label.setLayoutX(5);
        label.setLayoutY(3);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public int getNumberOfStock() {
        return 0;
    }

}
