package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 03/24/2016.
 */
public class DtoEmployees extends DtoLiableListView {
    private int id;
    private String fullName;
    private Pane paneContainer = new Pane();

    public DtoEmployees() {
    }

    public DtoEmployees(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Pane getPaneContainer() {
        return paneContainer;
    }

    public void setPaneContainer(Pane paneContainer) {
        this.paneContainer = paneContainer;
    }

    public void initPaneContainer (){
        paneContainer.setId(Integer.toString(id));
        paneContainer.setStyle("-fx-background-color: rgba(105, 105, 105, .5);");
        Label fullNameLabel = new Label(fullName);
        paneContainer.getChildren().add(fullNameLabel);
        fullNameLabel.setLayoutX(5);
        fullNameLabel.setLayoutY(3);

        Label numberOfStockLabel = new Label();
        paneContainer.getChildren().add(numberOfStockLabel);
        numberOfStockLabel.setLayoutX(160);
        numberOfStockLabel.setLayoutY(30);
    }
}
