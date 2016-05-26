package stock.tracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import stock.tracking.dto.*;
import java.time.LocalDate;
import java.util.List;

import static main.DB_Connector.getJdbcTemplate;


public class ODBC_PubsBDForStock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBDForStock.class);

    public static List<DtoStock> selectAllStockOfType(String stockType, LocalDate dateView, boolean onlyAvailableStock) {
        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stock.id, " +
                            "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                            "from stockCategory, stock " +
                            "where stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id and " +
                            "stockCategory.type = ? " +
                            "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), stockType);
        } else {
            return getJdbcTemplate().query("select stock.id, " +
                            "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                            "from stockCategory, stock left join " +
                            "   (select distinct stocktracking.stock_id " +
                            "   from stocktracking " +
                            "   where stocktracking.givingDate <= ? AND ( " +
                            "   stocktracking.returnDate is null or " +
                            "   stocktracking.returnDate >= ?)) usingStock " +
                            "   on stock.id = usingStock.stock_id " +
                            "where usingStock.stock_id is null and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id and " +
                            "stockCategory.type = ? " +
                            "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, stockType);
        }
    }

    public static List<DtoStock> selectAllStockOfTypeInRepository(
            String stockType, String repositoryName, LocalDate dateView, boolean onlyAvailableStock) {

        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stock.id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                    "from repository, stockCategory, stock " +
                    "where stock.repository_id = repository.id and " +
                    "repository.name = ? and " +
                    "stock.status = 'доступно' and " +
                    "stock.stockCategory_id = stockCategory.id and " +
                    "stockCategory.type = ? " +
                    "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), repositoryName, stockType);
        } else {
            return getJdbcTemplate().query("select stock.id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                    "from repository, stockCategory, stock left join " +
                    "   (select distinct stocktracking.stock_id " +
                    "   from stocktracking " +
                    "   where stocktracking.givingDate <= ? AND ( " +
                    "   stocktracking.returnDate is null or " +
                    "   stocktracking.returnDate >= ?)) usingStock " +
                    "   on stock.id = usingStock.stock_id " +
                    "where usingStock.stock_id is null and " +
                    "stock.repository_id = repository.id and " +
                    "repository.name = ? and " +
                    "stock.status = 'доступно' and " +
                    "stock.stockCategory_id = stockCategory.id and " +
                    "stockCategory.type = ? " +
                    "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, repositoryName, stockType);
        }
    }

    public static List<DtoStock> selectStockOfCategory(
            String stockCategory, String stockType, LocalDate dateView, boolean onlyAvailableStock) {

        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stock.id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                            "from stockCategory, stock " +
                            "where stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id and " +
                            "stockCategory.name = ? " +
                            "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), stockCategory);
        } else {
            return getJdbcTemplate().query("select stock.id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                    "from stockCategory, stock left join " +
                    "   (select distinct stocktracking.stock_id " +
                    "   from stocktracking " +
                    "   where stocktracking.givingDate <= ? AND ( " +
                    "   stocktracking.returnDate is null or " +
                    "   stocktracking.returnDate >= ?)) usingStock " +
                    "   on stock.id = usingStock.stock_id " +
                    "where usingStock.stock_id is null and " +
                    "stock.status = 'доступно' and " +
                    "stock.stockCategory_id = stockCategory.id and " +
                    "stockCategory.name = ? " +
                    "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, stockCategory);
        }
    }

    public static List<DtoStock> selectStockOfCategoryInRepository(
            String stockCategory, String repositoryName, String stockType, LocalDate dateView, boolean onlyAvailableStock) {

        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stock.id, " +
                            "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                            "from repository, stockCategory, stock " +
                            "where stock.repository_id = repository.id and " +
                            "repository.name = ? and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id and " +
                            "stockCategory.name = ? " +
                            "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), repositoryName, stockCategory);
        } else {
            return getJdbcTemplate().query("select stock.id, " +
                    "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                            "from repository, stockCategory, stock left join " +
                            "   (select distinct stocktracking.stock_id " +
                            "   from stocktracking " +
                            "   where stocktracking.givingDate <= ? AND ( " +
                            "   stocktracking.returnDate is null or " +
                            "   stocktracking.returnDate >= ?)) usingStock " +
                            "   on stock.id = usingStock.stock_id " +
                            "where usingStock.stock_id is null and " +
                            "stock.repository_id = repository.id and " +
                            "repository.name = ? and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id and " +
                            "stockCategory.name = ? " +
                            "order by stock.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, repositoryName, stockCategory);
        }
    }

    public static List<DtoStockCategory> selectStockCategory(
            String stockType, boolean onlyAvailableStock, LocalDate dateView) {

        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stockCategory.id as id, " +
                            "stockcategory.name as name, count(*) as numberOfStock " +
                            "from stockCategory, stock " +
                            "where stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id  and " +
                            "stockCategory.type = ? " +
                            "group by stockCategory.id " +
                            "order by stockCategory.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStockCategory.class), stockType);
        } else {
            return getJdbcTemplate().query("select stockCategory.id as id, " +
                            "stockcategory.name as name, count(*) as numberOfStock " +
                            "from stockCategory, stock left join " +
                            "   (select distinct stocktracking.stock_id " +
                            "   from stocktracking " +
                            "   where stocktracking.givingDate <= ? AND ( " +
                            "   stocktracking.returnDate is null or " +
                            "   stocktracking.returnDate >= ?)) usingStock " +
                            "   on stock.id = usingStock.stock_id " +
                            "where usingStock.stock_id is null and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id  and " +
                            "stockCategory.type = ? " +
                            "group by stockCategory.id " +
                            "order by stockCategory.name asc;",
                    BeanPropertyRowMapper.newInstance(DtoStockCategory.class), dateView, dateView, stockType);
        }
    }

    public static List<DtoStockCategory> selectStockCategoryInRepository
            (String stockType, String repositoryName, boolean onlyAvailableStock, LocalDate dateView) {

        if (! onlyAvailableStock && stockType.equals("Вартісні")) {
            return getJdbcTemplate().query("select stockCategory.id as id, " +
                            "stockcategory.name as name, count(*) as numberOfStock " +
                            "from repository, stockCategory, stock " +
                            "where stock.repository_id = repository.id and " +
                            "repository.name = ? and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id  and " +
                            "stockCategory.type = ? " +
                            "group by stockCategory.id " +
                            "order by stockCategory.name asc",
                    BeanPropertyRowMapper.newInstance(DtoStockCategory.class), repositoryName, stockType);
        } else {
            return getJdbcTemplate().query("select stockCategory.id as id, " +
                            "stockcategory.name as name, count(*) as numberOfStock " +
                            "from repository, stockCategory, stock left join " +
                            "   (select distinct stocktracking.stock_id " +
                            "   from stocktracking " +
                            "   where stocktracking.givingDate <= ? AND ( " +
                            "   stocktracking.returnDate is null or " +
                            "   stocktracking.returnDate >= ?)) usingStock " +
                            "   on stock.id = usingStock.stock_id " +
                            "where usingStock.stock_id is null and " +
                            "stock.repository_id = repository.id and " +
                            "repository.name = ? and " +
                            "stock.status = 'доступно' and " +
                            "stock.stockCategory_id = stockCategory.id  and " +
                            "stockCategory.type = ? " +
                            "group by stockCategory.id " +
                            "order by stockCategory.name asc;",
                    BeanPropertyRowMapper.newInstance(DtoStockCategory.class),
                    dateView, dateView, repositoryName, stockType);
        }
    }

    public static List<DtoStock> selectStockOfCategoryWithId (int stockCategoryId, LocalDate dateView) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from stockCategory, stock left join " +
                        "   (select distinct stocktracking.stock_id " +
                        "   from stocktracking " +
                        "   where stocktracking.givingDate >= ? OR ( " +
                        "   stocktracking.givingDate <= ? AND ( " +
                        "   stocktracking.returnDate is null or " +
                        "   stocktracking.returnDate >= ?))) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.id = ? " +
                "order by stock.name asc",
                BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, dateView, stockCategoryId);
    }

    public static List<DtoStock> selectStockOfCategoryWithIdInRepository (
            int stockCategoryId, String repositoryName, LocalDate dateView) {
        return getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory, stock.notes " +
                "from repository, stockCategory, stock left join " +
                        "   (select distinct stocktracking.stock_id " +
                        "   from stocktracking " +
                        "   where stocktracking.givingDate >= ? OR () " +
                        "   stocktracking.givingDate <= ? AND ( " +
                        "   stocktracking.returnDate is null or " +
                        "   stocktracking.returnDate >= ?))) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.repository_id = repository.id and " +
                "repository.name = ? and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id and " +
                "stockCategory.id = ? " +
                "order by stock.name asc",
                BeanPropertyRowMapper.newInstance(DtoStock.class), dateView, dateView, dateView,
                repositoryName, stockCategoryId);
    }

    public static String selectStockCategoryNameWithId (int stockCategoryId) {
        return getJdbcTemplate().queryForObject("select stockCategory.name " +
                        "from stockCategory " +
                        "where stockCategory.id = ?",
                new Object []{stockCategoryId}, String.class);
    }

    public static List<String> selectRepositoryNames() {
        List<String> repositoryNameList = getJdbcTemplate().query("select repository.name " +
                "from repository " +
                "order by repository.name asc", (RowMapper) (resultSet, i) -> resultSet.getString(1));
        repositoryNameList.add(0, "Всі склади");
        return repositoryNameList;
    }

    public static String selectStocksNotes(Integer stockId) {
        return getJdbcTemplate().queryForObject("select notes " +
                        "from stock " +
                        "where id = ?",
                new Object []{stockId}, String.class);
    }

    public static DtoStock selectStockName(Integer stockId) {
        List<DtoStock> dtoStocksList = getJdbcTemplate().query(
                "select ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                        "from stock, stockCategory " +
                        "where stock.id = ? AND " +
                        "stock.stockCategory_id = stockCategory.id ",
                BeanPropertyRowMapper.newInstance(DtoStock.class), stockId);
        return dtoStocksList.get(0);
    }

}