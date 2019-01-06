package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Set;

public class Company implements Serializable{
    
    private long id;
    private String name;
    private String desc;
    private String address;
    private Set<Product> products;

    public Company() {}
    
    public Company(long id, String name, String desc, String address) {
        this.id =id;
        this.name = name;
        this.desc = desc;
        this.address = address;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
