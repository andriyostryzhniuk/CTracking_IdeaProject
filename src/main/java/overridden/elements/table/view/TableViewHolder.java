package overridden.elements.table.view;

import javafx.beans.binding.NumberBinding;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Created by Andriy on 03/04/2016.
 */
public class TableViewHolder<s> extends StackPane {
    private TableView<s> table;

    public TableViewHolder(){
        this.table = new TableView<>();
        final GridPane grid = new GridPane();

        this.table.getColumns().addListener((ListChangeListener<TableColumn>) arg0 -> {

            grid.getColumnConstraints().clear();
            ColumnConstraints[] arr1 = new ColumnConstraints[TableViewHolder.this.table.getColumns().size()];
            StackPane[] arr2 = new StackPane[TableViewHolder.this.table.getColumns().size()];

            int i=0;
            for(TableColumn column : TableViewHolder.this.table.getColumns()){
                CustomTableColumn col = (CustomTableColumn)column;
                ColumnConstraints consta = new ColumnConstraints();
                consta.setPercentWidth(col.getPercentWidth());

                StackPane sp = new StackPane();
                if(i==0){
                    // Quick fix for not showing the horizantal scroll bar.
                    NumberBinding diff = sp.widthProperty().subtract(3.75);
                    column.prefWidthProperty().bind(diff);
                }else{
                    column.prefWidthProperty().bind(sp.widthProperty());
                }
                arr1[i] = consta;
                arr2[i] = sp;
                i++;
            }

            grid.getColumnConstraints().addAll(arr1);
            grid.addRow(0, arr2);
        });

        getChildren().addAll(grid,table);
    }

    public TableView<s> getTableView(){
        return this.table;
    }
}
