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
    
    public Long id;
    public String desc;
    public String name;
    public String email;
    public String address;
    public String telephone;
    public String clientWish;
    public String cookie;
    public Date startDay;
    public Date endDay;
    public OrderStatus status;
    //M-M
    public List<Product> products;

    public Cart() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
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
