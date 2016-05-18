package objects.tracking.dto;

import java.util.Date;

public class DTOResult {

    private Integer objectId;
    private Integer employeeId;
    private Date startDate;
    private Date finishDate;

    public DTOResult() {
    }

    public DTOResult(Integer objectId, Integer employeeId, Date startDate, Date finishDate) {
        this.objectId = objectId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.finishDate = finishDate;
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
}
