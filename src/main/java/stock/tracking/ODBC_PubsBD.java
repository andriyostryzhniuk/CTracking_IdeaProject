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

import static sample.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoStock> selectAllStockOfType(String stockType) {
        List<DtoStock> dtoStockList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.type = '" + stockType + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static List<DtoStock> selectStockOfCategory(String stockType) {
        List<DtoStock> dtoStockList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.name = '" + stockType + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

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

    public static ObservableList<String> selectStockCategoryName(String stockType) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from stockCategory " +
                "where stockCategory.type = '" + stockType + "' " +
                "order by stockCategory.name asc");
        ObservableList<String> stockCategoryList = FXCollections.observableArrayList();
        stockCategoryList.addAll("Всі категорії", "Весь інвентар");
        while (rs.next()) {
            stockCategoryList.add(rs.getString(1));
        }
        return stockCategoryList;
    }

    public static List<DtoStockCategory> selectStockCategory(String stockType) {
        List<DtoStockCategory> dtoStockCategoryList = getJdbcTemplate().query("select stockcategory.id, " +
                "stockcategory.name, ifnull(numberOfAvailableStockCategory.count, 0) as numberOfStock " +
                "from stockcategory left join " +
                "   (select stockCategory.id as id, count(*) as count " +
                "   from stockCategory, stock left join " +
                "       (select distinct stocktracking.stock_id " +
                "       from stocktracking " +
                "       where stocktracking.returnDate is null or " +
                "       stocktracking.returnDate > curdate()) usingStock " +
                "       on stock.id = usingStock.stock_id " +
                "   where usingStock.stock_id is null and " +
                "   stock.status = 'доступно' and " +
                "   stock.stockCategory_id = stockCategory.id " +
                "   group by stockCategory.id) numberOfAvailableStockCategory " +
                "   on numberOfAvailableStockCategory.id = stockcategory.id " +
                "where stockCategory.type = '" + stockType + "' " +
                "order by stockCategory.name asc", BeanPropertyRowMapper.newInstance(DtoStockCategory.class));
        return dtoStockCategoryList;
    }

    public static ObservableList<String> selectNumberStockOfCategory(String stockType) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from stockCategory " +
                "where stockCategory.type = '" + stockType + "' " +
                "order by stockCategory.name asc");
        ObservableList<String> stockCategoryList = FXCollections.observableArrayList();
        stockCategoryList.addAll("Всі категорії", "Весь інвентар");
        while (rs.next()) {
            stockCategoryList.add(rs.getString(1));
        }
        return stockCategoryList;
    }
}