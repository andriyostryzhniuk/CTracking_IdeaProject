package employees.attendance.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.stream.IntStream;

public class TableViewController<T extends DtoEmployeesFullName> {

    @FXML
    public StackPane rootBorderPane;
    @FXML
    private CustomTableView<T> tableView = new CustomTableView<>();

    @FXML
    public CustomTableColumn<T, String> colName = new CustomTableColumn<>("Працівник");

    public TableView<T> getTableView() {
        return tableView.getTableView();
    }

    private final ObservableList<T> employeesFullNameList = FXCollections.observableArrayList();

    public ObservableList<T> getEmployeesFullNameList() {
        return employeesFullNameList;
    }

    private final ObservableList<CustomTableColumn<T, CheckBox>> colsDateList = FXCollections.observableArrayList();

    private void fillColsDateList(){
        IntStream.range(0, 31).forEach(i -> colsDateList.add(new CustomTableColumn<>(null)));
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

        tableView.getTableView().setItems(employeesFullNameList);
        rootBorderPane.getChildren().add(tableView);

        initCheckBox();
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().add(colName);
        tableView.getTableView().getColumns().addAll(colsDateList);
    }

    public void setColsDateProperties(){
        for (CustomTableColumn<T, CheckBox> C : colsDateList) {
            C.setPercentWidth(40.0);
            C.setMinWidth(40.0);
            C.setResizable(false);
            C.setSortable(false);
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
                                TextField textField = new TextField();

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

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    checkBox.setOnAction((ActionEvent event) -> {
                                        System.out.println(param.getId());

                                        if (checkBox.isSelected() == true) {
                                            TableCell cell = (TableCell)checkBox.getParent().getParent();
                                            T CellRow = tableView.getTableView().getItems().get(cell.getTableRow().getIndex());
                                            textField.setText(Integer.toString(CellRow.getWorkingHours()));

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
                                                if (textField.equals(N)) {
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

}
