package stock.tracking.dto;

import javafx.scene.layout.Pane;

/**
 * Created by Andriy on 04/11/2016.
 */
public abstract class DtoStockListView {
    public abstract int getId();

    public abstract String getName();

    public abstract int getNumberOfStock();

    public abstract Pane getPaneContainer();

    public abstract void initStockPaneContainer();

    public abstract void initCategoryPaneContainer(int numberOfStockGranted);
}
