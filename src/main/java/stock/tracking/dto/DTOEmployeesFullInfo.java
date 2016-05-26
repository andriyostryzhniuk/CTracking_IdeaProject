package stock.tracking.dto;

import java.time.LocalDate;

public class DTOEmployeesFullInfo {

    private Integer id;
    private String fullName;
    private LocalDate firstDate;

    public DTOEmployeesFullInfo() {
    }

    public DTOEmployeesFullInfo(Integer id, String fullName, LocalDate firstDate) {
        this.id = id;
        this.fullName = fullName;
        this.firstDate = firstDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(LocalDate firstDate) {
        this.firstDate = firstDate;
    }
}
