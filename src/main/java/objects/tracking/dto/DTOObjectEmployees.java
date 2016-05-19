package objects.tracking.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static objects.tracking.ODBC_PubsBD.selectEmployeesFullName;

public class DTOObjectEmployees {

    private Integer id;
    private Integer objectId;
    private Integer employeeId;
    private Date startDate;
    private Date finishDate;
    private String fullName;
    private DateFormat dateFormat;
    private String formatStartDate;
    private String formatFinishDate;

    public DTOObjectEmployees() {
    }

    public DTOObjectEmployees(Integer id, Integer objectId, Integer employeeId, Date startDate, Date finishDate) {
        this.id = id;
        this.objectId = objectId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public DTOObjectEmployees(Integer id, Integer objectId, Integer employeeId, Date startDate, Date finishDate,
                              String fullName) {
        this.id = id;
        this.objectId = objectId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.fullName = fullName;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
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
            formatStartDate = getDateFormat().format(startDate);
        }
        return formatStartDate;
    }

    public void setFormatStartDate(String formatStartDate) {
        this.formatStartDate = formatStartDate;
    }

    public String getFormatFinishDate() {
        if (formatFinishDate == null && finishDate != null) {
            formatFinishDate = getDateFormat().format(finishDate);
        }
        return formatFinishDate;
    }

    public void setFormatFinishDate(String formatFinishDate) {
        this.formatFinishDate = formatFinishDate;
    }

    public DateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        }
        return dateFormat;
    }
}
