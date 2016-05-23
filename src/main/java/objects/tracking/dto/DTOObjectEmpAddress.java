package objects.tracking.dto;

import java.time.LocalDate;

public class DTOObjectEmpAddress extends DTOObjectEmployees {

    private String address;

    public DTOObjectEmpAddress() {
    }

    public DTOObjectEmpAddress(Integer id, LocalDate startDate, LocalDate finishDate, String address) {
        super(id, startDate, finishDate);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
