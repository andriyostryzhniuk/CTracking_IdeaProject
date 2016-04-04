package stock.tracking.dto;

/**
 * Created by Andriy on 04/04/2016.
 */
public class DtoResult {
    private Integer stockId;
    private Integer employeesId;
    private Integer objectId;

    public DtoResult() {
    }

    public DtoResult(Integer stockId, Integer employeesId, Integer objectId) {
        this.stockId = stockId;
        this.employeesId = employeesId;
        this.objectId = objectId;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
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
}
