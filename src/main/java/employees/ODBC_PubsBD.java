package employees;

import employees.dto.DTOEmployees;
import employees.dto.DTOSkills;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.time.LocalDate;
import java.util.List;
import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DTOEmployees> selectEmployeesList() {
        return getJdbcTemplate().query("SELECT id, name, surname, middleName, birthDate, firstDay AS firstDate, " +
                "notes, workingHours, imagesURL " +
                        "FROM employees " +
                        "WHERE lastDay IS NULL " +
                        "order by surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class));
    }

    public static List<DTOSkills> selectEmployeesSkills(Integer employeesId){
        return getJdbcTemplate().query("select skills.id, skills.skill " +
                        "from skills_employees, skills " +
                        "where skills_employees.employees_id = ? and " +
                        "skills_employees.skills_id = skills.id",
                BeanPropertyRowMapper.newInstance(DTOSkills.class), employeesId);
    }

    public static void updateImagesURL (Integer employeesId, String imagesURL) {
        getJdbcTemplate().update("UPDATE employees " +
                "SET imagesURL = ? " +
                "WHERE id = ?", imagesURL, employeesId);
    }

    public static List<DTOSkills> selectSkillsList() {
        return getJdbcTemplate().query("SELECT * FROM skills",
                BeanPropertyRowMapper.newInstance(DTOSkills.class));
    }

    public static LocalDate selectMinObjEmpDate(Integer employeesId) {
        return getJdbcTemplate().queryForObject("select min(startDate) " +
                        "from object_employees " +
                        "where employees_id = ?",
                new Object []{employeesId}, LocalDate.class);
    }

    public static LocalDate selectMinEmpStockDate(Integer employeesId) {
        return getJdbcTemplate().queryForObject("select min(givingDate) " +
                        "from stockTracking " +
                        "where employees_id = ?",
                new Object []{employeesId}, LocalDate.class);
    }

    public static LocalDate selectMaxObjEmpDate(Integer employeesId) {
        return getJdbcTemplate().queryForObject("select max(finishDate) " +
                        "from object_employees " +
                        "where employees_id = ?",
                new Object []{employeesId}, LocalDate.class);
    }

    public static LocalDate selectMaxEmpStockDate(Integer employeesId) {
        return getJdbcTemplate().queryForObject("select max(returnDate) " +
                        "from stockTracking " +
                        "where employees_id = ?",
                new Object []{employeesId}, LocalDate.class);
    }

    public static void updateEmployees (DTOEmployees dtoEmployees) {
        getNamedParameterJdbcTemplate().update("UPDATE employees " +
                "SET name = :name, " +
                "surname = :surname, " +
                "middleName = :middleName, " +
                "birthDate = :birthDate, " +
                "firstDay = :firstDate, " +
                "lastDay = :lastDate, " +
                "notes = :notes, " +
                "workingHours = :workingHours, " +
                "imagesURL = :imagesURL " +
                "WHERE id = :id",
                new BeanPropertySqlParameterSource(dtoEmployees));
    }
}