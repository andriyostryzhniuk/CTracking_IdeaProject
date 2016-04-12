package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import stock.tracking.dto.DtoEmployees;
import stock.tracking.dto.DtoGrantedStock;
import stock.tracking.dto.DtoObject;
import stock.tracking.dto.DtoStockCategory;

import java.util.LinkedList;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBDForLiable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForLiable.class);

    public static List<DtoEmployees> selectAllEmployees() {
        List<DtoEmployees> dtoEmployeesList = getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where employees.lastDay is null " +
                "order by employees.surname asc;", BeanPropertyRowMapper.newInstance(DtoEmployees.class));
        return dtoEmployeesList;
    }

    public static List<DtoEmployees> selectEmployeesOnObject(Integer objectId) {
        List<DtoEmployees> dtoEmployeesList = getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees, object_employees, object " +
                "where object.id = '" + objectId + "' and " +
                "object.startDate <= curdate() and " +
                "(object.finishDate >= curdate() or " +
                "object.finishDate is null) and " +
                "object.id = object_employees.object_id and " +
                "object_employees.startDate <= curdate() and " +
                "(object_employees.finishDate >= curdate() or " +
                "object_employees.finishDate is null) and " +
                "object_employees.employees_id = employees.id " +
                "order by employees.surname asc", BeanPropertyRowMapper.newInstance(DtoEmployees.class));
        return dtoEmployeesList;
    }

    public static void insertIntoWorkTracking(Integer stockId, Integer employeesId, Integer objectId){
        getJdbcTemplate().update("INSERT INTO stocktracking (id, stock_id, employees_id, object_id, givingDate, returnDate) " +
                "VALUES (null, " + stockId + ", " + employeesId + ", " + objectId + ", curdate(), null)");
    }

    public static List<DtoObject> selectObjects(){
        List<DtoObject> dtoObjectList = getJdbcTemplate().query("select id, address " +
                "from object " +
                "where startDate <= curdate() and " +
                "(finishDate >= curdate() or " +
                "finishDate is null) " +
                "order by address asc", BeanPropertyRowMapper.newInstance(DtoObject.class));
        return dtoObjectList;
    }

    public static ObservableList<DtoGrantedStock> selectStockWithId(LinkedList<Integer> stockIdList) {
        ObservableList<DtoGrantedStock> dtoGrantedStock = FXCollections.observableArrayList();
        stockIdList.forEach(stockId -> {
            dtoGrantedStock.addAll(getJdbcTemplate().query("select stock.id as id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, " +
                    "stockCategory.id as stockCategoryId " +
                    "from stockCategory, stock " +
                    "where stock.id = '" + stockId + "' and " +
                    "stock.stockCategory_id = stockCategory.id", BeanPropertyRowMapper.newInstance(DtoGrantedStock.class)));
        });
        return dtoGrantedStock;
    }

}