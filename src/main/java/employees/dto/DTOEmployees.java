package employees.dto;

import java.time.LocalDate;
import java.util.List;

public class DTOEmployees {

    private Integer id;
    private String name;
    private String surname;
    private String middleName;
    private LocalDate birthDate;
    private LocalDate firstDate;
    private LocalDate lastDate;
    private String notes;
    private Integer workingHours;
    private String imagesURL;
    private List<String> skillsList;
    private List<String> telephonesList;
    private String fullName;

    public DTOEmployees() {
    }

    public DTOEmployees(Integer id, String name, String surname, String middleName, LocalDate birthDate,
                        LocalDate firstDate, String notes, Integer workingHours) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.firstDate = firstDate;
        this.notes = notes;
        this.workingHours = workingHours;
    }

    public DTOEmployees(Integer id, String name, String surname, String middleName, LocalDate birthDate,
                        LocalDate firstDate, LocalDate lastDate, String notes, Integer workingHours, String imagesURL) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.notes = notes;
        this.workingHours = workingHours;
        this.imagesURL = imagesURL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(LocalDate firstDate) {
        this.firstDate = firstDate;
    }

    public LocalDate getLastDate() {
        return lastDate;
    }

    public void setLastDate(LocalDate lastDate) {
        this.lastDate = lastDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Integer workingHours) {
        this.workingHours = workingHours;
    }

    public String getImagesURL() {
        return imagesURL;
    }

    public void setImagesURL(String imagesURL) {
        this.imagesURL = imagesURL;
    }

    public List<String> getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(List<String> skillsList) {
        this.skillsList = skillsList;
    }

    public List<String> getTelephonesList() {
        return telephonesList;
    }

    public void setTelephonesList(List<String> telephonesList) {
        this.telephonesList = telephonesList;
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

    private String initFullName(){
        return surname + " " + name + " " + middleName;
    }
}
