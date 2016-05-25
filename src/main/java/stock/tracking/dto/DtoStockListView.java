package stock.tracking.dto;

import javafx.scene.layout.Pane;

public abstract class DtoStockListView {
    public abstract int getId();

    public abstract String getName();

    public abstract int getNumberOfStock();

    public abstract Pane getPaneContainer();

    public abstract void initStockPaneContainer();

    public abstract String getNotes();
}
