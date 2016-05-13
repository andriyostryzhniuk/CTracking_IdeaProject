package stock.tracking;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import overridden.elements.group.box.GroupBox;
import stock.tracking.dto.DtoResult;

public class WindowStockTrackingController {

    public BorderPane rootBorderPane;
    public GridPane leftSideGridPane;
    public GridPane rightSideGridPane;
    public TextArea notesTextArea;
    public GridPane gridPane;

    public StockListViewController stockListViewController;
    public LiableListViewController liableListViewController;
    public GrantedStockListViewController grantedStockListViewController;

    public List<DtoResult> resultList = new ArrayList<>();

    @FXML
    public void initialize() {
        notesTextArea.getStylesheets().add(getClass().getResource("/stock.tracking/TextAreaStyle.css").toExternalForm());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/StockListView.fxml"));
        try {
            gridPane.add(fxmlLoader.load(), 1, 0);
            stockListViewController = fxmlLoader.getController();
            stockListViewController.setNotesTextArea(notesTextArea);
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
        initRightSideGridPane();

        fxmlLoader = new FXMLLoader(getClass().getResource("/stock.tracking/GrantedStockListView.fxml"));
        try {
            rightSideGridPane.add(fxmlLoader.load(), 0, 1);
            grantedStockListViewController = fxmlLoader.getController();
            liableListViewController.setGrantedStockListViewController(grantedStockListViewController);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        liableListViewController.setStockListViewController(stockListViewController);
        liableListViewController.setResultList(resultList);
        stockListViewController.initListView(stockListViewController.stockTypeChoiceBox.getValue().toString());
        liableListViewController.initListView();

    }

    public void initLeftSideGritPane() {
        leftSideGridPane.add(initStockGroupBox(), 0, 0);
    }

    public void initRightSideGridPane(){
        ChoiceBox liableTypeChoiceBox = initLiableTypeChoiceBox();
        liableListViewController.setLiableTypeChoiceBox(liableTypeChoiceBox);
        rightSideGridPane.add(liableTypeChoiceBox, 0, 0);
    }

    public GridPane initStockGroupBox(){
        ChoiceBox repositoryChoiceBox = initRepositoryChoiceBox();
        stockListViewController.setRepositoryChoiceBox(repositoryChoiceBox);

        ChoiceBox contentChoiceBox = initContentChoiceBox();
        stockListViewController.setContentChoiceBox(contentChoiceBox);

        ChoiceBox stockTypeChoiceBox = initStockTypeChoiceBox();
        stockListViewController.setStockTypeChoiceBox(stockTypeChoiceBox);

        CheckBox showDisableStockCheckBox = initShowDisableStockCheckBox();
        stockListViewController.setShowDisableStockCheckBox(showDisableStockCheckBox);

        GridPane stockControlsGritPane = new GridPane();
        stockControlsGritPane.add(contentChoiceBox, 0, 0);
        stockControlsGritPane.setMargin(contentChoiceBox, new Insets(0, 0, 8, 0));
        stockControlsGritPane.add(stockTypeChoiceBox, 0, 1);
        stockControlsGritPane.setMargin(stockTypeChoiceBox, new Insets(8, 0, 8, 0));
        stockControlsGritPane.add(repositoryChoiceBox, 0, 2);
        stockControlsGritPane.setMargin(repositoryChoiceBox, new Insets(8, 0, 8, 0));
        stockControlsGritPane.add(showDisableStockCheckBox, 0, 3);
        stockControlsGritPane.setMargin(showDisableStockCheckBox, new Insets(8, 0, 5, 0));

        stockControlsGritPane.setAlignment(Pos.TOP_CENTER);

//        GroupBox groupBox = new GroupBox(stockControlsGritPane, "Склад", -70);
//        groupBox.setMaxWidth(350);
//        groupBox.getStylesheets().add(getClass().getResource("/overridden.elements/GroupBoxStyle.css").toExternalForm());

        return stockControlsGritPane;
    }

    public ChoiceBox initContentChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setTooltip(new Tooltip("Вибрати перегляд"));
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());

        choiceBox.getItems().addAll("Категорії", "Весь інвентар");
        choiceBox.setValue(choiceBox.getItems().get(0));
        stockListViewController.setListViewDateParameter(choiceBox.getValue().toString());

        choiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            stockListViewController.contentChoiceBox.setValue(choiceBox.getValue());
            stockListViewController.setListViewDateParameter(choiceBox.getValue().toString());
            stockListViewController.initListView(stockListViewController.stockTypeChoiceBox.getValue().toString());
        });

        return choiceBox;
    }

    public ChoiceBox initStockTypeChoiceBox() {
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setTooltip(new Tooltip("Вибрати тип інвентаря\n(Вартісний/Розхідний)"));
        choiceBox.getItems().addAll("Вартісні", "Розхідні");
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            stockListViewController.stockTypeChoiceBox.setValue(choiceBox.getValue());
            if (! stockListViewController.getListViewDateParameter().equals("Категорії") &&
                    ! stockListViewController.getListViewDateParameter().equals("Весь інвентар")) {
                stockListViewController.getContentChoiceBox().setValue("Категорії");
                stockListViewController.setListViewDateParameter("Категорії");
            }
            stockListViewController.initListView(choiceBox.getValue().toString());
        });
        choiceBox.setValue(choiceBox.getItems().get(0));
        return choiceBox;
    }

    public ChoiceBox initRepositoryChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.setTooltip(new Tooltip("Вибрати склад"));

        choiceBox.setItems(ODBC_PubsBDForStock.selectRepositoryName());
        choiceBox.setValue(choiceBox.getItems().get(0));

        choiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            stockListViewController.initListView(stockListViewController.stockTypeChoiceBox.getValue().toString());
        });

        return choiceBox;
    }

    public CheckBox initShowDisableStockCheckBox(){
        CheckBox checkBox = new CheckBox("Недоступні категорії");
        checkBox.setTooltip(new Tooltip("Показувати категорії в яких на даний момент" +
                "\nнемає жодного доступного інвентаря"));
        checkBox.setSelected(false);
        checkBox.setOnAction((ActionEvent event) -> {

            if (stockListViewController.getListViewDateParameter().equals("Категорії")) {
                stockListViewController.initListView(stockListViewController.stockTypeChoiceBox.getValue().toString());
            }
        });
        return checkBox;
    }

    public ChoiceBox initLiableTypeChoiceBox(){
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getStylesheets().add(getClass().getResource("/stock.tracking/ChoiceBoxStyle.css").toExternalForm());
        choiceBox.setTooltip(new Tooltip("Вибрати перегляд"));

        choiceBox.getItems().addAll("Об'єкти", "Всі працівники");
        choiceBox.setValue(choiceBox.getItems().get(0));
        liableListViewController.setListViewDateParameter(choiceBox.getValue().toString());

        choiceBox.valueProperty().addListener((ChangeListener<String>) (observableValue, oldValue, newValue) -> {
//                change detected
            liableListViewController.liableTypeChoiceBox.setValue(choiceBox.getValue());
            liableListViewController.setListViewDateParameter(choiceBox.getValue().toString());
            liableListViewController.initListView();
        });
        choiceBox.setMinWidth(237);
        choiceBox.setMaxWidth(237);

        return choiceBox;
    }

    public void saveToDB() {
        resultList.forEach(item -> {
            ODBC_PubsBDForLiable.insertIntoWorkTracking(item.getStockId(), item.getEmployeesId(), item.getObjectId());
        });

        resultList.clear();
        stockListViewController.setResultList(resultList);
        liableListViewController.setResultList(resultList);
        liableListViewController.initListView();
        stockListViewController.initListView(stockListViewController.stockTypeChoiceBox.getValue().toString());
    }

}
