package object;

import javafx.scene.control.Alert;
import object.dto.DTOObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import stocks.dto.DTOStocks;
import subsidiary.classes.AlertWindow;

import java.time.LocalDate;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);
    private static AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR);

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

}