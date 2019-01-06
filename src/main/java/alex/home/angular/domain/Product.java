package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Set;

public class Product implements Serializable {
    
    private Long id;
    private Integer quantity;
    private Double price;
    private String name;
    private String desc;
    private String concreteType;
    private Set<String> commonType;
    //manyToMany
    private Set<Order> orders;
    //manyToOne
    private Set<Comment> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getConcreteType() {
        return concreteType;
    }

    public void setConcreteType(String concreteType) {
        this.concreteType = concreteType;
    }

    public Set<String> getCommonType() {
        return commonType;
    }

    public void setCommonType(Set<String> commonType) {
        this.commonType = commonType;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    
    
}
