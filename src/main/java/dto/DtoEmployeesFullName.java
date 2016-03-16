package dto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by Andriy on 01/28/2016.
 */
public class DtoEmployeesFullName {
    private int id;
    private String fullName;
    private int workingHours;
    private GridPane gridPaneFullName = new GridPane();
    private Label sumOfWorkingHoursLabel = new Label();
    private GridPane gridPane1 = new GridPane();
    private GridPane gridPane2 = new GridPane();
    private GridPane gridPane3 = new GridPane();
    private GridPane gridPane4 = new GridPane();
    private GridPane gridPane5 = new GridPane();
    private GridPane gridPane6 = new GridPane();
    private GridPane gridPane7 = new GridPane();
    private GridPane gridPane8 = new GridPane();
    private GridPane gridPane9 = new GridPane();
    private GridPane gridPane10 = new GridPane();
    private GridPane gridPane11 = new GridPane();
    private GridPane gridPane12 = new GridPane();
    private GridPane gridPane13 = new GridPane();
    private GridPane gridPane14 = new GridPane();
    private GridPane gridPane15 = new GridPane();
    private GridPane gridPane16 = new GridPane();
    private GridPane gridPane17 = new GridPane();
    private GridPane gridPane18 = new GridPane();
    private GridPane gridPane19 = new GridPane();
    private GridPane gridPane20 = new GridPane();
    private GridPane gridPane21 = new GridPane();
    private GridPane gridPane22 = new GridPane();
    private GridPane gridPane23 = new GridPane();
    private GridPane gridPane24 = new GridPane();
    private GridPane gridPane25 = new GridPane();
    private GridPane gridPane26 = new GridPane();
    private GridPane gridPane27 = new GridPane();
    private GridPane gridPane28 = new GridPane();
    private GridPane gridPane29 = new GridPane();
    private GridPane gridPane30 = new GridPane();
    private GridPane gridPane31 = new GridPane();
    private ObservableList<GridPane> gridPaneList = initGridPaneList();

    public DtoEmployeesFullName() {

    }

    public DtoEmployeesFullName(int id, String fullName, int workingHours) {
        this.id = id;
        this.fullName = fullName;
        this.workingHours = workingHours;
    }

    private ObservableList<GridPane> initGridPaneList (){
        ObservableList<GridPane> gridPaneList = FXCollections.observableArrayList();
        gridPaneList.addAll(
                gridPane1, gridPane2, gridPane3, gridPane4, gridPane5,
                gridPane6, gridPane7, gridPane8, gridPane9, gridPane10,
                gridPane11, gridPane12, gridPane13, gridPane14, gridPane15,
                gridPane16, gridPane17, gridPane18, gridPane19, gridPane20,
                gridPane21, gridPane22, gridPane23, gridPane24, gridPane25,
                gridPane26, gridPane27, gridPane28, gridPane29, gridPane30, gridPane31);
        return gridPaneList;
    }

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

    public GridPane getGridPaneFullName() {
        return gridPaneFullName;
    }

    public void setGridPaneFullName(GridPane gridPaneFullName) {
        this.gridPaneFullName = gridPaneFullName;
    }

    public GridPane initGridPaneFullName() {
        GridPane gridPane = new GridPane();

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().add(columnConstraints);

        Label fullNameLabel = new Label(fullName);

        gridPane.add(fullNameLabel, 0, 0);
        gridPane.add(sumOfWorkingHoursLabel, 0, 1);

        gridPane.setHalignment(sumOfWorkingHoursLabel, HPos.RIGHT);
        gridPane.setValignment(sumOfWorkingHoursLabel, VPos.BOTTOM);
        gridPane.setMargin(sumOfWorkingHoursLabel, new Insets(0, 5, 0, 0));
        return gridPane;
    }

    public Label getSumOfWorkingHoursLabel() {
        return sumOfWorkingHoursLabel;
    }

    public void setSumOfWorkingHoursLabel(Label sumOfWorkingHoursLabel) {
        this.sumOfWorkingHoursLabel = sumOfWorkingHoursLabel;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public ObservableList<GridPane> getGridPaneList() {
        return gridPaneList;
    }

    public void setGridPaneList(ObservableList<GridPane> gridPaneList) {
        this.gridPaneList = gridPaneList;
    }

    public GridPane getGridPane1() {
        return gridPane1;
    }

    public void setGridPane1(GridPane gridPane1) {
        this.gridPane1 = gridPane1;
    }

    public GridPane getGridPane2() {
        return gridPane2;
    }

    public void setGridPane2(GridPane gridPane2) {
        this.gridPane2 = gridPane2;
    }

    public GridPane getGridPane3() {
        return gridPane3;
    }

    public void setGridPane3(GridPane gridPane3) {
        this.gridPane3 = gridPane3;
    }

    public GridPane getGridPane4() {
        return gridPane4;
    }

    public void setGridPane4(GridPane gridPane4) {
        this.gridPane4 = gridPane4;
    }

    public GridPane getGridPane5() {
        return gridPane5;
    }

    public void setGridPane5(GridPane gridPane5) {
        this.gridPane5 = gridPane5;
    }

    public GridPane getGridPane6() {
        return gridPane6;
    }

    public void setGridPane6(GridPane gridPane6) {
        this.gridPane6 = gridPane6;
    }

    public GridPane getGridPane7() {
        return gridPane7;
    }

    public void setGridPane7(GridPane gridPane7) {
        this.gridPane7 = gridPane7;
    }

    public GridPane getGridPane8() {
        return gridPane8;
    }

    public void setGridPane8(GridPane gridPane8) {
        this.gridPane8 = gridPane8;
    }

    public GridPane getGridPane9() {
        return gridPane9;
    }

    public void setGridPane9(GridPane gridPane9) {
        this.gridPane9 = gridPane9;
    }

    public GridPane getGridPane10() {
        return gridPane10;
    }

    public void setGridPane10(GridPane gridPane10) {
        this.gridPane10 = gridPane10;
    }

    public GridPane getGridPane11() {
        return gridPane11;
    }

    public void setGridPane11(GridPane gridPane11) {
        this.gridPane11 = gridPane11;
    }

    public GridPane getGridPane12() {
        return gridPane12;
    }

    public void setGridPane12(GridPane gridPane12) {
        this.gridPane12 = gridPane12;
    }

    public GridPane getGridPane13() {
        return gridPane13;
    }

    public void setGridPane13(GridPane gridPane13) {
        this.gridPane13 = gridPane13;
    }

    public GridPane getGridPane14() {
        return gridPane14;
    }

    public void setGridPane14(GridPane gridPane14) {
        this.gridPane14 = gridPane14;
    }

    public GridPane getGridPane15() {
        return gridPane15;
    }

    public void setGridPane15(GridPane gridPane15) {
        this.gridPane15 = gridPane15;
    }

    public GridPane getGridPane16() {
        return gridPane16;
    }

    public void setGridPane16(GridPane gridPane16) {
        this.gridPane16 = gridPane16;
    }

    public GridPane getGridPane17() {
        return gridPane17;
    }

    public void setGridPane17(GridPane gridPane17) {
        this.gridPane17 = gridPane17;
    }

    public GridPane getGridPane18() {
        return gridPane18;
    }

    public void setGridPane18(GridPane gridPane18) {
        this.gridPane18 = gridPane18;
    }

    public GridPane getGridPane19() {
        return gridPane19;
    }

    public void setGridPane19(GridPane gridPane19) {
        this.gridPane19 = gridPane19;
    }

    public GridPane getGridPane20() {
        return gridPane20;
    }

    public void setGridPane20(GridPane gridPane20) {
        this.gridPane20 = gridPane20;
    }

    public GridPane getGridPane21() {
        return gridPane21;
    }

    public void setGridPane21(GridPane gridPane21) {
        this.gridPane21 = gridPane21;
    }

    public GridPane getGridPane22() {
        return gridPane22;
    }

    public void setGridPane22(GridPane gridPane22) {
        this.gridPane22 = gridPane22;
    }

    public GridPane getGridPane23() {
        return gridPane23;
    }

    public void setGridPane23(GridPane gridPane23) {
        this.gridPane23 = gridPane23;
    }

    public GridPane getGridPane24() {
        return gridPane24;
    }

    public void setGridPane24(GridPane gridPane24) {
        this.gridPane24 = gridPane24;
    }

    public GridPane getGridPane25() {
        return gridPane25;
    }

    public void setGridPane25(GridPane gridPane25) {
        this.gridPane25 = gridPane25;
    }

    public GridPane getGridPane26() {
        return gridPane26;
    }

    public void setGridPane26(GridPane gridPane26) {
        this.gridPane26 = gridPane26;
    }

    public GridPane getGridPane27() {
        return gridPane27;
    }

    public void setGridPane27(GridPane gridPane27) {
        this.gridPane27 = gridPane27;
    }

    public GridPane getGridPane28() {
        return gridPane28;
    }

    public void setGridPane28(GridPane gridPane28) {
        this.gridPane28 = gridPane28;
    }

    public GridPane getGridPane29() {
        return gridPane29;
    }

    public void setGridPane29(GridPane gridPane29) {
        this.gridPane29 = gridPane29;
    }

    public GridPane getGridPane30() {
        return gridPane30;
    }

    public void setGridPane30(GridPane gridPane30) {
        this.gridPane30 = gridPane30;
    }

    public GridPane getGridPane31() {
        return gridPane31;
    }

    public void setGridPane31(GridPane gridPane31) {
        this.gridPane31 = gridPane31;
    }
}
