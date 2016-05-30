package employees;

import employees.dto.DTOEmployees;
import employees.dto.DTOSkills;
import employees.dto.DTOTelephones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static main.DB_Connector.creteSimpleJdbcInsert;
import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    private static SimpleJdbcInsert simpleJdbcInsertForEmployees;

    private static SimpleJdbcInsert getSimpleJdbcInsertForEmployees() {
        if (simpleJdbcInsertForEmployees == null) {
            simpleJdbcInsertForEmployees = creteSimpleJdbcInsert();
            simpleJdbcInsertForEmployees
                    .withTableName("employees")
                    .usingGeneratedKeyColumns("id");
        }
        return simpleJdbcInsertForEmployees;
    }

    public static List<DTOEmployees> selectEmployeesList() {
        return getJdbcTemplate().query("SELECT id, name, surname, middleName, birthDate, firstDay AS firstDate, " +
                "notes, workingHours, imagesURL " +
                        "FROM employees " +
                        "WHERE lastDay IS NULL " +
                        "order by surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class));
    }

    public static List<DTOSkills> selectEmployeesSkills(Integer employeesId){
        return getJdbcTemplate().query("select skills_employees.id AS skillsEmployeesId, skills.id AS skillsId, " +
                "skills.skill " +
                        "from skills_employees, skills " +
                        "where skills_employees.employees_id = ? and " +
                        "skills_employees.skills_id = skills.id",
                BeanPropertyRowMapper.newInstance(DTOSkills.class), employeesId);
    }

    public static List<DTOSkills> selectSkillsList() {
        return getJdbcTemplate().query("SELECT id AS skillsId, skill FROM skills",
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

    public static String selectCountNotClosedWork(Integer employeesId) {
        return getJdbcTemplate().queryForObject("SELECT object.address " +
                        "FROM object_employees, object " +
                        "WHERE employees_id = ? AND ( " +
                        "object_employees.finishDate > curdate() OR " +
                        "object_employees.finishDate IS NULL ) AND " +
                        "object_employees.object_id = object.id",
                new Object[]{employeesId}, String.class);
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

    public static Integer insertIntoEmployees(DTOEmployees dtoEmployees){
        Map<String, Object> parameters = new HashMap<>(9);
        parameters.put("name", dtoEmployees.getName());
        parameters.put("surname", dtoEmployees.getSurname());
        parameters.put("middleName", dtoEmployees.getMiddleName());
        parameters.put("birthDate", dtoEmployees.getBirthDate());
        parameters.put("firstDay", dtoEmployees.getFirstDate());
        parameters.put("lastDay", dtoEmployees.getLastDate());
        parameters.put("notes", dtoEmployees.getNotes());
        parameters.put("workingHours", dtoEmployees.getWorkingHours());
        parameters.put("imagesURL", dtoEmployees.getImagesURL());
        return getSimpleJdbcInsertForEmployees().executeAndReturnKey(parameters).intValue();
    }

    public static void insertIntoSkillsEmployees(List<DTOSkills> dtoSkillsList){
        getNamedParameterJdbcTemplate().batchUpdate("INSERT INTO skills_employees (id, employees_id, skills_id) " +
                        "VALUES (null, :employeesId, :skillsId)",
                SqlParameterSourceUtils.createBatch(dtoSkillsList.toArray()));
    }

    public static void deleteFromSkillsEmployees(List<DTOSkills> dtoSkillsList){
        getNamedParameterJdbcTemplate().batchUpdate("DELETE FROM skills_employees " +
                        "WHERE id = :skillsEmployeesId",
                SqlParameterSourceUtils.createBatch(dtoSkillsList.toArray()));
    }

    public static void insertIntoSkills(DTOSkills dtoSkills){
        getNamedParameterJdbcTemplate().update("INSERT INTO skills " +
                        "(id, skill) " +
                        "VALUES (:skillsId, :skill)",
                new BeanPropertySqlParameterSource(dtoSkills));
    }

    public static void updateSkills(DTOSkills dtoSkills) {
        getNamedParameterJdbcTemplate().update("UPDATE skills " +
                        "SET skill = :skill " +
                        "WHERE id = :skillsId",
                new BeanPropertySqlParameterSource(dtoSkills));
    }

    public static void deleteFromSkills(DTOSkills dtoSkills){
        getNamedParameterJdbcTemplate().update("DELETE FROM skills " +
                        "WHERE id = :skillsId",
                new BeanPropertySqlParameterSource(dtoSkills));
    }

    public static List<DTOTelephones> selectEmployeesTelephones(Integer employeesId){
        return getJdbcTemplate().query("SELECT id AS recordId, employees_id AS subscriberId, telephoneNumber AS number " +
                        "FROM empTelephone " +
                        "WHERE employees_id = ?",
                BeanPropertyRowMapper.newInstance(DTOTelephones.class), employeesId);
    }

    public static void insertTelephones(DTOTelephones dtoTelephones){
        getNamedParameterJdbcTemplate().update("INSERT INTO empTelephone (id, employees_id, telephoneNumber) " +
                        "VALUES (:recordId, :subscriberId, :number)",
                new BeanPropertySqlParameterSource(dtoTelephones));
    }

    public static void updateTelephones(DTOTelephones dtoTelephones){
        getNamedParameterJdbcTemplate().update("UPDATE empTelephone " +
                        "SET telephoneNumber = :number " +
                        "WHERE id = :recordId",
                new BeanPropertySqlParameterSource(dtoTelephones));
    }

    public static void deleteTelephones(List<DTOTelephones> dtoTelephonesList){
        getNamedParameterJdbcTemplate().batchUpdate("DELETE FROM empTelephone " +
                        "WHERE id = :recordId",
                SqlParameterSourceUtils.createBatch(dtoTelephonesList.toArray()));
    }
}