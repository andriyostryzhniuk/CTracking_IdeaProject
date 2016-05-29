package employees;

import employees.dto.DTOSkills;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static employees.ODBC_PubsBD.insertIntoSkills;
import static employees.ODBC_PubsBD.updateSkills;

public class EditingSkillsController {

    public TextField textField;
    public Label exceptionLabel;
    public Label titleLabel;

    private AddingEmployeesSkillController addingEmployeesSkillController;

    private DTOSkills dtoSkills;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
    }

    public void save(ActionEvent actionEvent) {
        if (!textFieldIsEmpty() && textFieldMatcherFind()){

            if (dtoSkills != null) {
                dtoSkills.setSkill(textField.getText());
                updateSkills(dtoSkills);
            } else {
                dtoSkills = new DTOSkills(null, textField.getText());
                insertIntoSkills(dtoSkills);
            }

            addingEmployeesSkillController.initListView();
            closeStage();
        } else {
            return;
        }
    }

    public void close(ActionEvent actionEvent) {
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

        Pattern pattern = Pattern.compile("[^а-яА-ЯіІїЇєЄ'\\s-]");
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
        if (textField.getText() == null) {
            return false;
        }
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

    public void setAddingEmployeesSkillController(AddingEmployeesSkillController addingEmployeesSkillController) {
        this.addingEmployeesSkillController = addingEmployeesSkillController;
    }

    public void setDtoSkills(DTOSkills dtoSkills) {
        this.dtoSkills = dtoSkills;
        initTitleLabelText();
    }

    private void initTitleLabelText(){
        if (dtoSkills == null) {
            titleLabel.setText("Створити спеціальність");
        } else {
            titleLabel.setText("Редагувати спеціальність");
            setTextToTextField(dtoSkills.getSkill());
        }
    }

    private void setTextToTextField(String text){
        textField.setText(text);
    }

}
