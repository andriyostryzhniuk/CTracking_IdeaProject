package objects.tracking;

import objects.tracking.dto.DTOEmployees;
import objects.tracking.dto.DTOObjects;
import objects.tracking.dto.DTOObjectEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

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

        dtoEmployeesList.forEach(item -> selectSkills(item.getId()));

        return dtoEmployeesList;
    }

    public static List<DTOEmployees> selectAllEmployees() {
        List<DTOEmployees> dtoEmployeesList = getJdbcTemplate().query("select id, " +
                "concat(surname, ' ', left (name, 1), '. ', left (middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where lastDay is null " +
                "order by surname asc", BeanPropertyRowMapper.newInstance(DTOEmployees.class));

        dtoEmployeesList.forEach(item -> selectSkills(item.getId()));

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

    public static void insertIntoObjectEmployees(List<DTOObjectEmployees> resultList){
        getNamedParameterJdbcTemplate().batchUpdate("INSERT INTO object_employees " +
                "(id, object_id, employees_id, startDate, finishDate) " +
                "VALUES (:id, :objectId, :employeeId, :startDate, :finishDate)",
                SqlParameterSourceUtils.createBatch(resultList.toArray()));
    }

}