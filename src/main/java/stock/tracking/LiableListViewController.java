package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import overridden.elements.combo.box.AutoCompleteComboBoxListener;
import stock.tracking.dto.DTOStockTracking;
import stock.tracking.dto.DtoLiableListView;
import stock.tracking.dto.DtoStock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static stock.tracking.ODBC_PubsBDForLiable.*;

public class LiableListViewController {

    public GridPane rootGridPane;
    public ListView<Pane> listView;
    public StackPane headerStackPane;
    public GridPane headerGridPane;
    public Pane headerPane;
    public Label headerLabel;

    public ComboBox comboBoxSearch = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    private Integer objectId;
    public Button levelUpButton;
    private String liableType;
    private LocalDate dateView = LocalDate.now();

    public ObservableList<DtoLiableListView> liableListViewDataList = FXCollections.observableArrayList();
    public ObservableList<String> liableNamesList = FXCollections.observableArrayList();
    public ObservableList<DtoStock> stockDataList = FXCollections.observableArrayList();
    public StockListViewController stockListViewController;

    private WindowStockTrackingController windowStockTrackingController;

    @FXML
    public void initialize() {
        listView.getStylesheets().add(getClass().getResource("/styles/ListViewStyle.css").toExternalForm());
        levelUpButton = initLevelUpButton();

        initLiableComboBoxSearch();
        rootGridPane.add(comboBoxSearch, 0, 0);
        comboBoxSearch.setMaxWidth(Double.MAX_VALUE);

        headerGridPane.add(levelUpButton, 0, 0);
        headerGridPane.setMargin(levelUpButton, new Insets(0, 10, 0, 10));

        initOnSelectionAction();

    }

    public void initListView(boolean isNeedSelectItems) {
        Integer selectedRowIndex = listView.getSelectionModel().getSelectedIndex();
        liableListViewDataList.clear();
        liableNamesList.clear();
        listView.getItems().clear();

        if (liableType.equals("Об'єкти")) {
            levelUpButton.setDisable(true);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectObjects(dateView));
        } else if (liableType.equals("Працівники")) {
            levelUpButton.setDisable(true);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectAllEmployees(dateView));
        } else {
            levelUpButton.setDisable(false);
            liableListViewDataList.addAll(ODBC_PubsBDForLiable.selectEmployeesOnObject(objectId, dateView));
        }

        liableListViewDataList.forEach(item -> {
            item.initPaneContainer();
            liableNamesList.add(item.getString());
            setTargetDragAndDrop(item.getPaneContainer());
            listView.getItems().add(item.getPaneContainer());

            if (liableType.equals("Об'єкти")) {
                item.getPaneContainer().setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            setLiableType(item.getString());
                            objectId = item.getId();
                            initListView(false);
                        }
                    }
                });
            }
        });

        if (isNeedSelectItems) {
            listView.getSelectionModel().select(selectedRowIndex);
            listView.getFocusModel().focus(selectedRowIndex);
            listView.scrollTo(selectedRowIndex);
        }

        comboBoxSearch.setItems(liableNamesList);
        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);
    }

    private void initOnSelectionAction(){
        listView.getSelectionModel().selectedItemProperty().addListener(event -> {
            Pane selectedItem;
            if ((selectedItem = listView.getSelectionModel().getSelectedItem()) != null) {
                if (liableType.equals("Об'єкти")) {
                    Integer objectId = Integer.parseInt(selectedItem.getId());
                    windowStockTrackingController.initTableView(objectId, null);
                } else if (liableType.equals("Працівники")) {
                    Integer employeeId = Integer.parseInt(selectedItem.getId());
                    windowStockTrackingController.initTableView(null, employeeId);
                } else {
                    Integer employeeId = Integer.parseInt(selectedItem.getId());
                    windowStockTrackingController.initTableView(objectId, employeeId);
                }
            } else {
                windowStockTrackingController.clearTableView();
            }
        });
    }

    public void setTargetDragAndDrop(Pane pane) {
        pane.setOnDragOver(event -> {
            if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        pane.setOnDragEntered(event -> {
            if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                pane.setStyle("-fx-background-color: rgba(105, 105, 105, .8)");
            }

            event.consume();
        });

        pane.setOnDragExited(event -> {
            pane.setStyle("-fx-background-color: rgba(105, 105, 105, .5)");
            event.consume();
        });

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Integer intPaneId = Integer.parseInt(pane.getId());
                if (!stockListViewController.getContentType().equals("Категорії")) {

                    DTOStockTracking dtoStockTracking;
                    int stockId = Integer.parseInt(db.getString());
                    if (liableType.equals("Об'єкти")) {
                        LocalDate returningDate = selectObjectFinishDate(intPaneId);
                        dtoStockTracking = new DTOStockTracking(null, stockId, null, intPaneId, dateView, returningDate);
                    } else if (liableType.equals("Працівники")) {
                        dtoStockTracking = new DTOStockTracking(null, stockId, intPaneId, null, dateView, null);
                    } else {
                        LocalDate returningDate = selectObjectFinishDate(objectId);
                        dtoStockTracking = new DTOStockTracking(null, stockId, intPaneId, objectId, dateView, returningDate);
                    }

                    LocalDate nextStockUsingDate;
                    DTOStockTracking simultaneousStockTracking;
                    if ((simultaneousStockTracking = selectIfStockIsDisable(stockId, dateView)) != null) {
                        if (showDeletingConfirmation(simultaneousStockTracking)) {
                            if (! closeStockTracking(simultaneousStockTracking)) {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if ((nextStockUsingDate = selectNextStockUsingDate(stockId, dateView)) != null) {
                        dtoStockTracking.setReturnDate(nextStockUsingDate.minusDays(1));
                    }

                    if (! windowStockTrackingController.editRecord(dtoStockTracking, false)) {
                        return;
                    }

                } else {

                    int stockCategoryId = Integer.parseInt(db.getString());

                    if (stockListViewController.getRepository().equals("Всі склади")) {
                        stockDataList.addAll(ODBC_PubsBDForStock.selectStockOfCategoryWithId(stockCategoryId, dateView));
                    } else {
                        stockDataList.addAll(ODBC_PubsBDForStock.
                                selectStockOfCategoryWithIdInRepository(stockCategoryId,
                                        stockListViewController.getRepository(), dateView));
                    }

                    Label liableNameLabel = (Label) pane.getChildren().get(0);
                    String liableName = liableNameLabel.getText();
                    PromptNumberStockToGrant promptNumberStockToGrant;
                    if (liableType.equals("Об'єкти")) {
                        promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, null, liableName);
                    } else if (liableType.equals("Працівники")) {
                        promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, liableName, null);
                    } else {
                        promptNumberStockToGrant = new PromptNumberStockToGrant(stockCategoryId, liableName, liableType);
                    }

                    int numberOfStockToGrant =
                            promptNumberStockToGrant.showPrompt(pane.getScene().getWindow(), stockDataList.size());

                    if (numberOfStockToGrant != 0) {
                        DTOStockTracking dtoStockTracking;
                        int i = 0;
                        for (DtoStock item : stockDataList) {
                            if (liableType.equals("Об'єкти")) {
                                LocalDate returningDate = selectObjectFinishDate(intPaneId);
                                dtoStockTracking =
                                        new DTOStockTracking(null, item.getId(), null, intPaneId, dateView, returningDate);
                            } else if (liableType.equals("Працівники")) {
                                dtoStockTracking =
                                        new DTOStockTracking(null, item.getId(), intPaneId, null, dateView, null);
                            } else {
                                LocalDate returningDate = selectObjectFinishDate(objectId);
                                dtoStockTracking =
                                        new DTOStockTracking(null, item.getId(), intPaneId, objectId, dateView, returningDate);
                            }
                            ODBC_PubsBDForLiable.insertIntoStockTracking(dtoStockTracking);
                            i++;
                            if (i == numberOfStockToGrant) {
                                break;
                            }
                        }
                    }
                    stockDataList.clear();
                }

                stockListViewController.initListView(true);
                initListView(true);
                success = true;
            }

            event.setDropCompleted(success);

            event.consume();
        });
    }

    private boolean showDeletingConfirmation(DTOStockTracking dtoStockTracking){
        String objectsName = "";
        if (dtoStockTracking.getObjectId() != null) {
            objectsName = "на об'єкті " + selectObjectAddress(dtoStockTracking.getObjectId()) + " ";
        }
        String employeesName = "";
        if (dtoStockTracking.getEmployeesId() != null) {
            employeesName = "за працівником " + selectEmployeesShortName(dtoStockTracking.getEmployeesId());
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Попередження");
        alert.setHeaderText(null);
        alert.setContentText("Даний інвентар уже закріплений\n" + objectsName + employeesName + "\nПередати його?");

        ButtonType okButton = new ButtonType("ОК");
        ButtonType cancelButton = new ButtonType("Скасувати");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == okButton) {
            return true;
        } else {
            return false;
        }
    }

    private boolean closeStockTracking (DTOStockTracking dtoStockTracking) {
        LocalDate yesterdaysDate = dateView.minusDays(1);

        if (dtoStockTracking.getGivingDate().isAfter(yesterdaysDate)) {
            showStartDateError(dtoStockTracking.getGivingDate(), yesterdaysDate);
            return false;
        }

        dtoStockTracking.setReturnDate(yesterdaysDate);
        updateStockTracking(dtoStockTracking);
        return true;
    }

    private void showStartDateError(LocalDate givingDate, LocalDate returningDate) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText("Неможливо вилучити інвентар за датою "
                + returningDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                ",\nменшою ніж дата отримання "
                + givingDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");
        alert.showAndWait();
    }

    public void initLiableComboBoxSearch() {
        comboBoxSearch.getStylesheets().add(getClass().getResource("/styles/ComboBoxSearchStyle.css").toExternalForm());
        comboBoxSearch.setTooltip(new Tooltip("Пошук"));
        comboBoxSearch.setPromptText("Пошук");

        comboBoxSearch.setItems(liableNamesList);

        new AutoCompleteComboBoxListener<>(comboBoxSearch, comboBoxListener);

        comboBoxSearch.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    super.setOnMousePressed((MouseEvent event) -> {
//                            mouse pressed
                        comboBoxListener.setValue(comboBoxSearch.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        comboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//                change detected
                if (newValue != null) {
                    comboBoxSearch.getStyleClass().remove("warning");
                    searchInListView();
                    comboBoxListener.setValue(null);
                }
        });
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
        Image image = new Image(getClass().getResourceAsStream("/icons/level_up_icon.png"));
        button.getStylesheets().add(getClass().getResource("/styles/ListsButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setPrefWidth(10);
        button.setMaxWidth(10);
        button.setOnAction((ActionEvent event) -> {
            windowStockTrackingController.getLiableTypeChoiceBox().setValue("Об'єкти");
            setLiableType("Об'єкти");
            initListView(false);
        });
        Tooltip.install(button, new Tooltip("Повернутись до об'єктів"));
        return button;
    }

    public void setTextToHeaderLabel(String text) {
        headerLabel.setVisible(false);
        headerLabel.setText(text);
        if (headerLabel.prefWidth(-1) <= headerPane.getWidth() - 40 || headerLabel.prefWidth(-1) == 0) {
            headerLabel.setStyle("-fx-translate-x: -21; -fx-text-fill: rgb(200,200,200); -fx-font-weight: bold;");
        } else if (headerLabel.prefWidth(-1) > headerPane.getWidth() - 40 && headerLabel.prefWidth(-1) < headerPane.getWidth() - 5) {
            Double translateValue = (headerPane.getWidth() - headerLabel.prefWidth(-1))*0.457142857*-1;
            headerLabel.setStyle("-fx-translate-x: "+ translateValue.toString() +"; " +
                    "-fx-text-fill: rgb(200,200,200); -fx-font-weight: bold;");
        } else {
            headerLabel.setStyle("-fx-translate-x: 0; -fx-text-fill: rgb(200,200,200); -fx-font-weight: bold;");
        }

        headerLabel.setVisible(true);
    }

    public void setStockListViewController(StockListViewController stockListViewController) {
        this.stockListViewController = stockListViewController;
    }

    public void setLiableType(String liableType) {
        this.liableType = liableType;
        setTextToHeaderLabel(liableType);
    }

    public void setWindowStockTrackingController(WindowStockTrackingController windowStockTrackingController) {
        this.windowStockTrackingController = windowStockTrackingController;
    }

    public void setDateView(LocalDate dateView) {
        this.dateView = dateView;
    }
}
