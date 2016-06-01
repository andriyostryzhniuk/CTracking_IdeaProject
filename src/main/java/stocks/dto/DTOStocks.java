package stocks.dto;

import java.math.BigDecimal;

public class DTOStocks {

    private Integer stockId;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private String type;
    private BigDecimal price;
    private String status;
    private String notes;
    private Integer repositoryId;
    private String repositoryName;

    public DTOStocks() {
    }

    public DTOStocks(Integer stockId, String name, Integer categoryId, String categoryName, String type,
                     BigDecimal price, String status, String notes, Integer repositoryId, String repositoryName) {
        this.stockId = stockId;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.price = price;
        this.status = status;
        this.notes = notes;
        this.repositoryId = repositoryId;
        this.repositoryName = repositoryName;
    }

    public DTOStocks(Integer stockId, String name, Integer categoryId, String categoryName, String type,
                     BigDecimal price, String notes, Integer repositoryId, String repositoryName) {
        this.stockId = stockId;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.price = price;
        this.notes = notes;
        this.repositoryId = repositoryId;
        this.repositoryName = repositoryName;
    }

    public DTOStocks(Integer stockId, String name, Integer categoryId, BigDecimal price, String status,
                     String notes, Integer repositoryId) {
        this.stockId = stockId;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.status = status;
        this.notes = notes;
        this.repositoryId = repositoryId;
    }

    public DTOStocks(Integer stockId, String name, Integer categoryId, BigDecimal price,
                     String notes, Integer repositoryId) {
        this.stockId = stockId;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.notes = notes;
        this.repositoryId = repositoryId;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
