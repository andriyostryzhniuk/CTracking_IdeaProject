package objects.tracking.dto;

import stock.tracking.dto.DtoObject;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class DTOObjects extends DtoObject {

    private List<DTOObjectEmployees> objectEmployeesList;
    private LocalDate startDate;
    private LocalDate finishDate;

    public DTOObjects() {
    }

    public DTOObjects(int id, String address) {
        super(id, address);
    }

    public DTOObjects(int id, String address, LocalDate startDate, LocalDate finishDate) {
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
}
