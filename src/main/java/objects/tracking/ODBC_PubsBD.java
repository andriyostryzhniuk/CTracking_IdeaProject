package objects.tracking;

import objects.tracking.dto.DTOEmployees;
import objects.tracking.dto.DTOObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;

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
        return getJdbcTemplate().query("select id, address " +
                "from object " +
                "where startDate <= curdate() and " +
                "(finishDate >= curdate() or " +
                "finishDate is null) " +
                "order by address asc", BeanPropertyRowMapper.newInstance(DTOObjects.class));
    }

}