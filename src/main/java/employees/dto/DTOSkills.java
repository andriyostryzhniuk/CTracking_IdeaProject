package employees.dto;

public class DTOSkills {

    private Integer skillsEmployeesId;
    private Integer skillsId;
    private Integer employeesId;
    private String skill;

    public DTOSkills() {
    }

    public DTOSkills(Integer skillsId, String skill) {
        this.skillsId = skillsId;
        this.skill = skill;
    }

    public DTOSkills(Integer skillsEmployeesId, Integer skillsId, Integer employeesId, String skill) {
        this.skillsEmployeesId = skillsEmployeesId;
        this.skillsId = skillsId;
        this.employeesId = employeesId;
        this.skill = skill;
    }

    public Integer getSkillsEmployeesId() {
        return skillsEmployeesId;
    }

    public void setSkillsEmployeesId(Integer skillsEmployeesId) {
        this.skillsEmployeesId = skillsEmployeesId;
    }

    public Integer getSkillsId() {
        return skillsId;
    }

    public void setSkillsId(Integer skillsId) {
        this.skillsId = skillsId;
    }

    public Integer getEmployeesId() {
        return employeesId;
    }

    public void setEmployeesId(Integer employeesId) {
        this.employeesId = employeesId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return skill;
    }
}
