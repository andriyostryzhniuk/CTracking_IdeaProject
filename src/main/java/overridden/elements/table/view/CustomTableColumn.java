package overridden.elements.table.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableColumn;

/**
 * Created by Andriy on 03/04/2016.
 */
public class CustomTableColumn<S,T> extends TableColumn<S, T> {
    private SimpleDoubleProperty percentWidth = new SimpleDoubleProperty();

    public CustomTableColumn(String columnName){
        super(columnName);
    }

    public SimpleDoubleProperty percentWidth() {
        return percentWidth;
    }

    public double getPercentWidth() {
        return percentWidth.get();
    }

    public void setPercentWidth(double percentWidth) {
        this.percentWidth.set(percentWidth);
    }
}
