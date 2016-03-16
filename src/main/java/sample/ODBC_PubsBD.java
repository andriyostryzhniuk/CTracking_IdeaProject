package sample;

import dto.DtoWokingHoursADay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import dto.DtoEmployeesFullName;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

import static sample.DB_Connector.getJdbcTemplate;
import static sample.DB_Connector.getSimpleJdbcCall;


public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

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

    public static void initTableDaysOfMonth (String firstDayOfMonth) {
        LOGGER.debug("selectWorkingHoursADay started");
        getJdbcTemplate().update("truncate table table_days_of_month");
        LOGGER.debug("table_days_of_month truncated");

        getSimpleJdbcCall()
                .withProcedureName("init_table_days_of_month")
                .execute(new MapSqlParameterSource().addValue("firstDayOfMonth", firstDayOfMonth));
        LOGGER.debug("init_table_days_of_month executed");
    }

    public static List<DtoWokingHoursADay> selectWorkingHoursADay(String firstDayOfMonth, int employeesId) {
        List<DtoWokingHoursADay> dtoWorkingHoursADay = getJdbcTemplate().query(
                "SELECT table_days_of_month.date, ifnull(newTable.workingHours, -1) as workingHours " +
                        "from (select worktracking.date, worktracking.workingHours " +
                        "from object_employees, worktracking " +
                        "where object_employees.employees_id = '" + employeesId + "' and " +
                        "object_employees.id = worktracking.object_employees_id and " +
                        "worktracking.date between convert('" + firstDayOfMonth + "', DATE) and " +
                        "adddate(convert('" + firstDayOfMonth + "', DATE), interval 30 day)) " +
                        "newTable right join table_days_of_month " +
                        "    on table_days_of_month.date = newTable.date",
                BeanPropertyRowMapper.newInstance(DtoWokingHoursADay.class));
        LOGGER.debug("query finished");
        return dtoWorkingHoursADay;
    }

    public static Integer selectObjectEmployeesId(String date, int employeesId) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select object_employees.id " +
                "from object_employees " +
                "where object_employees.employees_id = '" + employeesId + "' and " +
                "(object_employees.startDate <= convert('" + date + "', DATE) and " +
                "object_employees.finishDate is null) or " +
                "convert('" + date + "', DATE) between object_employees.startDate and object_employees.finishDate");
        Integer objectEmployeesId = null;
        while (rs.next()) {
            objectEmployeesId = new Integer(rs.getInt(1));
        }
        return objectEmployeesId;
    }

    public static void insertIntoWorkTracking(String date, int workingHours, int employeesId){
        final int objectEmployeesId = selectObjectEmployeesId(date, employeesId);
        getJdbcTemplate().update("INSERT INTO worktracking (id, object_employees_id, date, workingHours) " +
                "VALUES (null, '" + objectEmployeesId + "', convert('" + date + "', DATE), '" + workingHours + "')");
    }

    public static void updateWorkTracking(String date, int workingHours, int employeesId){
        final int objectEmployeesId = selectObjectEmployeesId(date, employeesId);
        getJdbcTemplate().update("UPDATE worktracking " +
                "SET workingHours = '" + workingHours + "' " +
                "WHERE object_employees_id = '" + objectEmployeesId + "' and " +
                "date = convert('" + date + "', DATE)");
    }

    public static void deleteFromWorkTracking(String date, int employeesId){
        final int objectEmployeesId = selectObjectEmployeesId(date, employeesId);
        getJdbcTemplate().update("DELETE FROM worktracking " +
                "WHERE object_employees_id = '" + objectEmployeesId + "' and " +
                "date = convert('" + date + "', DATE)");
    }
}