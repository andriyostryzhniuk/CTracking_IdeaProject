package employees;

import employees.dto.DTOEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DTOEmployees> selectEmployeesList() {
        return getJdbcTemplate().query("SELECT id, name, surname, middleName, birthDate, firstDay AS firstDate, " +
                "notes, workingHours " +
                        "FROM employees " +
                        "WHERE lastDay IS NULL " +
                        "order by surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class));
    }

    public static DTOEmployees selectEmployeesById(Integer employeesId) {
        List<DTOEmployees> dtoEmployeesList = getJdbcTemplate().query("SELECT id, name, surname, middleName, " +
                "birthDate, firstDay AS firstDate, lastDay AS lastDate, notes, workingHours " +
                        "FROM employees " +
                        "WHERE id = ? " +
                        "order by surname asc",
                BeanPropertyRowMapper.newInstance(DTOEmployees.class), employeesId);
        return dtoEmployeesList.get(0);
    }

}