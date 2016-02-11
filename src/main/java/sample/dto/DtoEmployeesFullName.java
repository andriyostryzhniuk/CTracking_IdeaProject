package sample.dto;

/**
 * Created by Andriy on 01/28/2016.
 */
public class DtoEmployeesFullName {
    private int id;
    private String name;
    private String surname;
    private String middleName;

    public DtoEmployeesFullName() {

    }

    public DtoEmployeesFullName(int id, String name, String surname, String middleName) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurnameAndInitials () {
        return surname + " " + name.substring(0, 1) + "." + middleName.substring(0, 1) + ".";
    }
}
