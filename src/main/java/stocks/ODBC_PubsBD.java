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

import static main.DB_Connector.createDataSource;
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

    public static List<DTOStocks> selectStockOnRepositoryList(String status, Integer repositoryId) {
        return getJdbcTemplate().query("SELECT stock.id AS stockId, ifnull(stock.name, stockCategory.name) AS name, " +
                        "stock.stockCategory_id AS categoryId, stockcategory.name AS categoryName, stockcategory.type, " +
                        "price, notes, stock.repository_id AS repositoryId, repository.name AS repositoryName " +
                        "FROM stock, stockcategory, repository " +
                        "WHERE status = ? AND " +
                        "stock.stockCategory_id = stockcategory.id AND " +
                        "stock.repository_id = ? AND " +
                        "stock.repository_id = repository.id " +
                        "ORDER BY stockcategory.name",
                BeanPropertyRowMapper.newInstance(DTOStocks.class), status, repositoryId);
    }

    public static List<DTOStocks> selectStockWithCategoryList(String status, Integer categoryId) {
        return getJdbcTemplate().query("SELECT stock.id AS stockId, ifnull(stock.name, stockCategory.name) AS name, " +
                        "stock.stockCategory_id AS categoryId, stockcategory.name AS categoryName, stockcategory.type, " +
                        "price, notes, stock.repository_id AS repositoryId, repository.name AS repositoryName " +
                        "FROM stock, stockcategory, repository " +
                        "WHERE status = ? AND " +
                        "stock.stockCategory_id = ? AND " +
                        "stock.stockCategory_id = stockcategory.id AND " +
                        "stock.repository_id = repository.id " +
                        "ORDER BY stockcategory.name",
                BeanPropertyRowMapper.newInstance(DTOStocks.class), status, categoryId);
    }

    public static List<DTOStocks> selectStockWithCategoryOnRepositoryList(String status, Integer categoryId,
                                                                          Integer repositoryId) {
        return getJdbcTemplate().query("SELECT stock.id AS stockId, ifnull(stock.name, stockCategory.name) AS name, " +
                        "stock.stockCategory_id AS categoryId, stockcategory.name AS categoryName, stockcategory.type, " +
                        "price, notes, stock.repository_id AS repositoryId, repository.name AS repositoryName " +
                        "FROM stock, stockcategory, repository " +
                        "WHERE status = ? AND " +
                        "stock.stockCategory_id = ? AND " +
                        "stock.stockCategory_id = stockcategory.id AND " +
                        "stock.repository_id = ? AND " +
                        "stock.repository_id = repository.id " +
                        "ORDER BY stockcategory.name",
                BeanPropertyRowMapper.newInstance(DTOStocks.class), status, categoryId, repositoryId);
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

    public static void updateStatus (Integer stockId, String status) {
        getJdbcTemplate().update("UPDATE stock " +
                        "SET status = ? " +
                        "WHERE id = ?", status, stockId);
    }

    public static void insertIntoStockCategory(DTOStockCategory dtoStockCategory){
        getNamedParameterJdbcTemplate().update("INSERT INTO stockcategory " +
                        "(id, name, type) " +
                        "VALUES (:id, :name, :type)",
                new BeanPropertySqlParameterSource(dtoStockCategory));
    }

    public static void updateStockCategory(DTOStockCategory dtoStockCategory) {
        getNamedParameterJdbcTemplate().update("UPDATE stockcategory " +
                        "SET name = :name, " +
                        "type = :type " +
                        "WHERE id = :id",
                new BeanPropertySqlParameterSource(dtoStockCategory));
    }

    public static void deleteFromStockCategory(DTOStockCategory dtoStockCategory){
        try {
            getNamedParameterJdbcTemplate().update("DELETE FROM stockcategory " +
                            "WHERE id = :id",
                    new BeanPropertySqlParameterSource(dtoStockCategory));
        } catch (DataIntegrityViolationException e) {
            alertWindow.showDeletingError();
        }
    }

    public static void insertIntoRepository(DTORepository dtoRepository){
        getNamedParameterJdbcTemplate().update("INSERT INTO repository " +
                        "(id, name) " +
                        "VALUES (:id, :name)",
                new BeanPropertySqlParameterSource(dtoRepository));
    }

    public static void updateRepository(DTORepository dtoRepository) {
        getNamedParameterJdbcTemplate().update("UPDATE repository " +
                        "SET name = :name " +
                        "WHERE id = :id",
                new BeanPropertySqlParameterSource(dtoRepository));
    }

    public static void deleteFromRepository(DTORepository dtoRepository){
        try {
            getNamedParameterJdbcTemplate().update("DELETE FROM repository " +
                            "WHERE id = :id",
                    new BeanPropertySqlParameterSource(dtoRepository));
        } catch (DataIntegrityViolationException e) {
            alertWindow.showDeletingError();
        }
    }

}