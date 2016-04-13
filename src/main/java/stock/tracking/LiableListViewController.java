package stock.tracking;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import stock.tracking.dto.DtoLiableListView;
import stock.tracking.dto.DtoResult;
import stock.tracking.dto.DtoStock;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LiableListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;
    public StackPane headerStackPane;
    public GridPane headerGridPane;
    public Pane headerPane;
    public Label headerLabel;

    public ChoiceBox liableTypeChoiceBox = new ChoiceBox();
    public ComboBox comboBoxSearch = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    private Integer objectId;
    public Button levelUpButton;
    private String listViewDateParameter;

    public ObservableList<DtoLiableListView> liableListViewDataList = FXCollections.observableArrayList();
    public ObservableList<String> liableNamesList = FXCollections.observableArrayList();
    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    public StockListViewController stockListViewController;

    private List<DtoResult> resultList = new ArrayList<>();

    private GrantedStockListViewController grantedStockListViewController;

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/stock.tracking/ListViewStyle.css").toExternalForm());
        levelUpButton = initLevelUpButton();

        comboBoxSearch = initLiableComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);

        headerGridPane.add(levelUpButton, 0, 0);
        headerGridPane.setMargin(levelUpButton, new Insets(0, 10, 0, 10));

    }

    public void initListView() {
        liableListViewDataList.clear();
        liableNamesList.clear();
        listView.getItems().clear();
        grantedStockListViewController.clearListView();

        if (listViewDateParameter.equals("Об'єкти")) {
            levelUpButton.setDisable(true);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectObjects());
        } else if (listViewDateParameter.equals("Всі працівники")) {
            levelUpButton.setDisable(true);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectAllEmployees());
        } else {
            levelUpButton.setDisable(false);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectEmployeesOnObject(objectId));
        }

        liableListViewDataList.forEach(item -> {
            item.initPaneContainer();
            liableNamesList.add(item.getString());
            setTargetDragAndDrop(item.getPaneContainer());
            listView.getItems().add(item.getPaneContainer());
            setTextNumberOfStock(Integer.parseInt(item.getPaneContainer().getId()));

            if (listViewDateParameter.equals("Об'єкти")) {
                item.getPaneContainer().setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            setListViewDateParameter(item.getString());
                            objectId = item.getId();
                            initListView();
                        } else {
                            initGrantedList(item.getId(), item.getPaneContainer());
                        }
                    }
                });
            } else {
                item.getPaneContainer().setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        initGrantedList(item.getId(), item.getPaneContainer());
                    }
                });
            }
        });
        comboBoxSearch.setItems(liableNamesList);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    public void setTargetDragAndDrop(Pane pane) {
        pane.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragOver");
        /* data is dragged over the target */
        /* accept it only if it is not dragged from the same node
         * and if it has a string data */
                if (event.getGestureSource() != pane &&
                        event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        pane.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragEntered");
    /* the drag-and-drop gesture entered the target */
    /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != pane &&
                        event.getDragboard().hasString()) {
                    pane.setStyle("-fx-background-color: rgba(105, 105, 105, .8)");
                }

                event.consume();
            }
        });

        pane.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragExited");
        /* mouse moved away, remove the graphical cues */
                pane.setStyle("-fx-background-color: rgba(105, 105, 105, .5)");
                event.consume();
            }
        });

        pane.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
//                System.out.println("target.setOnDragDropped");
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Integer intPaneId = Integer.parseInt(pane.getId());
                    if (Integer.parseInt(db.getString().substring(0, 1)) == 0) {
                        int stockId = Integer.parseInt(db.getString().substring(2));
                        if (listViewDateParameter.equals("Об'єкти")) {
                            resultList.add(new DtoResult(stockId, null, intPaneId));
                        } else if (listViewDateParameter.equals("Всі працівники")) {
                            resultList.add(new DtoResult(stockId, intPaneId, null));
                        } else {
                            resultList.add(new DtoResult(stockId, intPaneId, objectId));
                        }
                        stockListViewController.setResultList(resultList);
                        stockListViewController.setDisableDroppedSource(stockId);
                        setTextNumberOfStock(intPaneId);
                    } else {

                        int stockCategoryId = Integer.parseInt(db.getString().substring(2));

                        if (stockListViewController.repositoryChoiceBox.getValue().equals("Всі склади")) {
                            stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryWithId(stockCategoryId));
                        } else {
                            stockDataList.addAll(ODBC_PubsBDForStock.
                                    selectStockOfCategoryWithIdInRepository(stockCategoryId,
                                            stockListViewController.repositoryChoiceBox.getValue().toString()));
                        }

                        int numberOfStockGranted = 0;
                        if (!resultList.isEmpty()) {
                            for (DtoStock dtoStock : stockDataList) {
                                for (DtoResult item : resultList) {
                                    if (item.getStockId() == dtoStock.getId()) {
                                        numberOfStockGranted++;
                                    }
                                }
                            }
                        }
                        int numberOfAvailableStock = stockDataList.size() - numberOfStockGranted;

                        Label liableNameLabel = (Label) pane.getChildren().get(0);
                        String liableName = liableNameLabel.getText();
                        PromptNumberStockToGrant promptNumberStockToGrant;
                        if (listViewDateParameter.equals("Об'єкти")) {
                            promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, null, liableName);
                        } else if (listViewDateParameter.equals("Всі працівники")) {
                            promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, liableName, null);
                        } else {
                            promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, liableName , listViewDateParameter);
                        }
                        int numberOfStockToGrant =
                                promptNumberStockToGrant.showPrompt(pane.getScene().getWindow(), numberOfAvailableStock);
                        if (numberOfStockToGrant != 0) {
                            int i = 0;
                            for (DtoStock item : stockDataList) {
                                if (!resultList.contains(item.getId())) {
                                    if (listViewDateParameter.equals("Об'єкти")) {
                                        resultList.add(new DtoResult(item.getId(), null, intPaneId));
                                    } else if (listViewDateParameter.equals("Всі працівники")) {
                                        resultList.add(new DtoResult(item.getId(), intPaneId, null));
                                    } else {
                                        resultList.add(new DtoResult(item.getId(), intPaneId, objectId));
                                    }
                                    i++;
                                }
                                if (i == numberOfStockToGrant) {
                                    break;
                                }
                            }
                            stockListViewController.setResultList(resultList);
                            stockListViewController.setTextOfAvailableStock(stockCategoryId);
                            setTextNumberOfStock(intPaneId);
                        }
                        stockDataList.clear();
                    }
                    if (grantedStockListViewController.getLiableId() == intPaneId) {
                        initGrantedList(intPaneId, pane);
                    }
                    success = true;
                }
        /* let the source know whether the string was successfully
         * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    public void setTextNumberOfStock(int liableId) {
        int i = 0;
        if (listViewDateParameter.equals("Об'єкти")) {
            for (DtoResult item : resultList) {
                if (item.getObjectId() != null && item.getObjectId() == liableId && item.getEmployeesId() == null) {
                    i++;
                }
            }
        } else if (listViewDateParameter.equals("Всі працівники")){
            for (DtoResult item : resultList) {
                if (item.getEmployeesId() != null && item.getObjectId() == null && item.getEmployeesId() == liableId) {
                    i++;
                }
            }
        } else {
            for (DtoResult item : resultList) {
                if (item.getEmployeesId() != null && item.getObjectId() != null &&
                        item.getEmployeesId() == liableId && item.getObjectId() == objectId) {
                    i++;
                }
            }
        }
        if (i != 0) {
            for (Pane pane : listView.getItems()) {
                if (Integer.parseInt(pane.getId()) == liableId) {
                    Label label = (Label) pane.getChildren().get(1);
                    label.setText("Кількість: " + i);
                }
            }
        }
    }

    public ComboBox initLiableComboBoxSearch() {
        ComboBox comboBox = new ComboBox();

        comboBox.getStylesheets().add(getClass().getResource("/stock.tracking/ComboBoxStyle.css").toExternalForm());
        comboBox.setTooltip(new Tooltip("Пошук"));
        comboBox.setPromptText("Пошук");

        comboBox.setItems(liableNamesList);

        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
//                                mouse pressed
                                comboBoxListener.setValue(comboBox.getValue());
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

        comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                if (newValue != null) {
                    comboBox.getStyleClass().remove("warning");
                    searchInListView();
                    comboBoxListener.setValue(null);
                }
            }
        });
        return comboBox;
    }

    public void searchInListView() {
        Object comboBoxListenerValue = comboBoxListener.getValue();
        Integer liableID = null;
        for (DtoLiableListView item : liableListViewDataList) {
            if (item.getString().equals(comboBoxListenerValue)) {
                liableID = item.getId();
            }
        }
        if (liableID != null) {
            int i = 0;
            for (Pane pane : listView.getItems()) {
                if (Integer.parseInt(pane.getId()) == liableID) {
                    listView.getSelectionModel().select(i);
                    listView.getFocusModel().focus(i);
                    listView.scrollTo(i);
                }
                i++;
            }
        }
    }

    public Button initLevelUpButton() {
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/image/level_up_icon.png"));
        button.getStylesheets().add(getClass().getResource("/stock.tracking/LevelUpButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setPrefWidth(10);
        button.setMaxWidth(10);
        button.setOnAction((ActionEvent event) -> {
            liableTypeChoiceBox.setValue("Об'єкти");
            setListViewDateParameter("Об'єкти");
            initListView();
        });
        Tooltip.install(button, new Tooltip("Повернутись до об'єктів"));
        return button;
    }

    public void setTextToHeaderLabel(String text) {
        headerLabel.setVisible(false);
        headerLabel.setText(text);
        if (headerLabel.prefWidth(-1) <= headerPane.getWidth() - 40 || headerLabel.prefWidth(-1) == 0) {
            headerLabel.setStyle("-fx-translate-x: -21;");
        } else if (headerLabel.prefWidth(-1) > headerPane.getWidth() - 40 && headerLabel.prefWidth(-1) < headerPane.getWidth() - 5) {
            Double translateValue = (headerPane.getWidth() - headerLabel.prefWidth(-1))*0.457142857*-1;
            headerLabel.setStyle("-fx-translate-x: "+ translateValue.toString() +";");
        } else {
            headerLabel.setStyle("-fx-translate-x: 0;");
        }

        headerLabel.setVisible(true);
    }

    public void initGrantedList(Integer liableId, Pane pane){
        LinkedList<Integer> stockIdList = new LinkedList<>();
        if (listViewDateParameter.equals("Об'єкти")){
            resultList.forEach(item -> {
                if (item.getObjectId() == liableId && item.getEmployeesId() == null) {
                    stockIdList.add(item.getStockId());
                }
            });
        } else if (listViewDateParameter.equals("Всі працівники")){
            resultList.forEach(item -> {
                if (item.getEmployeesId() == liableId && item.getObjectId() == null) {
                    stockIdList.add(item.getStockId());
                }
            });
        } else  {
            resultList.forEach(item -> {
                if (item.getEmployeesId() == liableId && item.getObjectId() == objectId) {
                    stockIdList.add(item.getStockId());
                }
            });
        }
        grantedStockListViewController.setLiableId(liableId);
        grantedStockListViewController.setLiablePane(pane);
        grantedStockListViewController.setListViewDateParameter(listViewDateParameter);
        grantedStockListViewController.setStockIdList(stockIdList);
        grantedStockListViewController.initListView();
    }

    public void setResultList(List<DtoResult> resultList) {
        this.resultList = resultList;
        grantedStockListViewController.setResultList(resultList);
        grantedStockListViewController.setStockListViewController(stockListViewController);
    }

    public void setStockListViewController(StockListViewController stockListViewController) {
        this.stockListViewController = stockListViewController;
    }

    public void setLiableTypeChoiceBox(ChoiceBox liableTypeChoiceBox) {
        this.liableTypeChoiceBox = liableTypeChoiceBox;
    }

    public void setListViewDateParameter(String listViewDateParameter) {
        this.listViewDateParameter = listViewDateParameter;
        setTextToHeaderLabel(listViewDateParameter);
    }

    public void setGrantedStockListViewController(GrantedStockListViewController grantedStockListViewController) {
        this.grantedStockListViewController = grantedStockListViewController;
    }

}
