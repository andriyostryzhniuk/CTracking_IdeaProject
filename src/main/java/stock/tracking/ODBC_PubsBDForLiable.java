package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import stock.tracking.dto.DtoEmployees;
import stock.tracking.dto.DtoStock;
import stock.tracking.dto.DtoStockCategory;

import java.util.List;

import static main.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBDForLiable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForLiable.class);

    public static List<DtoEmployees> selectEmployees() {
        List<DtoEmployees> dtoEmployeesList = getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where employees.lastDay is null " +
                "order by employees.surname asc;", BeanPropertyRowMapper.newInstance(DtoEmployees.class));
        return dtoEmployeesList;
    }

    public static void insertIntoWorkTracking(int stockId, int employeesId){
        getJdbcTemplate().update("INSERT INTO stocktracking (id, stock_id, employees_id, object_id, givingDate, returnDate) " +
                "VALUES (null, '" + stockId + "', '" + employeesId + "', null, curdate(), null)");
    }

}