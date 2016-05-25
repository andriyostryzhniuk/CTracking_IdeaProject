package stock.tracking.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DTOStockTracking {

    private Integer id;
    private Integer stockId;
    private String stockName;
    private String stockCategory;
    private Integer employeesId;
    private Integer objectId;
    private LocalDate givingDate;
    private LocalDate returnDate;
    private String pattern = "dd.MM.yyyy";
    private String formatGivingDate;
    private String formatReturnDate;

    public DTOStockTracking() {
    }

    public DTOStockTracking(Integer id, Integer stockId, String stockName, String stockCategory, Integer employeesId,
                            Integer objectId, LocalDate givingDate, LocalDate returnDate) {
        this.id = id;
        this.stockId = stockId;
        this.stockName = stockName;
        this.stockCategory = stockCategory;
        this.employeesId = employeesId;
        this.objectId = objectId;
        this.givingDate = givingDate;
        this.returnDate = returnDate;
    }

    public DTOStockTracking(Integer id, Integer stockId, Integer employeesId, Integer objectId,
                            LocalDate givingDate, LocalDate returnDate) {
        this.id = id;
        this.stockId = stockId;
        this.employeesId = employeesId;
        this.objectId = objectId;
        this.givingDate = givingDate;
        this.returnDate = returnDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockCategory() {
        return stockCategory;
    }

    public void setStockCategory(String stockCategory) {
        this.stockCategory = stockCategory;
    }

    public Integer getEmployeesId() {
        return employeesId;
    }

    public void setEmployeesId(Integer employeesId) {
        this.employeesId = employeesId;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public LocalDate getGivingDate() {
        return givingDate;
    }

    public void setGivingDate(LocalDate givingDate) {
        this.givingDate = givingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getFormatGivingDate() {
        if (formatGivingDate == null) {
            initFormatGivingDate();
        }
        return formatGivingDate;
    }

    public void setFormatGivingDate(String formatGivingDate) {
        this.formatGivingDate = formatGivingDate;
    }

    public void initFormatGivingDate(){
        formatGivingDate = givingDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String getFormatReturnDate() {
        if (formatReturnDate == null) {
            initFormatReturnDate();
        }
        return formatReturnDate;
    }

    public void setFormatReturnDate(String formatReturnDate) {
        this.formatReturnDate = formatReturnDate;
    }

    public void initFormatReturnDate(){
        if (returnDate != null) {
            formatReturnDate = returnDate.format(DateTimeFormatter.ofPattern(pattern));
        } else {
            formatReturnDate = "-";
        }
    }

}
