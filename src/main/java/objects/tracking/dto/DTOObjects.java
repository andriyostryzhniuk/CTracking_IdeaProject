package objects.tracking.dto;

import stock.tracking.dto.DtoObject;

import java.util.List;

public class DTOObjects extends DtoObject {

    private List<DTOObjectEmployees> objectEmployeesList;

    public DTOObjects() {
    }

    public DTOObjects(int id, String address) {
        super(id, address);
    }

    public List<DTOObjectEmployees> getObjectEmployeesList() {
        return objectEmployeesList;
    }

    public void setObjectEmployeesList(List<DTOObjectEmployees> objectEmployeesList) {
        this.objectEmployeesList = objectEmployeesList;
    }
}
