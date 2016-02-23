package sample;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import dto.DtoEmployeesFullName;
import java.util.List;

import static sample.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBD {
    public static List<DtoEmployeesFullName> selectEmployeesFullName(String firstDayOfMonth, String lastDayOfMonth) {
        List<DtoEmployeesFullName> dtoEmployeesFullNames = getJdbcTemplate().query("select employees.id, " +
                "concat(employees.surname, ' ', left (employees.name, 1), '. ', left (employees.middleName, 1), '.') as fullName " +
                "from employees left join " +
                "(select distinct employees_id " +
                "from object_employees " +
                "where startDate between convert('" + firstDayOfMonth + "', DATE) and convert('" + lastDayOfMonth + "', DATE) or " +
                "finishDate between convert('" + firstDayOfMonth + "', DATE) and convert('" + lastDayOfMonth + "', DATE) or " +
                "startDate < convert('" + firstDayOfMonth + "', DATE) and finishDate > convert('" + lastDayOfMonth + "', DATE) or " +
                "startDate < convert('" + firstDayOfMonth + "', DATE) and finishDate is null) who_is_on_object " +
                "on employees.id = who_is_on_object.employees_id " +
                "where who_is_on_object.employees_id is not null and " +
                "(employees.lastDay is null or " +
                "employees.lastDay > convert('" + firstDayOfMonth + "', DATE)) " +
                "order by employees.id asc", BeanPropertyRowMapper.newInstance(DtoEmployeesFullName.class));

//        List<DtoEmployeesFullName> dtoEmployeesFullNames = getJdbcTemplate().query("select employees.id, employees.surname as fullName " +
//                "from employees " +
//                "where employees.lastDay is null or " +
//                "employees.lastDay > '" + firstDayOfMonth + "' " +
//                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DtoEmployeesFullName.class));
        return dtoEmployeesFullNames;
    }

    public static int selectDefaultEmployeesWorkingHours(int employees_id) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select employees.workingHours " +
                "from employees " +
                "where employees.id = '" + employees_id + "'");
        Integer id = null;
        while (rs.next()) {
            id = new Integer(rs.getInt(1));
        }
        return id;
    }
}