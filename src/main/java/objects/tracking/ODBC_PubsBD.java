package objects.tracking;

import main.BeanPropertyRowMapperWithNullCheck;
import objects.tracking.dto.DTOEmployees;
import objects.tracking.dto.DTOObjectEmpAddress;
import objects.tracking.dto.DTOObjects;
import objects.tracking.dto.DTOObjectEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import java.time.LocalDate;
import java.util.List;
import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DTOEmployees> selectFreeEmployees(LocalDate dateView) {
        return getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName, notes " +
                "from employees left join ( " +
                "   select employees_id " +
                "   from object_employees " +
                "   where (startDate <= ? and finishDate is null) or " +
                "   ? between startDate and finishDate) who_is_on_object " +
                "   on employees.id = who_is_on_object.employees_id " +
                "where who_is_on_object.employees_id is null and " +
                "employees.lastDay is null " +
                "order by employees.surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class), dateView, dateView);
    }

    public static List<DTOEmployees> selectAllEmployees() {
        return getJdbcTemplate().query("select id, " +
                "concat(surname, ' ', left (name, 1), '. ', left (middleName, 1), '.' ) as fullName, notes " +
                "from employees " +
                "where lastDay is null " +
                "order by surname asc", BeanPropertyRowMapper.newInstance(DTOEmployees.class));
    }

    public static List<DTOEmployees> selectFreeEmployeesSkills(LocalDate dateView, String skill) {
        return getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName, notes " +
                "from skills_employees, skills, employees left join ( " +
                "   select employees_id " +
                "   from object_employees " +
                "   where (startDate <= ? and finishDate is null) or " +
                "   ? between startDate and finishDate) who_is_on_object " +
                "   on employees.id = who_is_on_object.employees_id " +
                "where who_is_on_object.employees_id is null and " +
                "employees.lastDay is null and " +
                "employees.id = skills_employees.employees_id and " +
                "skills_employees.skills_id = skills.id and " +
                "skills.skill = ? " +
                "order by employees.surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class), dateView, dateView, skill);
    }

    public static List<DTOEmployees> selectAllEmployeesSkills(String skill) {
        return getJdbcTemplate().query("select employees.id, " +
                "concat(surname, ' ', left (name, 1), '. ', left (middleName, 1), '.' ) as fullName, notes " +
                "from employees, skills_employees, skills " +
                "where skills.skill = ? and " +
                "skills.id = skills_employees.skills_id and " +
                "skills_employees.employees_id = employees.id and " +
                "employees.lastDay is null " +
                "order by surname asc", BeanPropertyRowMapper.newInstance(DTOEmployees.class), skill);
    }

    public static List<String> selectEmployeesSkills(Integer employeeId){
        return getJdbcTemplate().query("select skills.skill " +
                "from skills_employees, skills " +
                "where skills_employees.employees_id = ? and " +
                "skills_employees.skills_id = skills.id", (RowMapper) (resultSet, i) -> resultSet.getString(1),
                employeeId);
    }

    public static String selectEmployeesNotes(Integer employeeId) {
        return getJdbcTemplate().queryForObject("select notes " +
                        "from employees " +
                        "where id = ?",
                new Object []{employeeId}, String.class);
    }

    public static String selectEmployeesName(Integer employeeId) {
        return getJdbcTemplate().queryForObject("select concat(surname, ' ', name, ' ', middleName) as name " +
                        "from employees " +
                        "where id = ?",
                new Object []{employeeId}, String.class);
    }

    public static List<DTOObjects> selectObjects(LocalDate dateView) {
        return getJdbcTemplate().query("select id, address " +
                "from object " +
                "where startDate <= ? and " +
                "(finishDate >= ? or " +
                "finishDate is null) " +
                "order by address asc", BeanPropertyRowMapper.newInstance(DTOObjects.class), dateView, dateView);
    }

    public static List<DTOObjectEmployees> selectObjectEmployeesList(Integer objectId){
        return getJdbcTemplate().query("select object_employees.id, object_id as objectId, " +
                "   employees_id as employeeId, startDate, finishDate, concat( employees.surname, ' ', " +
                "   left (employees.name, 1), '. ', left (employees.middleName, 1), '.' ) as fullName " +
                "from object_employees, employees " +
                "where object_id = ? and " +
                "object_employees.employees_id = employees.id " +
                "order by startDate desc",
                BeanPropertyRowMapper.newInstance(DTOObjectEmployees.class), objectId);
    }

    public static String selectObjectAddress(Integer objectId) {
        return getJdbcTemplate().queryForObject("select address " +
                        "from object " +
                        "where id = ?",
                new Object []{objectId}, String.class);
    }

    public static void insertIntoObjectEmployees(DTOObjectEmployees dtoObjectEmployees) {
        getNamedParameterJdbcTemplate().update("INSERT INTO object_employees " +
                        "(id, object_id, employees_id, startDate, finishDate) " +
                        "VALUES (:id, :objectId, :employeeId, :startDate, :finishDate)",
                new BeanPropertySqlParameterSource(dtoObjectEmployees));
    }

    public static void deleteFromObjectEmployees(Integer recordsIdList) {
        getJdbcTemplate().update("DELETE FROM object_employees " +
                "WHERE id = ?", recordsIdList);
    }

    public static String selectEmployeesFullName(Integer employeeId) {
        return getJdbcTemplate().queryForObject("select concat(surname, ' ', left (name, 1), '. ', " +
                        "left (middleName, 1), '.' ) as fullName " +
                        "from employees " +
                        "where id = ?",
                new Object []{employeeId}, String.class);
    }

    public static DTOObjects selectObject(Integer objectId) {
        List<DTOObjects> stringList = getJdbcTemplate().query("select id, address, startDate, finishDate " +
                "from object " +
                "where id = ?", BeanPropertyRowMapper.newInstance(DTOObjects.class), objectId);
        return stringList.get(0);
    }

    public static void updateObjectEmployees(DTOObjectEmployees dtoObjectEmployees){
        getNamedParameterJdbcTemplate().update("UPDATE object_employees " +
                        "SET startDate = :startDate, " +
                        "finishDate = :finishDate " +
                        "WHERE id = :id",
                new BeanPropertySqlParameterSource(dtoObjectEmployees));
    }

    public static LocalDate selectMinWorkDate(Integer objectEmployeesId) {
        return getJdbcTemplate().queryForObject("select min(date) " +
                        "from worktracking " +
                        "where object_employees_id = ?",
                new Object []{objectEmployeesId}, LocalDate.class);
    }

    public static LocalDate selectMaxWorkDate(Integer objectEmployeesId) {
        return getJdbcTemplate().queryForObject("select max(date) " +
                "from worktracking " +
                "where object_employees_id = ?",
                new Object []{objectEmployeesId}, LocalDate.class);
    }

    public static LocalDate selectLastObjEmpFinishDate(Integer employeesId, LocalDate dateView) {
        return getJdbcTemplate().queryForObject("select max(finishDate) " +
                        "from object_employees " +
                        "where employees_id = ? and " +
                        "finishDate < ?",
                new Object []{employeesId, dateView}, LocalDate.class);
    }

    public static LocalDate selectNextObjEmpStartDate(Integer employeesId, LocalDate dateView) {
        return getJdbcTemplate().queryForObject("select min(startDate) " +
                        "from object_employees " +
                        "where employees_id = ? and " +
                        "startDate > ?",
                new Object []{employeesId, dateView}, LocalDate.class);
    }

    public static DTOObjectEmpAddress selectIfEmployeeIsOnObject(Integer employeeId, LocalDate dateView) {
        List<DTOObjectEmpAddress> dtoObjectEmpAddressesList = getJdbcTemplate().query("select object_employees.id, " +
                "object_employees.startDate, object_employees.finishDate, object.address " +
                "from object_employees, object " +
                "where object_employees.employees_id = ? and " +
                "object_employees.startDate <= ? and ( " +
                "object_employees.finishDate >= ? or " +
                "object_employees.finishDate is null) and " +
                "object_employees.object_id = object.id", BeanPropertyRowMapper.newInstance(DTOObjectEmpAddress.class),
                employeeId, dateView, dateView);

        if (dtoObjectEmpAddressesList.size() == 0) {
            return null;
        } else {
            return dtoObjectEmpAddressesList.get(0);
        }
    }

    public static void updateFinishDate (Integer objectEmployeesId, LocalDate finishDate){
        getJdbcTemplate().update("UPDATE object_employees " +
                "SET finishDate = ? " +
                "WHERE id = ?", finishDate, objectEmployeesId);
    }

    public static List<String> selectAllSkills(){
        return getJdbcTemplate().query("select skill from skills",
                (RowMapper) (resultSet, i) -> resultSet.getString(1));
    }

}