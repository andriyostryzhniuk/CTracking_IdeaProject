package stock.tracking;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import overridden.elements.group.box.GroupBox;
import stock.tracking.dto.DtoResult;

public class WindowStockTrackingController {

    public BorderPane rootBorderPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
    public GridPane gridPane;

    public StockListViewController stockListViewController;

    public LiableListViewController liableListViewController;
    public List<DtoResult> resultList = new ArrayList<>();

    @FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/StockListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            stockListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/LiableListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 3, 0);
            liableListViewController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initLeftSideGritPane();
//        rootBorderPane.setTop(topGridPane);
//        rootBorderPane.setAlignment(topGridPane, Pos.TOP_LEFT);
//        rootBorderPane.setMargin(topGridPane, new Insets(0.0, 0.0, 20.0, 0.0));

        liableListViewController.setResultList(resultList);
        liableListViewController.setStockListViewController(stockListViewController);
        liableListViewController.initLiableListView();
    }

    public void initLeftSideGritPane() {
        leftSideGridPane.setPadding(new Insets(0, 10, 0, 0));
        leftSideGridPane.add(initStockGroupBox(), 0, 0);
        leftSideGridPane.add(initLiableGroupBox(), 0, 1);
    }

    public GroupBox initStockGroupBox(){
        ChoiceBox repositoryChoiceBox = initRepositoryChoiceBox();
        stockListViewController.setRepositoryChoiceBox(repositoryChoiceBox);

        ComboBox stockCategoryComboBox = initStockCategoryComboBox();
        stockListViewController.setStockCategoryComboBox(stockCategoryComboBox);

        ChoiceBox stockTypeChoiceBox = initStockTypeChoiceBox();
        stockListViewController.setStockTypeChoiceBox(stockTypeChoiceBox);

        CheckBox showDisableStockCheckBox = initShowDisableStockCheckBox();
        stockListViewController.setShowDisableStockCheckBox(showDisableStockCheckBox);

        GridPane stockControlsGritPane = new GridPane();
        stockControlsGritPane.add(stockCategoryComboBox, 0, 0);
        stockControlsGritPane.setMargin(stockCategoryComboBox, new Insets(0, 0, 8, 0));
        stockControlsGritPane.add(stockTypeChoiceBox, 0, 1);
        stockControlsGritPane.setMargin(stockTypeChoiceBox, new Insets(8, 0, 8, 0));
        stockControlsGritPane.add(repositoryChoiceBox, 0, 2);
        stockControlsGritPane.setMargin(repositoryChoiceBox, new Insets(8, 0, 8, 0));
        stockControlsGritPane.add(showDisableStockCheckBox, 0, 3);
        stockControlsGritPane.setMargin(showDisableStockCheckBox, new Insets(8, 0, 5, 0));

        stockControlsGritPane.setAlignment(Pos.CENTER);

        GroupBox groupBox = new GroupBox(stockControlsGritPane, "Склад", -70);
        groupBox.setMaxWidth(350);
        groupBox.getStylesheets().add(getClass().getResource("/overridden.elements/GroupBoxStyle.css").toExternalForm());

        return groupBox;
    }

    public GroupBox initLiableGroupBox(){
        ChoiceBox liableTypeChoiceBox = initLiableTypeChoiceBox();
        liableListViewController.setLiableTypeChoiceBox(liableTypeChoiceBox);

        GridPane liableControlsGritPane = new GridPane();
        liableControlsGritPane.add(liableTypeChoiceBox, 0, 0);
        liableControlsGritPane.setMargin(liableTypeChoiceBox, new Insets(0, 0, 8, 0));

        liableControlsGritPane.setAlignment(Pos.CENTER);

        GroupBox groupBox = new GroupBox(liableControlsGritPane, "Відповідальні", -50);
        groupBox.setMaxWidth(350);
        groupBox.getStylesheets().add(getClass().getResource("/overridden.elements/GroupBoxStyle.css").toExternalForm());

        return groupBox;
    }

    public ChoiceBox initStockTypeChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        Tooltip.install(choiceBox, new Tooltip("Вибрати тип інвентаря\n(Вартісний/Розхідний)"));
        choiceBox.getItems().addAll("Вартісні", "Розхідні");
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                stockListViewController.stockTypeChoiceBox.setValue(choiceBox.getValue());

                Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

                stockListViewController.updateStockCategoryComboBoxItems();

                stockListViewController.stockCategoryComboBox.setValue(stockListViewController.stockCategoryComboBox.getItems().get(0));
                stockListViewController.comboBoxListener.setValue(stockListViewController.stockCategoryComboBox.getValue());
                new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

                if (oldStockCategoryComboBoxValue != null && oldStockCategoryComboBoxValue.equals(stockListViewController.comboBoxListener.getValue())) {
                    stockListViewController.initStockListView(choiceBox.getValue().toString(), stockListViewController.comboBoxListener.getValue().toString());
                }
            }
        });
        choiceBox.setValue(choiceBox.getItems().get(0));
        return choiceBox;
    }

    public ComboBox initStockCategoryComboBox() {
        ComboBox comboBox = new ComboBox();

        comboBox.getStylesheets().add(getClass().getResource("/stock.tracking/ComboBoxStyle.css").toExternalForm());
        Tooltip.install(comboBox, new Tooltip("Вибрати ктегорію"));

        comboBox.setItems(stockListViewController.stockCategoryNameList);

        new AutoCompleteComboBoxListener<>(comboBox, stockListViewController.comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
//                                mouse pressed
                                comboBox.getStyleClass().remove("warning");
                                stockListViewController.comboBoxListener.setValue(comboBox.getValue());
                            }
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        stockListViewController.comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                if (newValue != null) {
                    comboBox.getStyleClass().remove("warning");
                    stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                            stockListViewController.comboBoxListener.getValue().toString());
                }
            }
        });
        return comboBox;
    }

    public ChoiceBox initRepositoryChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        Tooltip.install(choiceBox, new Tooltip("Вибрати склад"));

        choiceBox.setItems(ODBC_PubsBDForStock.selectRepositoryName());
        choiceBox.setValue(choiceBox.getItems().get(0));

        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

                stockListViewController.updateStockCategoryComboBoxItems();

                if (oldStockCategoryComboBoxValue != null) {
                    stockListViewController.stockCategoryComboBox.setValue(oldStockCategoryComboBoxValue);
                    stockListViewController.comboBoxListener.setValue(oldStockCategoryComboBoxValue);
                }
                new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

                stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                        stockListViewController.comboBoxListener.getValue().toString());
            }
        });

        return choiceBox;
    }

    public CheckBox initShowDisableStockCheckBox(){
        CheckBox checkBox = new CheckBox("Показувати недоступні категорії");
        Tooltip.install(checkBox, new Tooltip("Показувати категорії в яких\nнемає жодного доступного" +
                "\nна даний момент інвентаря"));
        checkBox.setSelected(false);
        checkBox.setOnAction((ActionEvent event) -> {
            Object oldStockCategoryComboBoxValue = stockListViewController.comboBoxListener.getValue();

            stockListViewController.updateStockCategoryComboBoxItems();

            if (oldStockCategoryComboBoxValue != null) {
                stockListViewController.stockCategoryComboBox.setValue(oldStockCategoryComboBoxValue);
                stockListViewController.comboBoxListener.setValue(oldStockCategoryComboBoxValue);
                stockListViewController.stockCategoryComboBox.getStyleClass().remove("warning");
            }
            new AutoCompleteComboBoxListener<>(stockListViewController.stockCategoryComboBox, stockListViewController.comboBoxListener);

            if (stockListViewController.comboBoxListener.getValue().equals("Всі категорії")) {
                stockListViewController.initStockListView(stockListViewController.stockTypeChoiceBox.getValue().toString(),
                        stockListViewController.comboBoxListener.getValue().toString());
            }
        });
        return checkBox;
    }

    public ChoiceBox initLiableTypeChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        Tooltip.install(choiceBox, new Tooltip("Вибрати дані"));

        choiceBox.getItems().addAll("Об'єкти", "Працівники");
        choiceBox.setValue(choiceBox.getItems().get(0));

        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                liableListViewController.liableTypeChoiceBox.setValue(choiceBox.getValue());
                liableListViewController.initLiableListView();
            }
        });

        return choiceBox;
    }

    public void saveToDB() {
        resultList.forEach(item -> {
            ODBC_PubsBDForLiable.insertIntoWorkTracking(item.getStockId(), item.getEmployeesId(), item.getObjectId());
        });

        resultList.clear();
        stockListViewController.setResultList(resultList);
        liableListViewController.setResultList(resultList);
    }
}
