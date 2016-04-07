package stock.tracking.dto;

import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 04/04/2016.
 */
public abstract class DtoLiableListView {
    public abstract Pane getPaneContainer();

    public abstract void initPaneContainer();

    public abstract int getId();

    public abstract String getString();

}
