package employees.attendance.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import dto.DtoEmployeesFullName;
import javafx.scene.layout.*;
import java.util.stream.IntStream;

public class TableViewController<T extends DtoEmployeesFullName> {


    @FXML
    public StackPane rootBorderPane;
    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();

    @FXML
    public CustomTableColumn<T, String> colName = new CustomTableColumn<>("Працівник");
    public CustomTableColumn<T, GridPane> colDate1 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate2 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate3 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate4 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate5 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate6 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate7 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate8 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate9 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate10 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate11 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate12 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate13 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate14 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate15 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate16 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate17 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate18 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate19 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate20 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate21 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate22 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate23 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate24 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate25 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate26 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate27 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate28 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate29 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate30 = new CustomTableColumn<>(null);
    public CustomTableColumn<T, GridPane> colDate31 = new CustomTableColumn<>(null);

    public TableView<T> getTableView() {
        return tableView.getTableView();
    }

    private final ObservableList<T> employeesFullNameList = FXCollections.observableArrayList();

    public ObservableList<T> getEmployeesFullNameList() {
        return employeesFullNameList;
    }

    private final ObservableList<CustomTableColumn<T, GridPane>> colsDateList = FXCollections.observableArrayList();

    ObservableMap<Integer, ObservableMap<String, Integer>> itemsObservableMap = FXCollections.observableHashMap();

    @FXML
    public void initialize() {

        fillColsDateList();

        tableView.setStyle("-fx-accent: derive(-fx-control-inner-background, -20%); -fx-control-inner-background, -80%;");
        tableView.getTableView().setFixedCellSize(40.0);
        tableView.getTableView().getStylesheets().add(getClass().getResource("/employees.attendance.table/TableViewStyle.css").toExternalForm());

        colName.setPercentWidth(150.0);
        colName.setMinWidth(150.0);
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        setColsDateProperties();

        fillTableView();

        tableView.getTableView().setItems(employeesFullNameList);
        rootBorderPane.getChildren().add(tableView);

//        initCheckBox();
    }

    private void fillColsDateList() {
        colDate1.setCellValueFactory(new PropertyValueFactory<>("gridPane1"));
        colDate2.setCellValueFactory(new PropertyValueFactory<>("gridPane2"));
        colDate3.setCellValueFactory(new PropertyValueFactory<>("gridPane3"));
        colDate4.setCellValueFactory(new PropertyValueFactory<>("gridPane4"));
        colDate5.setCellValueFactory(new PropertyValueFactory<>("gridPane5"));
        colDate6.setCellValueFactory(new PropertyValueFactory<>("gridPane6"));
        colDate7.setCellValueFactory(new PropertyValueFactory<>("gridPane7"));
        colDate8.setCellValueFactory(new PropertyValueFactory<>("gridPane8"));
        colDate9.setCellValueFactory(new PropertyValueFactory<>("gridPane9"));
        colDate10.setCellValueFactory(new PropertyValueFactory<>("gridPane10"));
        colDate11.setCellValueFactory(new PropertyValueFactory<>("gridPane11"));
        colDate12.setCellValueFactory(new PropertyValueFactory<>("gridPane12"));
        colDate13.setCellValueFactory(new PropertyValueFactory<>("gridPane13"));
        colDate14.setCellValueFactory(new PropertyValueFactory<>("gridPane14"));
        colDate15.setCellValueFactory(new PropertyValueFactory<>("gridPane15"));
        colDate16.setCellValueFactory(new PropertyValueFactory<>("gridPane16"));
        colDate17.setCellValueFactory(new PropertyValueFactory<>("gridPane17"));
        colDate18.setCellValueFactory(new PropertyValueFactory<>("gridPane18"));
        colDate19.setCellValueFactory(new PropertyValueFactory<>("gridPane19"));
        colDate20.setCellValueFactory(new PropertyValueFactory<>("gridPane20"));
        colDate21.setCellValueFactory(new PropertyValueFactory<>("gridPane21"));
        colDate22.setCellValueFactory(new PropertyValueFactory<>("gridPane22"));
        colDate23.setCellValueFactory(new PropertyValueFactory<>("gridPane23"));
        colDate24.setCellValueFactory(new PropertyValueFactory<>("gridPane24"));
        colDate25.setCellValueFactory(new PropertyValueFactory<>("gridPane25"));
        colDate26.setCellValueFactory(new PropertyValueFactory<>("gridPane26"));
        colDate27.setCellValueFactory(new PropertyValueFactory<>("gridPane27"));
        colDate28.setCellValueFactory(new PropertyValueFactory<>("gridPane28"));
        colDate29.setCellValueFactory(new PropertyValueFactory<>("gridPane29"));
        colDate30.setCellValueFactory(new PropertyValueFactory<>("gridPane30"));
        colDate31.setCellValueFactory(new PropertyValueFactory<>("gridPane31"));
        colsDateList.addAll(
                colDate1, colDate2, colDate3, colDate4, colDate5, colDate6,
                colDate7, colDate8, colDate9, colDate10, colDate11, colDate12,
                colDate13, colDate14, colDate15, colDate16, colDate17, colDate18,
                colDate19, colDate20, colDate21, colDate22, colDate23, colDate24,
                colDate25, colDate26, colDate27, colDate28, colDate29, colDate30, colDate31);
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().addAll(
                colName,
                colDate1, colDate2, colDate3, colDate4, colDate5, colDate6,
                colDate7, colDate8, colDate9, colDate10, colDate11, colDate12,
                colDate13, colDate14, colDate15, colDate16, colDate17, colDate18,
                colDate19, colDate20, colDate21, colDate22, colDate23, colDate24,
                colDate25, colDate26, colDate27, colDate28, colDate29, colDate30, colDate31);
    }

    public void setColsDateProperties(){
        for (CustomTableColumn<T, ?> c : colsDateList) {
            c.setPercentWidth(40.0);
            c.setMinWidth(40.0);
            c.setResizable(false);
            c.setSortable(false);
        }
    }

    public void initCheckBox() {
        IntStream.range(0, employeesFullNameList.size()).forEach(i -> {
            int j = 1;
            for (GridPane gridPane : tableView.getTableView().getItems().get(i).getGridPaneList()) {
                ColumnConstraints columnConstraints = new ColumnConstraints();
                columnConstraints.setMinWidth(38.0);
                gridPane.getColumnConstraints().add(columnConstraints);

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setMinHeight(38.0);
                gridPane.getRowConstraints().add(rowConstraints);

                TextField textField = new TextField();

                CheckBox checkBox = new CheckBox();
                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                checkBox.setCursor(Cursor.HAND);
                if (tableView.getTableView().getColumns().get(j).getStyleClass().indexOf("disable") != -1) {
                    checkBox.setDisable(true);
                }
                gridPane.add(checkBox, 0, 0);

                checkBox.setOnAction((ActionEvent event) -> {

                    if (checkBox.isSelected() == true) {
                        TableCell cell = (TableCell) checkBox.getParent().getParent();
                        T employeesFullName = tableView.getTableView().getItems().get(cell.getTableRow().getIndex());

                        textField.setText(Integer.toString(employeesFullName.getWorkingHours()));
                        textField.setPrefHeight(16.0);
                        textField.setMinHeight(16.0);
                        textField.setAlignment(Pos.CENTER);
                        textField.setPadding(Insets.EMPTY);
                        gridPane.add(textField, 0, 0);
                        gridPane.setHalignment(textField, HPos.CENTER);
                        gridPane.setValignment(textField, VPos.BOTTOM);
                    } else {
                        ObservableList<Node> childrens = gridPane.getChildren();
                        int counter = 0;
                        for (Node node : childrens) {
                            if (textField.equals(node)) {
                                childrens.remove(counter);
                                break;
                            }
                            counter++;
                        }
                    }
                });
                j++;
            }
        });
    }

    public void getTableViewIteams (){
        IntStream.range(0, employeesFullNameList.size()).forEach(i -> {
            int j = 1;
            for (GridPane gridPane : tableView.getTableView().getItems().get(i).getGridPaneList()) {
                if(i == 0 && j == 1) {
                    CheckBox checkBox = (CheckBox) gridPane.getChildren().get(0);
                    checkBox.setSelected(true);
                }
                j++;
            }
        });
    }

//    public void fillItemsObservableMap(String firstDayOfMonth){
//        employeesFullNameList.forEach(item -> {
//            ObservableMap<String, Integer> valuesObservableMap = FXCollections.observableHashMap();
////            valuesObservableMap.putAll(ODBC_PubsBD.selectWorkingHours(firstDayOfMonth, item.getId()));
////            System.out.println(valuesObservableMap.size());
//            itemsObservableMap.put(item.getId(), new ObservableMap<String, Integer>() {
//                @Override
//                public void addListener(MapChangeListener<? super String, ? super Integer> listener) {
//
//                }
//
//                @Override
//                public void removeListener(MapChangeListener<? super String, ? super Integer> listener) {
//
//                }
//
//                @Override
//                public int size() {
//                    return 0;
//                }
//
//                @Override
//                public boolean isEmpty() {
//                    return false;
//                }
//
//                @Override
//                public boolean containsKey(Object key) {
//                    return false;
//                }
//
//                @Override
//                public boolean containsValue(Object value) {
//                    return false;
//                }
//
//                @Override
//                public Integer get(Object key) {
//                    return null;
//                }
//
//                @Override
//                public Integer put(String key, Integer value) {
//                    return null;
//                }
//
//                @Override
//                public Integer remove(Object key) {
//                    return null;
//                }
//
//                @Override
//                public void putAll(Map<? extends String, ? extends Integer> m) {
//
//                }
//
//                @Override
//                public void clear() {
//
//                }
//
//                @Override
//                public Set<String> keySet() {
//                    return null;
//                }
//
//                @Override
//                public Collection<Integer> values() {
//                    return null;
//                }
//
//                @Override
//                public Set<Entry<String, Integer>> entrySet() {
//                    return null;
//                }
//
//                @Override
//                public void addListener(InvalidationListener listener) {
//
//                }
//
//                @Override
//                public void removeListener(InvalidationListener listener) {
//
//                }
//            });
//        });
//    }
}
