package stock.tracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import stock.tracking.dto.*;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBDForStock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForStock.class);

    public static List<DtoStock> selectAllStockOfType(String stockType) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.type = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class), stockType);
    }

    public static List<DtoStock> selectAllStockOfTypeInRepository(String stockType, String repositoryName) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = ? and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.type = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class), repositoryName, stockType);
    }

    public static List<DtoStock> selectStockOfCategory(String stockCategory) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.name = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class), stockCategory);
    }

    public static List<DtoStock> selectStockOfCategoryInRepository(String stockCategory, String repositoryName) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = ? and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.name = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class),
                repositoryName, stockCategory);
    }

    public static List<DtoStockCategory> selectStockCategory(String stockType, boolean showDisableStock) {
        List<DtoStockCategory> dtoStockCategoryList = getJdbcTemplate().query("select stockCategory.id as id, " +
                "stockcategory.name as name, count(*) as numberOfStock " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id  and " +
                "stockCategory.type = ? " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class), stockType);

        if (! showDisableStock) {
            dtoStockCategoryList.addAll(getJdbcTemplate().query("select stockcategory.id as id, stockcategory.name as name, " +
                    "ifnull(numberOfAvailableStockCategory.count, 0) as numberOfStock " +
                    "from stockcategory left join " +
                    "   (select stockCategory.id as id, count(*) as count  " +
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
                    "where stockCategory.type = ? and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class), stockType));
        }

        return dtoStockCategoryList;
    }

    public static List<DtoStockCategory> selectStockCategoryInRepository
            (String stockType, String repositoryName, boolean showDisableStock) {
        List<DtoStockCategory> dtoStockCategoryList = getJdbcTemplate().query("select stockCategory.id as id, " +
                "stockcategory.name as name, count(*) as numberOfStock " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = ? and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id  and " +
                "stockCategory.type = ? " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class),
                repositoryName, stockType);

        if (! showDisableStock) {
            dtoStockCategoryList.addAll(getJdbcTemplate().query("select stockcategory.id as id, stockcategory.name as name, " +
                    "ifnull(numberOfAvailableStockCategory.count, 0) as numberOfStock " +
                    "from stockcategory left join " +
                    "   (select stockCategory.id as id, count(*) as count  " +
                    "   from repository, stockCategory, stock left join " +
                    "       (select distinct stocktracking.stock_id " +
                    "       from stocktracking " +
                    "       where stocktracking.returnDate is null or " +
                    "       stocktracking.returnDate > curdate()) usingStock " +
                    "       on stock.id = usingStock.stock_id " +
                    "   where usingStock.stock_id is null and " +
                    "   stock.status = 'доступно' and " +
                    "   stock.stockCategory_id = stockCategory.id and " +
                    "    stock.repository_id = repository.id and " +
                    "   repository.name = ? " +
                    "   group by stockCategory.id) numberOfAvailableStockCategory " +
                    "   on numberOfAvailableStockCategory.id = stockcategory.id " +
                    "where stockCategory.type = ? and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class),
                    repositoryName, stockType));
        }
        return dtoStockCategoryList;
    }

    public static List<DtoStock> selectStockOfCategoryWithId (int stockCategoryId) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.id = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class), stockCategoryId);
    }

    public static List<DtoStock> selectStockOfCategoryWithIdInRepository (int stockCategoryId, String repositoryName) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = ? and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.id = ? " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class),
                repositoryName, stockCategoryId);
    }

    public static String selectStockCategoryNameWithId (int stockCategoryId) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from stockCategory " +
                "where stockCategory.id = ?", stockCategoryId );
        String stockCategoryName = new String();
        while (rs.next()) {
            stockCategoryName = rs.getString(1);
        }
        return stockCategoryName;
    }

    public static ObservableList<String> selectRepositoryName() {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select repository.name " +
                "from repository " +
                "order by repository.name asc");
        ObservableList<String> repositoryNameList = FXCollections.observableArrayList();
        repositoryNameList.addAll("Всі склади");
        while (rs.next()) {
            repositoryNameList.add(rs.getString(1));
        }
        return repositoryNameList;
    }

    public static String selectStocksNotes(Integer stockId) {
        return getJdbcTemplate().queryForObject("select notes " +
                        "from stock " +
                        "where id = ?",
                new Object []{stockId}, String.class);
    }

}