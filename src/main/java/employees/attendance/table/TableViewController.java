package employees.attendance.table;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
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
import javafx.util.Callback;

import java.util.*;
import java.util.stream.IntStream;

public class TableViewController<T extends DtoEmployeesFullName> {


    @FXML
    public StackPane rootBorderPane;
    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();

    @FXML
    public CustomTableColumn<T, String> colName = new CustomTableColumn<>("Працівник");

    public TableView<T> getTableView() {
        return tableView.getTableView();
    }

    private final ObservableList<T> employeesFullNameList = FXCollections.observableArrayList();

    public ObservableList<T> getEmployeesFullNameList() {
        return employeesFullNameList;
    }

    private final ObservableList<CustomTableColumn<T, Integer>> colsDateList = FXCollections.observableArrayList();

    ObservableMap<Integer, ObservableMap<String, Integer>> iteamsObservableMap = FXCollections.observableHashMap();

    private void fillColsDateList(){
        IntStream.range(0, 31).forEach(i -> colsDateList.add(createCustomTabeleColumn()));
    }

    private CustomTableColumn<T, Integer> createCustomTabeleColumn() {
        CustomTableColumn column =  new CustomTableColumn<>(null);
//        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
//            @Override
//            public ObservableValue call(TableColumn.CellDataFeatures param) {
//                return null;
//            }
//        });
        return column;
    }

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

//        fillIteamsObservableMap();

        tableView.getTableView().setItems(employeesFullNameList);
        rootBorderPane.getChildren().add(tableView);

        initCheckBox();

//        getTableViewIteams ();
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().add(colName);
        tableView.getTableView().getColumns().addAll(colsDateList);
    }

    public void setColsDateProperties(){
        for (CustomTableColumn<T, ?> c : colsDateList) {
            c.setPercentWidth(40.0);
            c.setMinWidth(40.0);
            c.setResizable(false);
            c.setSortable(false);
        }
    }

    public void initCheckBox (){
        ObservableList<TableColumn<T, ?>> tableColumns = tableView.getTableView().getColumns();

        int i = 0;
        for (TableColumn C : tableColumns) {
            if (i > 0) {
                C.setCellFactory(checkBoxCellFactory());
            }
            i++;
        }
    }

    public Callback<TableColumn<T, String>, TableCell<T, String>> checkBoxCellFactory() {

        Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory =
                new Callback<TableColumn<T, String>, TableCell<T, String>>() {
                    @Override
                    public TableCell call(final TableColumn<T, String> param) {
                        final TableCell<T, String> cell = new TableCell<T, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                GridPane gridPane = new GridPane();
//                                TextField textField = new TextField();

                                ColumnConstraints columnConstraints = new ColumnConstraints();
                                columnConstraints.setMinWidth(38.0);
                                gridPane.getColumnConstraints().add(columnConstraints);

                                RowConstraints rowConstraints = new RowConstraints();
                                rowConstraints.setMinHeight(38.0);
                                gridPane.getRowConstraints().add(rowConstraints);

                                CheckBox checkBox = new CheckBox();
                                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                                checkBox.setCursor(Cursor.HAND);
                                if (param.getStyleClass().indexOf("disable") != -1) {
                                    checkBox.setDisable(true);
                                }
                                gridPane.add(checkBox, 0, 0);

//                                TableCell cell1 = (TableCell)checkBox.getParent().getParent();
//                                cell1.getTableRow();

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    checkBox.setOnAction((ActionEvent event) -> {
//                                        System.out.println(param.getId());
                                        TableCell cell = (TableCell)checkBox.getParent().getParent();
                                        T employeesFullName = tableView.getTableView().getItems().get(cell.getTableRow().getIndex());

                                        Integer tableColumnIndex = 0;
                                        for (TableColumn t : tableView.getTableView().getColumns()) {
                                            if (t.equals(param)) {
                                                break;
                                            }
                                            tableColumnIndex++;
                                        }

                                        if (checkBox.isSelected() == true) {
//                                            employeesFullName.getMonthHours().get()

                                            TextField textField = employeesFullName.getTextFieldList().get(tableColumnIndex - 1);

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
                                            int i = 0;
                                            for (Node N : childrens) {
                                                if (employeesFullName.getTextFieldList().get(tableColumnIndex - 1).equals(N)) {
                                                    childrens.remove(i);
                                                    break;
                                                }
                                                i++;
                                            }
                                        }
                                    });
                                    setGraphic(gridPane);
                                    setText(null);


                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    public void getTableViewIteams (){
        tableView.getChildren().forEach(child -> System.out.println("child " + child.getClass()));
        tableView.getTableView();
    }

    public void fillIteamsObservableMap (){
        employeesFullNameList.forEach(iteam -> {
            iteamsObservableMap.put(iteam.getId(), new ObservableMap<String, Integer>() {
                @Override
                public void addListener(MapChangeListener<? super String, ? super Integer> listener) {

                }

                @Override
                public void removeListener(MapChangeListener<? super String, ? super Integer> listener) {

                }

                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean containsKey(Object key) {
                    return false;
                }

                @Override
                public boolean containsValue(Object value) {
                    return false;
                }

                @Override
                public Integer get(Object key) {
                    return null;
                }

                @Override
                public Integer put(String key, Integer value) {
                    return null;
                }

                @Override
                public Integer remove(Object key) {
                    return null;
                }

                @Override
                public void putAll(Map<? extends String, ? extends Integer> m) {

                }

                @Override
                public void clear() {

                }

                @Override
                public Set<String> keySet() {
                    return null;
                }

                @Override
                public Collection<Integer> values() {
                    return null;
                }

                @Override
                public Set<Entry<String, Integer>> entrySet() {
                    return null;
                }

                @Override
                public void addListener(InvalidationListener listener) {

                }

                @Override
                public void removeListener(InvalidationListener listener) {

                }
            });
        });
    }
}
