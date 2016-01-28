package sample.dto;

/**
 * Created by Andriy on 01/28/2016.
 */
public class DtoEmployeesFullName {
    private String name;
    private String surname;
    private String middleName;

    public DtoEmployeesFullName(String name, String surname, String middleName) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
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
}
