package employees.attendance.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import dto.DtoEmployeesFullName;
import javafx.util.Callback;
import sample.ODBC_PubsBD;

public class TableViewController<T extends DtoEmployeesFullName> {

    @FXML
    public TableColumn<T, String> colName;

    @FXML
    private TableView<T> tableView;

    public TableView<T> getTableView() {
        return tableView;
    }

    private final ObservableList<T> employeesFullNameList = FXCollections.observableArrayList();

    public ObservableList<T> getEmployeesFullNameList() {
        return employeesFullNameList;
    }

    @FXML
    public void initialize() {

        tableView.setStyle("-fx-accent: derive(-fx-control-inner-background, -30%); -fx-control-inner-background, -80%;");

        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        tableView.setItems(employeesFullNameList);

        initCheckBox ();
    }

    public void initCheckBox (){
        ObservableList<TableColumn<T, ?>> tableColumns = tableView.getColumns();

        int i = 0;
        for (TableColumn C : tableColumns) {
            if (i > 0) {
                C.setCellFactory(checkBoxCellFactory2());
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
                                CheckBox checkBox = new CheckBox();
                                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                                checkBox.setCursor(Cursor.HAND);

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    checkBox.setOnAction((ActionEvent event) -> {
                                        System.out.println(param.getId());
                                        Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory1 = param.getCellFactory();
                                    });
                                    setGraphic(checkBox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    public Callback<TableColumn<T, CheckBox>, TableCell<T, CheckBox>> checkBoxCellFactory2() {

        Callback<TableColumn<T, CheckBox>, TableCell<T, CheckBox>> cellFactory =
                new Callback<TableColumn<T, CheckBox>, TableCell<T, CheckBox>>() {
                    @Override
                    public TableCell call(final TableColumn<T, CheckBox> param) {
                        final TableCell<T, String> cell = new TableCell<T, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                CheckBox checkBox = new CheckBox();
                                checkBox.getStylesheets().add(getClass().getResource("/CheckBoxStyle.css").toExternalForm());
                                checkBox.setCursor(Cursor.HAND);

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    checkBox.setOnAction((ActionEvent event) -> {
                                        System.out.println(param.getId());
                                        Callback<TableColumn<T, CheckBox>, TableCell<T, CheckBox>> cellFactory1 = param.getCellFactory();
                                    });
                                    setGraphic(checkBox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    public Callback<TableColumn<T, String>, TableCell<T, String>> textFieldCellFactory() {
        Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory =
                new Callback<TableColumn<T, String>, TableCell<T, String>>() {
                    @Override
                    public TableCell call(final TableColumn<T, String> param) {

                        final TableCell<T, String> cell = new TableCell<T, String>() {

                            @Override
                            public void updateItem(String item, boolean empty) {

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    TextField textField = new TextField();
                                    try {
                                        textField.setText(Integer.toString(ODBC_PubsBD.selectDefaultEmployeesWorkingHours(
                                                1)));
                                        //calculateEmployeesId(checkBoxId))));
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                    textField.setPrefHeight(16.0);
                                    textField.setMinHeight(16.0);
                                    textField.setAlignment(Pos.CENTER);
                                    textField.setPadding(Insets.EMPTY);
                                    setGraphic(textField);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }
}
