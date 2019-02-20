package alex.home.angular.domain;

import java.io.Serializable;
import java.util.List;

//CREATE TABLE client(id BIGSERIAL PRIMARY KEY, age SMALLINT, name VARCHAR(100), email VARCHAR(255), 
//mobile VARCHAR(20), home VARCHAR(20), info VARCHAR(100), address VARCHAR(100), cookie VARCHAR(100), lastdate TIMESTAMP);
public class Client implements Serializable {
    
    public Long id;
    public String wish;
    public String name;
    public String email;
    public String telephone;
    public String address;

    //M-O
    public List<Cart> orders;
    
    public Client() {}
    
    public Client(Long id, String name, String email, String telephone, String address, String wish) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.wish = wish;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Cart> getOrders() {
        return orders;
    }

    public void setOrders(List<Cart> orders) {
        this.orders = orders;
    }
}
