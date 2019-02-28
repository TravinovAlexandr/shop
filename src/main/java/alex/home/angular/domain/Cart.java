package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TYPE status AS ENUM('on','stop','off');

//CREATE TABLE cart(id BIGSERIAL PRIMARY KEY, description VARCHAR(300) NOT NULL, 
//startday TIMASTAMP NOT NULL, endday TIMESTAMP, stts status NOT NULL, client_id BIGINT REFERENCES client(id) ON DELETE RESTRICT ON UPDATE CASCADE);

//CREATE TABLE cart_products(cart_id BIGINT REFERENCES cart (id) ON DELETE CASCADE ON UPDATE CASCADE, 
//product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE);
public class Cart implements Serializable {

    public enum OrderStatus {
        ON, STOP, OFF
    }
    
    public Integer id;
    public String desc;
    public String name;
    public String email;
    public String address;
    public String telephone;
    public String clientWish;
    public Date startDay;
    public Date endDay;
    public OrderStatus status;
    public List<Product> products;

    public Cart(String name, String email, String address, String telephone, String clientWish) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.telephone = telephone;
        this.clientWish = clientWish;
    }
    
    public Cart() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getClientWish() {
        return clientWish;
    }

    public void setClientWish(String clientWish) {
        this.clientWish = clientWish;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
