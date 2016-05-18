package objects.tracking.dto;

import stock.tracking.dto.DtoEmployees;

import java.util.List;

public class DTOEmployees extends DtoEmployees {

    private List<String> skills;

    public DTOEmployees() {
    }

    public DTOEmployees(int id, String fullName) {
        super(id, fullName);
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
