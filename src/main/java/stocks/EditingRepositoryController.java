package stocks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import stocks.dto.DTORepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static stocks.ODBC_PubsBD.*;

public class EditingRepositoryController {

    public TextField textField;
    public Label exceptionLabel;
    public Label titleLabel;

    private SettingsRepositoryController SettingsRepositoryController;
    private WindowStocksController windowStocksController;

    private DTORepository dtoRepository;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
    }

    public void saveButtonAction(ActionEvent actionEvent) {
        if (textFieldIsEmpty() || !textFieldMatcherFind()) {
            return;
        }

        if (dtoRepository != null) {
            dtoRepository.setName(textField.getText());
            updateRepository(dtoRepository);
        } else {
            dtoRepository = new DTORepository(null, textField.getText());
            insertIntoRepository(dtoRepository);
        }

        SettingsRepositoryController.initTableView();
        windowStocksController.refreshRepository();
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
            exceptionLabel.setVisible(false);
        });

        textField.setOnMouseClicked((MouseEvent event) -> {
            if (textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().remove("warning");
                exceptionLabel.setVisible(true);
            }
        });

        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setVisible(true);
            } else {
                exceptionLabel.setVisible(false);
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
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
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

    public void setSettingsRepositoryController(SettingsRepositoryController settingsRepositoryController) {
        this.SettingsRepositoryController = settingsRepositoryController;
    }

    public void setDtoRepository(DTORepository dtoRepository) {
        this.dtoRepository = dtoRepository;
        initTitleLabelText();
    }

    private void initTitleLabelText(){
        if (dtoRepository == null) {
            titleLabel.setText("Створити категорію");
        } else {
            titleLabel.setText("Редагувати категорію");
            setTextToTextField(dtoRepository.getName());
        }
    }

    private void setTextToTextField(String text){
        textField.setText(text);
    }

    public void setWindowStocksController(WindowStocksController windowStocksController) {
        this.windowStocksController = windowStocksController;
    }
}
