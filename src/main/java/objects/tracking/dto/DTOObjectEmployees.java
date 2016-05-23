package objects.tracking.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static objects.tracking.ODBC_PubsBD.selectEmployeesFullName;

public class DTOObjectEmployees {

    private Integer id;
    private Integer objectId;
    private Integer employeeId;
    private LocalDate startDate;
    private LocalDate finishDate;
    private String fullName;
    private String pattern = "dd.MM.yyyy";
    private String formatStartDate;
    private String formatFinishDate;

    public DTOObjectEmployees() {
    }

    public DTOObjectEmployees(Integer id, Integer objectId, Integer employeeId,
                              LocalDate startDate, LocalDate finishDate) {
        this.id = id;
        this.objectId = objectId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public DTOObjectEmployees(Integer id, Integer objectId, Integer employeeId,
                              LocalDate startDate, LocalDate finishDate, String fullName) {
        this.id = id;
        this.objectId = objectId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.fullName = fullName;
    }

    public DTOObjectEmployees(Integer id, LocalDate startDate, LocalDate finishDate) {
        this.id = id;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public String getFullName() {
        if (fullName == null) {
            fullName = selectEmployeesFullName(employeeId);
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFormatStartDate() {
        if (formatStartDate == null) {
            initFormatStartDate();
        }
        return formatStartDate;
    }

    public void setFormatStartDate(String formatStartDate) {
        this.formatStartDate = formatStartDate;
    }

    public void initFormatStartDate(){
        formatStartDate = startDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String getFormatFinishDate() {
        if (formatFinishDate == null) {
            initFormatFinishDate();
        }
        return formatFinishDate;
    }

    public void setFormatFinishDate(String formatFinishDate) {
        this.formatFinishDate = formatFinishDate;
    }

    public void initFormatFinishDate(){
        if (finishDate != null) {
            formatFinishDate = finishDate.format(DateTimeFormatter.ofPattern(pattern));
        } else {
            formatFinishDate = "-";
        }
    }
}
