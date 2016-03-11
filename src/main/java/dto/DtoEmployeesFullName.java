package dto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

/**
 * Created by Andriy on 01/28/2016.
 */
public class DtoEmployeesFullName {
    private int id;
    private String fullName;
    private int workingHours;
    private ObservableList<TextField> textFieldList = initTextFieldList();
//    private List<Integer> monthHours;

    public DtoEmployeesFullName() {

    }

    public DtoEmployeesFullName(int id, String fullName, int workingHours /* + one param */) {
        this.id = id;
        this.fullName = fullName;
        this.workingHours = workingHours;
//        this.monthHours = ...
    }

//    public List<Integer> getMonthHours() {
//        return monthHours;
//    }
//
//    public void setMonthHours(List<Integer> monthHours) {
//        this.monthHours = monthHours;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public ObservableList<TextField> getTextFieldList() {
        return textFieldList;
    }

    public void setTextFieldList(ObservableList<TextField> textFieldList) {
        this.textFieldList = textFieldList;
    }

    private ObservableList<TextField> initTextFieldList (){
        ObservableList<TextField> textFieldList = FXCollections.observableArrayList();
        IntStream.range(0, 31).forEach(i -> textFieldList.add(new TextField()));
        return textFieldList;
    }
}
