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


public class ODBC_PubsBDForStock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForStock.class);

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

    public static List<DtoStock> selectAllStockOfTypeInRepository(String stockType, String repositoryName) {
        List<DtoStock> dtoStockList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = '" + repositoryName + "' and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.type = '" + stockType + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static List<DtoStock> selectStockOfCategory(String stockCategory) {
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
                "stockCategory.name = '" + stockCategory + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static List<DtoStock> selectStockOfCategoryInRepository(String stockCategory, String repositoryName) {
        List<DtoStock> dtoStockList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = '" + repositoryName + "' and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.name = '" + stockCategory + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static ObservableList<String> selectStockCategoryName(String stockType, boolean showDisableStock) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id  and " +
                "stockCategory.type = '" + stockType+ "' " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;");
        ObservableList<String> stockCategoryList = FXCollections.observableArrayList();
        stockCategoryList.addAll("Всі категорії", "Весь інвентар");
        while (rs.next()) {
            stockCategoryList.add(rs.getString(1));
        }

        if (showDisableStock) {
            rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
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
                    "where stockCategory.type = '" + stockType + "' and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;");
            while (rs.next()) {
                stockCategoryList.add(rs.getString(1));
            }
        }

        return stockCategoryList;
    }

    public static ObservableList<String> selectStockCategoryNameInRepository
            (String stockType, String repositoryName, boolean showDisableStock) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = '" + repositoryName + "' and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id  and " +
                "stockCategory.type = '" + stockType+ "' " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;");
        ObservableList<String> stockCategoryList = FXCollections.observableArrayList();
        stockCategoryList.addAll("Всі категорії", "Весь інвентар");
        while (rs.next()) {
            stockCategoryList.add(rs.getString(1));
        }

        if (showDisableStock) {
            rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                    "from stockcategory left join " +
                    "   (select stockCategory.id as id, count(*) as count  " +
                    "   from repository, stockCategory, stock left join " +
                    "       (select distinct stocktracking.stock_id " +
                    "       from stocktracking " +
                    "       where stocktracking.returnDate is null or " +
                    "       stocktracking.returnDate > curdate()) usingStock " +
                    "       on stock.id = usingStock.stock_id " +
                    "   where usingStock.stock_id is null and " +
                    "   stock.repository_id = repository.id and " +
                    "   repository.name = '" + repositoryName + "' and " +
                    "   stock.status = 'доступно' and " +
                    "   stock.stockCategory_id = stockCategory.id " +
                    "   group by stockCategory.id) numberOfAvailableStockCategory " +
                    "   on numberOfAvailableStockCategory.id = stockcategory.id " +
                    "where stockCategory.type = '" + stockType + "' and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;");
            while (rs.next()) {
                stockCategoryList.add(rs.getString(1));
            }
        }

        return stockCategoryList;
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
                "stockCategory.type = '" + stockType+ "' " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class));

        if (showDisableStock) {
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
                    "where stockCategory.type = '" + stockType + "' and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class)));
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
                "repository.name = '" + repositoryName + "' and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id  and " +
                "stockCategory.type = '" + stockType+ "' " +
                "group by stockCategory.id " +
                "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class));

        if (showDisableStock) {
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
                    "   repository.name = '" + repositoryName + "' " +
                    "   group by stockCategory.id) numberOfAvailableStockCategory " +
                    "   on numberOfAvailableStockCategory.id = stockcategory.id " +
                    "where stockCategory.type = '" + stockType + "' and " +
                    "numberOfAvailableStockCategory.count is null " +
                    "order by stockCategory.name asc;", BeanPropertyRowMapper.newInstance(DtoStockCategory.class)));
        }
        return dtoStockCategoryList;
    }

    public static List<DtoStock> selectStockOfCategoryWithId (int stockCategoryId) {
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
                "stockCategory.id = '" + stockCategoryId + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static List<DtoStock> selectStockOfCategoryWithIdInRepository (int stockCategoryId, String repositoryName) {
        List<DtoStock> dtoStockList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from repository, stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = '" + repositoryName + "' and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.id = '" + stockCategoryId + "' " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoStock.class));
        return dtoStockList;
    }

    public static String selectStockCategoryNameWithId (int stockCategoryId) {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select stockCategory.name " +
                "from stockCategory " +
                "where stockCategory.id = '" + stockCategoryId + "'");
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
}