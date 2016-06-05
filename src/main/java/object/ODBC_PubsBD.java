package object;

import javafx.scene.control.Alert;
import object.dto.DTOObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import subsidiary.classes.AlertWindow;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.DB_Connector.creteSimpleJdbcInsert;
import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);
    private static AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR);

    private static SimpleJdbcInsert simpleJdbcInsertForEmployees;

    private static SimpleJdbcInsert getSimpleJdbcInsertForObject() {
        if (simpleJdbcInsertForEmployees == null) {
            simpleJdbcInsertForEmployees = creteSimpleJdbcInsert();
            simpleJdbcInsertForEmployees
                    .withTableName("object")
                    .usingGeneratedKeyColumns("id");
        }
        return simpleJdbcInsertForEmployees;
    }

    public static List<DTOObject> selectObjectsList(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        return getJdbcTemplate().query("SELECT id AS objectsId, address, startDate, finishDate, " +
                        "customers_id AS customersId, estimatedCost, notes " +
                        "from object " +
                        "WHERE startDate BETWEEN ? AND ? OR " +
                        "finishDate BETWEEN ? AND ? OR " +
                        "startDate < ? AND finishDate > ? OR " +
                        "startDate < ? AND finishDate IS NULL " +
                        "ORDER BY address ASC",
                BeanPropertyRowMapper.newInstance(DTOObject.class), firstDayOfMonth, lastDayOfMonth,
                firstDayOfMonth, lastDayOfMonth, firstDayOfMonth, lastDayOfMonth, firstDayOfMonth);
    }

    public static Integer insertIntoObject(DTOObject dtoObject){
        Map<String, Object> parameters = new HashMap<>(6);
        parameters.put("address", dtoObject.getAddress());
        parameters.put("startDate", dtoObject.getStartDate());
        parameters.put("finishDate", dtoObject.getFinishDate());
        parameters.put("customers_id", dtoObject.getCustomersId());
        parameters.put("estimatedCost", dtoObject.getEstimatedCost());
        parameters.put("notes", dtoObject.getNotes());
        return getSimpleJdbcInsertForObject().executeAndReturnKey(parameters).intValue();
    }

    public static void updateObject(DTOObject dtoObject) {
        getNamedParameterJdbcTemplate().update("UPDATE object " +
                        "SET address = :address, " +
                        "startDate = :startDate, " +
                        "finishDate = :finishDate, " +
                        "customers_id = :customersId, " +
                        "estimatedCost = :estimatedCost, " +
                        "notes = :notes " +
                        "WHERE id = :objectsId",
                new BeanPropertySqlParameterSource(dtoObject));
    }

    public static void deleteFromObject(DTOObject dtoObject){
        try {
            getNamedParameterJdbcTemplate().update("DELETE FROM object " +
                            "WHERE id = :objectsId",
                    new BeanPropertySqlParameterSource(dtoObject));
        } catch (DataIntegrityViolationException e) {
            alertWindow.showDeletingError();
        }
    }

    public static LocalDate selectMinObjEmpDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select min(object_employees.startDate) " +
                        "from object_employees " +
                        "where object_employees.object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMinStockTrackingDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select min(stocktracking.givingDate) " +
                        "from stocktracking " +
                        "where object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMinObjEmpStartDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select max(object_employees.startDate) " +
                        "from object_employees " +
                        "where object_employees.object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMinObjEmpFinishDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select max(object_employees.finishDate) " +
                        "from object_employees " +
                        "where object_employees.object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMaxWorckTrackingDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select max(worktracking.date) " +
                        "from object_employees, worktracking " +
                        "where object_id = ? and " +
                        "object_employees.id = worktracking.object_employees_id",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMaxStockTrackingGivingDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select max(stocktracking.givingDate) " +
                        "from stocktracking " +
                        "where object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

    public static LocalDate selectMaxStockTrackingReturnDate(Integer objectId) {
        return getJdbcTemplate().queryForObject("select max(stocktracking.returnDate) " +
                        "from stocktracking " +
                        "where object_id = ?",
                new Object []{objectId}, LocalDate.class);
    }

}