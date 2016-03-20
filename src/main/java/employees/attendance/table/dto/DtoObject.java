package employees.attendance.table.dto;

import java.sql.Date;

/**
 * Created by Andriy on 03/19/2016.
 */
public class DtoObject {
    private String address;
    private int id;
    private Date startDate;
    private Date finishDate;

    public DtoObject() {
    }

    public DtoObject(String address, int id, Date startDate, Date finishDate) {
        this.address = address;
        this.id = id;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
