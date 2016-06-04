package object.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DTOObject {

    private Integer objectsId;
    private String address;
    private LocalDate startDate;
    private LocalDate finishDate;
    private Integer customersId;
    private BigDecimal estimatedCost;
    private String notes;
    private String pattern = "dd.MM.yyyy";
    private String formatStartDate;
    private String formatFinishDate;

    public DTOObject() {
    }

    public DTOObject(Integer objectsId, String address, LocalDate startDate, LocalDate finishDate, Integer customersId, BigDecimal estimatedCost, String notes) {
        this.objectsId = objectsId;
        this.address = address;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.customersId = customersId;
        this.estimatedCost = estimatedCost;
        this.notes = notes;
    }

    public Integer getObjectsId() {
        return objectsId;
    }

    public void setObjectsId(Integer objectsId) {
        this.objectsId = objectsId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getCustomersId() {
        return customersId;
    }

    public void setCustomersId(Integer customersId) {
        this.customersId = customersId;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFormatStartDate() {
        if (formatStartDate == null) {
            initFormatStartDate();
        }
        return formatStartDate;
    }

    public void setFormatStartDate(String formatStartDate) {
        this.formatStartDate = formatStartDate;
    }

    public void initFormatStartDate(){
        formatStartDate = startDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String getFormatFinishDate() {
        if (formatFinishDate == null) {
            initFormatFinishDate();
        }
        return formatFinishDate;
    }

    public void setFormatFinishDate(String formatFinishDate) {
        this.formatFinishDate = formatFinishDate;
    }

    public void initFormatFinishDate(){
        if (finishDate != null) {
            formatFinishDate = finishDate.format(DateTimeFormatter.ofPattern(pattern));
        } else {
            formatFinishDate = "-";
        }
    }

}


