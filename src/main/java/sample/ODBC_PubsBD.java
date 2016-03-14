package sample;

import dto.DtoWokingHoursADay;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import dto.DtoEmployeesFullName;
import java.util.List;

import static sample.DB_Connector.getJdbcTemplate;
import static sample.DB_Connector.getSimpleJdbcCall;


public class ODBC_PubsBD {
    public static List<DtoEmployeesFullName> selectEmployeesFullName(String firstDayOfMonth, String lastDayOfMonth) {
        List<DtoEmployeesFullName> dtoEmployeesFullNames = getJdbcTemplate().query("select employees.id, " +
                "concat(employees.surname, ' ', left (employees.name, 1), '. ', left (employees.middleName, 1), '.') as fullName, " +
                "employees.workingHours " +
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
                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DtoEmployeesFullName.class));
        return dtoEmployeesFullNames;
    }

    public static List<DtoWokingHoursADay> selectWorkingHours(String firstDayOfMonth, int employeesId) {
        int i = 0;
        getSimpleJdbcCall()
                .withProcedureName("init_table_days_of_month")
                .execute(new MapSqlParameterSource().addValue("firstDayOfMonth", firstDayOfMonth).addValue("i", i));

        List<DtoWokingHoursADay> dtoWokingHoursADay = getJdbcTemplate().query(
                        "select table_days_of_month.date, ifnull(newTable.workingHours, -1) as workingHours " +
                        "from (select worktracking.date, worktracking.workingHours " +
                            "from object_employees, worktracking " +
                            "where object_employees.employees_id = '" + employeesId + "' and " +
                            "object_employees.id = worktracking.object_employees_id and " +
                            "worktracking.date between convert('" + firstDayOfMonth + "', DATE) and " +
                            "adddate(convert('" + firstDayOfMonth + "', DATE), interval 30 day)) " +
                            "newTable right join table_days_of_month " +
                            "    on table_days_of_month.date = newTable.date",
                BeanPropertyRowMapper.newInstance(DtoWokingHoursADay.class));

        getJdbcTemplate().update("truncate table table_days_of_month");

        return dtoWokingHoursADay;
    }
}