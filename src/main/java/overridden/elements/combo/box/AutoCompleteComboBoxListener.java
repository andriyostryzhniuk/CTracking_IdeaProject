package overridden.elements.combo.box;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox comboBox;
    private ComboBox comboBoxListener;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox comboBox, ComboBox comboBoxListener) {
        this.comboBox = comboBox;
        this.comboBoxListener = comboBoxListener;
        data = comboBox.getItems();
        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(event -> comboBox.hide());
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    @Override
    public void handle(KeyEvent event) {
        System.out.println(event.getCode());
        if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.ENTER) {
//            enter pressed
            setValueToComboBoxListener();
            return;
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        ObservableList list = FXCollections.observableArrayList();
        for (int i=0; i<data.size(); i++) {
            if(data.get(i).toString().toLowerCase().
                            indexOf(AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase()) != -1) {
                list.add(data.get(i));
            }
        }

        String text = comboBox.getEditor().getText();

        comboBox.setItems(list);
        comboBox.getEditor().setText(text);
        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(text.length());

        if (!list.isEmpty()) {
            comboBox.show();
            comboBox.getStyleClass().remove("warning");
        } else {
            setWarningStyle();
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

    private void setValueToComboBoxListener(){
        if (comboBox.getItems().contains(comboBox.getValue())) {
            comboBoxListener.setValue(comboBox.getValue());
        } else {
            setWarningStyle();
        }
    }

    private void setWarningStyle(){
        if (!comboBox.getStyleClass().contains("warning")) {
            comboBox.getStyleClass().add("warning");
        }
    }

}
