package alex.home.angular.dto;

import java.io.Serializable;

public class ProductRow implements Serializable {
    
    public Long id;
    public Integer buyStat;
    public Integer quantity;
    public Integer mark;
    public Integer productsCount;
    public Double price;
    public Boolean isExist;
    public String startDate;
    public String lastBuyDate;
    public String name;
    public String description;
    
    
    public ProductRow() {}
    
    public ProductRow(Long id, Integer buyStat, Integer quantity, Integer mark, Double price, String name, String description, 
            Boolean isExist, String startDate, String lastBuyDate, Integer productsCount) {
        this.id = id;
        this.buyStat = buyStat;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.description = description;
        this.isExist = isExist;
        this.startDate = startDate;
        this.lastBuyDate = lastBuyDate;
        this.productsCount = productsCount;            
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBuyStat() {
        return buyStat;
    }

    public void setBuyStat(Integer buyStat) {
        this.buyStat = buyStat;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Integer getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(Integer productsCount) {
        this.productsCount = productsCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getIsExist() {
        return isExist;
    }

    public void setIsExist(Boolean isExist) {
        this.isExist = isExist;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastBuyDate() {
        return lastBuyDate;
    }

    public void setLastBuyDate(String lastBuyDate) {
        this.lastBuyDate = lastBuyDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
