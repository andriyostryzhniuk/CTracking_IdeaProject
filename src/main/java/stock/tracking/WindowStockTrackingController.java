package stock.tracking;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import combo.box.AutoCompleteComboBoxListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.io.IOException;

public class WindowStockTrackingController {

    public BorderPane rootBorderPane;
    private GridPane topGridPane = new GridPane();
    public GridPane gridPane;

    public StockListViewController stockListViewController;

    public LiableListViewController liableListViewController;
    public ObservableMap<Integer, Integer> resultMap = FXCollections.observableHashMap();

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

        initTopGridPane();
        rootBorderPane.setTop(topGridPane);
        rootBorderPane.setAlignment(topGridPane, Pos.TOP_LEFT);
        rootBorderPane.setMargin(topGridPane, new Insets(0.0, 0.0, 20.0, 0.0));

        liableListViewController.setResultMap(resultMap);
        liableListViewController.setStockListViewController(stockListViewController);
        liableListViewController.initLiableListView();
    }

    public void initTopGridPane() {
        ChoiceBox repositoryChoiceBox = initRepositoryChoiceBox();
        stockListViewController.setRepositoryChoiceBox(repositoryChoiceBox);

        ComboBox stockCategoryComboBox = initStockCategoryComboBox();
        stockListViewController.setStockCategoryComboBox(stockCategoryComboBox);

        Button goBackStockButton = initGoBackStockButton();
        stockListViewController.setGoBackButton(goBackStockButton);

        ChoiceBox stockTypeChoiceBox = initStockTypeChoiceBox();
        stockListViewController.setStockTypeChoiceBox(stockTypeChoiceBox);

        CheckBox showDisableStockCheckBox = initShowDisableStockCheckBox();
        stockListViewController.setShowDisableStockCheckBox(showDisableStockCheckBox);

        topGridPane.add(repositoryChoiceBox, 0, 0);
        topGridPane.add(stockTypeChoiceBox, 1, 0);
        topGridPane.add(goBackStockButton, 2, 0);
        topGridPane.add(stockCategoryComboBox, 3, 0);
        topGridPane.add(showDisableStockCheckBox, 4, 0);
    }

    public ChoiceBox initStockTypeChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("вартісні", "розхідні");
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

    public Button initGoBackStockButton() {
        Button button = new Button("Back");
        button.setOnAction((ActionEvent event) -> {
            if (!stockListViewController.comboBoxListener.getValue().equals("Всі категорії")) {
                stockListViewController.stockCategoryComboBox.setValue("Всі категорії");
                stockListViewController.comboBoxListener.setValue(stockListViewController.stockCategoryComboBox.getValue());
            }
        });
        return button;
    }

    public CheckBox initShowDisableStockCheckBox(){
        CheckBox checkBox = new CheckBox("Показувати недоступні категорії");
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

    public void saveToDB() {
        resultMap.entrySet().stream().forEach((entry) -> {
            ODBC_PubsBDForLiable.insertIntoWorkTracking(entry.getKey(), entry.getValue());
        });
        resultMap.clear();
        stockListViewController.setResultMap(resultMap);
        liableListViewController.setResultMap(resultMap);
    }
}
