package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.dto.DtoEmployeesFullName;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Andriy on 01/28/2016.
 */
public class ODBC_PubsBD {
    public ObservableList<DtoEmployeesFullName> selectEmployeesFullName() throws SQLException {
        try (Connection connection = DB_Connector.getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select employees.id, employees.name, employees.surname, " +
                    "employees.middleName from employees");
            ObservableList<DtoEmployeesFullName> employeesFullNameList = FXCollections.observableArrayList();
            while (rs.next()) {
                DtoEmployeesFullName employeesFullName = new DtoEmployeesFullName(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4));
                employeesFullNameList.add(employeesFullName);
            }
            return employeesFullNameList;
        }
    }
}