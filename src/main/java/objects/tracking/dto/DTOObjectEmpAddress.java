package objects.tracking.dto;

public class DTOObjectEmpAddress {

    private String address;
    private Integer objectEmployeesId;

    public DTOObjectEmpAddress() {
    }

    public DTOObjectEmpAddress(String address, Integer objectEmployeesId) {
        this.address = address;
        this.objectEmployeesId = objectEmployeesId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getObjectEmployeesId() {
        return objectEmployeesId;
    }

    public void setObjectEmployeesId(Integer objectEmployeesId) {
        this.objectEmployeesId = objectEmployeesId;
    }
}
