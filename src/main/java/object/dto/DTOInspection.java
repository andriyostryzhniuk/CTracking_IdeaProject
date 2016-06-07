package object.dto;

import employees.dto.DTOTelephones;

import java.util.List;

public class DTOInspection {

    private Integer id;
    private Integer customersId;
    private String name;
    private String surname;
    private String middleName;
    private String fullName;
    private List<DTOTelephones> dtoTelephonesList;

    public DTOInspection() {
    }

    public DTOInspection(Integer id, Integer customersId, String name, String surname, String middleName) {
        this.id = id;
        this.customersId = customersId;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomersId() {
        return customersId;
    }

    public void setCustomersId(Integer customersId) {
        this.customersId = customersId;
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

    public String getFullName() {
        if (fullName == null) {
            fullName = initFullName();
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String initFullName(){
        return surname + " " + name + " " + middleName;
    }

    public List<DTOTelephones> getDtoTelephonesList() {
        return dtoTelephonesList;
    }

    public void setDtoTelephonesList(List<DTOTelephones> dtoTelephonesList) {
        this.dtoTelephonesList = dtoTelephonesList;
    }

    @Override
    public String toString() {
        return getFullName();
    }

}
