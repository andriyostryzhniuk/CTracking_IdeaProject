package stock.tracking.dto;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 04/03/2016.
 */
public class DtoObject extends DtoLiableListView {
    private int id;
    private String address;
    private Pane paneContainer = new Pane();

    public DtoObject() {
    }

    public DtoObject(int id, String address) {
        this.id = id;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getString() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        Label fullNameLabel = new Label(address);
        fullNameLabel.setMaxWidth(290);
        fullNameLabel.setWrapText(true);
        paneContainer.getChildren().add(fullNameLabel);
        fullNameLabel.setLayoutX(5);
        fullNameLabel.setLayoutY(3);
    }
}
