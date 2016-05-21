package objects.tracking;

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
import java.util.Date;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DTOEmployees> selectFreeEmployees() {
        List<DTOEmployees> dtoEmployeesList = getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees left join ( " +
                "   select employees_id " +
                "   from object_employees " +
                "   where (startDate <= curdate() and finishDate is null) or " +
                "   curdate() between startDate and finishDate) who_is_on_object " +
                "   on employees.id = who_is_on_object.employees_id " +
                "where who_is_on_object.employees_id is null and " +
                "employees.lastDay is null " +
                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DTOEmployees.class));

        dtoEmployeesList.forEach(item -> item.setSkills(selectSkills(item.getId())));

        return dtoEmployeesList;
    }

    public static List<DTOEmployees> selectAllEmployees() {
        List<DTOEmployees> dtoEmployeesList = getJdbcTemplate().query("select id, " +
                "concat(surname, ' ', left (name, 1), '. ', left (middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where lastDay is null " +
                "order by surname asc", BeanPropertyRowMapper.newInstance(DTOEmployees.class));

        dtoEmployeesList.forEach(item -> item.setSkills(selectSkills(item.getId())));

        return dtoEmployeesList;
    }

    public static List<String> selectSkills(Integer employeeId){
        return getJdbcTemplate().query("select skills.skill " +
                "from skills_employees, skills " +
                "where skills_employees.employees_id = ? and " +
                "skills_employees.skills_id = skills.id", (RowMapper) (resultSet, i) -> resultSet.getString(1),
                employeeId);
    }

    public static List<DTOObjects> selectObjects() {
        List<DTOObjects> dtoObjectsList = getJdbcTemplate().query("select id, address " +
                "from object " +
                "where startDate <= curdate() and " +
                "(finishDate >= curdate() or " +
                "finishDate is null) " +
                "order by address asc", BeanPropertyRowMapper.newInstance(DTOObjects.class));

        dtoObjectsList.forEach(item -> {
            item.setObjectEmployeesList(selectObjectEmployeesList(item.getId()));
        });

        return dtoObjectsList;
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

    public static void insertIntoObjectEmployees(DTOObjectEmployees dtoObjectEmployees){
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
        List<String> stringList = getJdbcTemplate().query("select concat(surname, ' ', left (name, 1), '. ', " +
                "left (middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where id = ?", (RowMapper) (resultSet, i) -> resultSet.getString(1), employeeId);
        return stringList.get(0);
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

    public static Date selectMinWorkDate(Integer objectEmployeesId) {
        List<Date> dateList = getJdbcTemplate().query("select min(date) " +
                "from worktracking " +
                "where object_employees_id = ?", (RowMapper) (resultSet, i) -> resultSet.getDate(1), objectEmployeesId);
        if (dateList.get(0) != null) {
            return new java.util.Date(dateList.get(0).getTime());
        }
        return null;
    }

    public static Date selectMaxWorkDate(Integer objectEmployeesId) {
        List<Date> dateList = getJdbcTemplate().query("select max(date) " +
                "from worktracking " +
                "where object_employees_id = ?", (RowMapper) (resultSet, i) -> resultSet.getDate(1), objectEmployeesId);
        if (dateList.get(0) != null) {
            return new java.util.Date(dateList.get(0).getTime());
        }
        return null;
    }

    public static LocalDate selectMaxWorkDate2(Integer objectEmployeesId) {
        List<LocalDate> dateList = getJdbcTemplate().query("select max(date) " +
                "from worktracking " +
                "where object_employees_id = ?", (RowMapper) (resultSet, i) -> resultSet.getObject(1, LocalDate.class),
                objectEmployeesId);
        if (dateList.get(0) != null) {
            return dateList.get(0);
        }
        return null;
    }

    public static DTOObjectEmpAddress selectIfEmployeeIsOnObject(Integer employeeId) {
        List<DTOObjectEmpAddress> dtoObjectEmpAddressesList = getJdbcTemplate().query("select object.address, " +
                "object_employees.id as objectEmployeesId " +
                "from object_employees, object " +
                "where object_employees.employees_id = ? and " +
                "object_employees.startDate <= curdate() and ( " +
                "object_employees.finishDate >= curdate() or " +
                "object_employees.finishDate is null) and " +
                "object_employees.object_id = object.id", BeanPropertyRowMapper.newInstance(DTOObjectEmpAddress.class),
                employeeId);

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

}