package objects.tracking.dto;

import stock.tracking.dto.DtoObject;

import java.util.Date;
import java.util.List;

public class DTOObjects extends DtoObject {

    private List<DTOObjectEmployees> objectEmployeesList;
    private Date startDate;
    private Date finishDate;

    public DTOObjects() {
    }

    public DTOObjects(int id, String address) {
        super(id, address);
    }

    public DTOObjects(int id, String address, Date startDate, Date finishDate) {
        super(id, address);
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public List<DTOObjectEmployees> getObjectEmployeesList() {
        return objectEmployeesList;
    }

    public void setObjectEmployeesList(List<DTOObjectEmployees> objectEmployeesList) {
        this.objectEmployeesList = objectEmployeesList;
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
