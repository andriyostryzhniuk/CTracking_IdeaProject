package stocks;

import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import stocks.dto.DTORepository;
import stocks.dto.DTOStockCategory;
import stocks.dto.DTOStocks;
import subsidiary.classes.AlertWindow;
import java.util.List;
import static main.DB_Connector.getJdbcTemplate;
import static main.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);
    private static AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR);

    public static List<DTOStocks> selectStockList(String status) {
        return getJdbcTemplate().query("SELECT stock.id AS stockId, ifnull(stock.name, stockCategory.name) AS name, " +
                        "stock.stockCategory_id AS categoryId, stockcategory.name AS categoryName, stockcategory.type, " +
                        "price, notes, stock.repository_id AS repositoryId, repository.name AS repositoryName " +
                        "FROM stock, stockcategory, repository " +
                        "WHERE status = ? AND " +
                        "stock.stockCategory_id = stockcategory.id AND " +
                        "stock.repository_id = repository.id " +
                        "ORDER BY stockcategory.name",
                BeanPropertyRowMapper.newInstance(DTOStocks.class), status);
    }

    public static List<DTOStockCategory> selectStockCategoryList() {
        return getJdbcTemplate().query("SELECT id, name, type " +
                        "FROM stockcategory",
                BeanPropertyRowMapper.newInstance(DTOStockCategory.class));
    }

    public static List<String> selectStockCategoryNameList() {
        return getJdbcTemplate().query("SELECT name " +
                        "FROM stockcategory " +
                        "GROUP BY name ASC",
                (RowMapper) (resultSet, i) -> resultSet.getString(1));
    }

    public static List<DTORepository> selectRepositoryList() {
        return getJdbcTemplate().query("SELECT id, name " +
                        "FROM repository",
                BeanPropertyRowMapper.newInstance(DTORepository.class));
    }

    public static void insertIntoStock(DTOStocks dtoStocks){
        getNamedParameterJdbcTemplate().update("INSERT INTO stock " +
                        "(id, name, stockCategory_id, price, status, notes, repository_id) " +
                        "VALUES (:stockId, :name, :categoryId, :price, :status, :notes, :repositoryId)",
                new BeanPropertySqlParameterSource(dtoStocks));
    }

    public static Integer selectCategoryId(String categoryName) {
        return getJdbcTemplate().queryForObject("SELECT id " +
                        "FROM stockcategory " +
                        "WHERE name = ?",
                new Object[]{categoryName}, Integer.class);
    }

    public static void deleteFromStock(DTOStocks dtoStocks){
        try {
        getNamedParameterJdbcTemplate().update("DELETE FROM stock " +
                        "WHERE id = :stockId",
                new BeanPropertySqlParameterSource(dtoStocks));
        } catch (DataIntegrityViolationException e) {
            alertWindow.showDeletingError();
        }
    }

    public static void updateStock (DTOStocks dtoStocks) {
        getNamedParameterJdbcTemplate().update("UPDATE stock " +
                        "SET name = :name, " +
                        "stockCategory_id = :categoryId, " +
                        "price = :price, " +
                        "notes = :notes, " +
                        "repository_id = :repositoryId " +
                        "WHERE id = :stockId",
                new BeanPropertySqlParameterSource(dtoStocks));
    }

}