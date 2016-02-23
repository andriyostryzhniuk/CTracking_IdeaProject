package dto;

/**
 * Created by Andriy on 01/28/2016.
 */
public class DtoEmployeesFullName {
    private int id;
    private String fullName;

    public DtoEmployeesFullName() {

    }

    public DtoEmployeesFullName(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    //    public String getSurnameAndInitials () {
//        return surname + " " + fullName.substring(0, 1) + "." + middleName.substring(0, 1) + ".";
//    }
}
