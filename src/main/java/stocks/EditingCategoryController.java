package stocks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import stocks.dto.DTOStockCategory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static stocks.ODBC_PubsBD.insertIntoStockCategory;
import static stocks.ODBC_PubsBD.updateStockCategory;

public class EditingCategoryController {

    public TextField textField;
    public Label exceptionLabel;
    public Label titleLabel;
    public ComboBox typeComboBox;

    private SettingsStockCategoryController settingsStockCategoryController;
    private WindowStocksController windowStocksController;

    private DTOStockCategory dtoStockCategory;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
        initTypeComboBox();
    }

    public void saveButtonAction(ActionEvent actionEvent) {
        if (textFieldIsEmpty() || !textFieldMatcherFind()) {
            return;
        }

        if (typeComboBox.getValue() == null) {
            exceptionLabel.setText("Виберіть тип");
            return;
        }

        if (dtoStockCategory != null) {
            dtoStockCategory.setName(textField.getText());
            dtoStockCategory.setType(typeComboBox.getValue().toString());
            updateStockCategory(dtoStockCategory);
        } else {
            dtoStockCategory = new DTOStockCategory(null, textField.getText(), typeComboBox.getValue().toString());
            insertIntoStockCategory(dtoStockCategory);
        }

        settingsStockCategoryController.initTableView();
        windowStocksController.refreshCategory();
        windowStocksController.initTableView();
        closeStage();
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) textField.getScene().getWindow();
        stage.close();
    }

    private void setListenerToNameTextField() {
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind ();
            exceptionLabel.setText("");
        });

        textField.setOnMouseClicked((MouseEvent event) -> {
            if (textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().remove("warning");
                exceptionLabel.setText("Назва містить неприпустимі символи");
            }
        });

        Pattern pattern = Pattern.compile("[^а-яА-ЯіІїЇєЄ'\\s-]");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Назва містить неприпустимі символи");
            } else {
                exceptionLabel.setText("");
            }
            if (textField.getText().length() > 45) {
                String text = textField.getText().substring(0, 45);
                textField.setText(text);
            }
        });
    }

    private boolean textFieldMatcherFind(){
        if (textField.getText() == null || textField.getText().isEmpty()) {
            return false;
        }
        textField.setText(textField.getText().trim());
        textField.setText(textField.getText().substring(0, 1).toUpperCase() + textField.getText().substring(1));
        boolean right = true;
        Pattern pattern = Pattern.compile("[^а-яА-ЯіІїЇєЄ'\\s-]");
        textField.setText(textField.getText().trim());
        Matcher matcher = pattern.matcher(textField.getText());
        if (matcher.find()) {
            right = false;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return right;
    }

    private boolean textFieldIsEmpty(){
        boolean isEmpty = false;
        if (textField.getText() == null || textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    private void initTypeComboBox(){
        typeComboBox.getItems().addAll("Вартісні", "Розхідні");
        typeComboBox.setTooltip(new Tooltip("Виберіть тип"));

        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (exceptionLabel.getText().equals("Виберіть тип")) {
                exceptionLabel.setText("");
            }
        });
    }

    public void setSettingsStockCategoryController(SettingsStockCategoryController settingsStockCategoryController) {
        this.settingsStockCategoryController = settingsStockCategoryController;
    }

    public void setDtoStockCategory(DTOStockCategory dtoStockCategory) {
        this.dtoStockCategory = dtoStockCategory;
        initTitleLabelText();
    }

    private void initTitleLabelText(){
        if (dtoStockCategory == null) {
            titleLabel.setText("Створити категорію");
        } else {
            titleLabel.setText("Редагувати категорію");
            setTextToTextField(dtoStockCategory.getName());
            typeComboBox.setValue(dtoStockCategory.getType());
        }
    }

    private void setTextToTextField(String text){
        textField.setText(text);
    }

    public void setWindowStocksController(WindowStocksController windowStocksController) {
        this.windowStocksController = windowStocksController;
    }
}
