package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import sample.dto.DtoEmployeesFullName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static sample.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBD {
    public static ObservableList<DtoEmployeesFullName> selectEmployeesFullName(String firstDayOfMonth, String lastDayOfMonth) {
//        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select employees.id, employees.name, employees.surname, " +
//                "employees.middleName " +
//                "from employees " +
//                "order by employees.surname asc");
////////
//        List<DtoEmployeesFullName> dtoEmployeesFullNames = getJdbcTemplate().query("select employees.id, employees.name, employees.surname, " +
//                "employees.middleName " +
//                "from employees " +
//                "where employees.lastDay is null or " +
//                "employees.lastDay > '" + firstDayOfMonth + "' " +
//                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DtoEmployeesFullName.class));
///////
        List<DtoEmployeesFullName> dtoEmployeesFullNames = getJdbcTemplate().query("select employees.id, employees.name, employees.surname, " +
                "employees.middleName " +
                "from employees " +
                "where employees.lastDay is null or " +
                "employees.lastDay > '" + firstDayOfMonth + "' " +
                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DtoEmployeesFullName.class));

        ObservableList<DtoEmployeesFullName> employeesFullNameList = FXCollections.observableArrayList();
        employeesFullNameList.addAll(dtoEmployeesFullNames);
//        while (rs.next()) {
//            DtoEmployeesFullName employeesFullName = new DtoEmployeesFullName(rs.getInt(1), rs.getString(2), rs.getString(3),
//                    rs.getString(4));
//            employeesFullNameList.add(employeesFullName);
//        }
        return employeesFullNameList;
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