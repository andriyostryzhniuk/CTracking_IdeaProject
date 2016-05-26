package stock.tracking;

import objects.tracking.dto.DTOObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import stock.tracking.dto.*;
import java.time.LocalDate;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBDForLiable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForLiable.class);

    public static List<DtoEmployees> selectAllEmployees() {
        return getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees " +
                "where employees.lastDay is null " +
                "order by employees.surname asc;", BeanPropertyRowMapper.newInstance(DtoEmployees.class));
    }

    public static List<DtoEmployees> selectEmployeesOnObject(Integer objectId, LocalDate dateView) {
        return getJdbcTemplate().query("select employees.id, " +
                "concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                "   left (employees.middleName, 1), '.' ) as fullName " +
                "from employees, object_employees, object " +
                "where object.id = ? and " +
                "object.id = object_employees.object_id and " +
                "object_employees.startDate <= ? and " +
                "(object_employees.finishDate >= ? or " +
                "object_employees.finishDate is null) and " +
                "object_employees.employees_id = employees.id " +
                "order by employees.surname asc",
                BeanPropertyRowMapper.newInstance(DtoEmployees.class), objectId, dateView, dateView);
    }

    public static void insertIntoStockTracking(DTOStockTracking dtoStockTracking){
        getNamedParameterJdbcTemplate().update("INSERT INTO stocktracking " +
                "(id, stock_id, employees_id, object_id, givingDate, returnDate) " +
                "VALUES (:id, :stockId, :employeesId, :objectId, :givingDate, :returnDate)",
                new BeanPropertySqlParameterSource(dtoStockTracking));
    }

    public static void updateStockTracking(DTOStockTracking dtoStockTracking){
        getNamedParameterJdbcTemplate().update("UPDATE stocktracking " +
                        "SET givingDate = :givingDate, " +
                        "returnDate = :returnDate " +
                        "WHERE id = :id",
                new BeanPropertySqlParameterSource(dtoStockTracking));
    }

    public static void deleteFromStockTracking(Integer recordId) {
        getJdbcTemplate().update("DELETE FROM stockTracking " +
                "WHERE id = ?", recordId);
    }

    public static List<DtoObject> selectObjects(LocalDate dateView){
        return getJdbcTemplate().query("select id, address " +
                "from object " +
                "where startDate <= ? and " +
                "(finishDate >= ? or " +
                "finishDate is null) " +
                "order by address asc",
                BeanPropertyRowMapper.newInstance(DtoObject.class), dateView, dateView);
    }

    public static List<DTOStockTracking> selectStockTracking(Integer objectId, Integer employeeId, LocalDate dateView) {
        if (objectId == null) {
            return getJdbcTemplate().query("SELECT stocktracking.id, stocktracking.stock_id AS stockId, " +
                    "ifnull(stock.name, stockCategory.name) AS stockName, stockCategory.name AS stockCategory, " +
                    "stocktracking.employees_id AS employeesId, stocktracking.object_id AS objectId, " +
                    "givingDate, returnDate " +
                            "FROM stock, stocktracking, stockcategory " +
                            "WHERE stocktracking.object_id IS NULL AND " +
                            "stocktracking.employees_id = ? AND " +
                            "stocktracking.givingDate <= ? AND ( " +
                            "stocktracking.returnDate is null OR " +
                            "stocktracking.returnDate >= ?) AND " +
                            "stocktracking.stock_id = stock.id AND " +
                            "stock.stockCategory_id = stockCategory.id " +
                            "ORDER BY givingDate DESC",
                    BeanPropertyRowMapper.newInstance(DTOStockTracking.class), employeeId, dateView, dateView);
        } else if (employeeId == null) {
            return getJdbcTemplate().query("SELECT stocktracking.id, stocktracking.stock_id AS stockId, " +
                    "ifnull(stock.name, stockCategory.name) AS stockName, stockCategory.name AS stockCategory, " +
                    "stocktracking.employees_id AS employeesId, stocktracking.object_id AS objectId, " +
                    "givingDate, returnDate " +
                            "FROM stock, stocktracking, stockcategory " +
                            "WHERE stocktracking.object_id = ? AND " +
                            "stocktracking.employees_id IS NULL AND " +
                            "stocktracking.givingDate <= ? AND ( " +
                            "stocktracking.returnDate is null OR " +
                            "stocktracking.returnDate >= ?) AND " +
                            "stocktracking.stock_id = stock.id AND " +
                            "stock.stockCategory_id = stockCategory.id " +
                            "ORDER BY givingDate DESC",
                    BeanPropertyRowMapper.newInstance(DTOStockTracking.class), objectId, dateView, dateView);
        } else {
            return getJdbcTemplate().query("SELECT stocktracking.id, stocktracking.stock_id AS stockId, " +
                    "ifnull(stock.name, stockCategory.name) AS stockName, stockCategory.name AS stockCategory, " +
                    "stocktracking.employees_id AS employeesId, stocktracking.object_id AS objectId, " +
                    "givingDate, returnDate " +
                            "FROM stock, stocktracking, stockcategory " +
                            "WHERE stocktracking.object_id = ? AND " +
                            "stocktracking.employees_id = ? AND " +
                            "stocktracking.givingDate <= ? AND ( " +
                            "stocktracking.returnDate is null OR " +
                            "stocktracking.returnDate >= ?) AND " +
                            "stocktracking.stock_id = stock.id AND " +
                            "stock.stockCategory_id = stockCategory.id " +
                            "ORDER BY givingDate DESC",
                    BeanPropertyRowMapper.newInstance(DTOStockTracking.class), objectId, employeeId, dateView, dateView);
        }
    }

    public static String selectEmployeesFullName(Integer employeeId) {
        return getJdbcTemplate().queryForObject("select concat(surname, ' ', name, ' ', middleName) as name " +
                        "from employees " +
                        "where id = ?",
                new Object []{employeeId}, String.class);
    }

    public static String selectEmployeesShortName(Integer employeeId) {
        return getJdbcTemplate().queryForObject(
                        "select concat( employees.surname, ' ', left (employees.name, 1), '. ', " +
                        "   left (employees.middleName, 1), '.' ) as shortName " +
                        "from employees " +
                        "where id = ?",
                new Object []{employeeId}, String.class);
    }

    public static String selectObjectAddress(Integer objectId) {
        return getJdbcTemplate().queryForObject("select address " +
                        "from object " +
                        "where id = ?",
                new Object []{objectId}, String.class);
    }

    public static DTOObjects selectObject(Integer objectId) {
        List<DTOObjects> dtoObjectsList = getJdbcTemplate().query("select id, address, startDate, finishDate " +
                "from object " +
                "where id = ?", BeanPropertyRowMapper.newInstance(DTOObjects.class), objectId);
        return dtoObjectsList.get(0);
    }

    public static DTOEmployeesFullInfo selectEmployeesFullInfo(Integer employeeId) {
        List<DTOEmployeesFullInfo> dtoEmployeesFullInfo = getJdbcTemplate().query("select id, " +
                "concat(surname, ' ', name, ' ', middleName) as fullName, firstDay as firstDate, lastDay as lastDate " +
                        "from employees " +
                        "where id = ?", BeanPropertyRowMapper.newInstance(DTOEmployeesFullInfo.class), employeeId);
        return dtoEmployeesFullInfo.get(0);
    }

    public static LocalDate selectLastStockUsingDate(Integer stockId, LocalDate dateView) {
        return getJdbcTemplate().queryForObject("select max(returnDate) " +
                        "from stockTracking " +
                        "where stock_id = ? and " +
                        "returnDate < ?",
                new Object []{stockId, dateView}, LocalDate.class);
    }

    public static LocalDate selectNextStockUsingDate(Integer stockId, LocalDate dateView) {
        return getJdbcTemplate().queryForObject("select min(givingDate) " +
                        "from stockTracking " +
                        "where stock_id = ? and " +
                        "givingDate > ?",
                new Object []{stockId, dateView}, LocalDate.class);
    }

    public static LocalDate selectObjectFinishDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select finishDate " +
                        "from object " +
                        "where id = ? ",
                new Object []{objectId}, LocalDate.class);
    }

    public static DTOStockTracking selectIfStockIsDisable(Integer stockId, LocalDate dateView) {
        List<DTOStockTracking> dtoStockTrackingList = getJdbcTemplate().query("SELECT stocktracking.id, " +
                "stocktracking.stock_id AS stockId, ifnull(stock.name, stockCategory.name) AS stockName, " +
                "stockCategory.name AS stockCategory, stocktracking.employees_id AS employeesId, " +
                "stocktracking.object_id AS objectId, givingDate, returnDate " +
                "FROM stock, stockCategory, stocktracking " +
                "WHERE stock.id = ? AND " +
                "stock.id = stocktracking.stock_id AND " +
                "stocktracking.givingDate <= ? AND ( " +
                "stocktracking.returnDate IS NULL OR " +
                "stocktracking.returnDate >= ?) AND " +
                "stock.stockCategory_id = stockCategory.id",
                BeanPropertyRowMapper.newInstance(DTOStockTracking.class), stockId, dateView, dateView);

        if (dtoStockTrackingList.size() == 0) {
            return null;
        } else {
            return dtoStockTrackingList.get(0);
        }
    }

}