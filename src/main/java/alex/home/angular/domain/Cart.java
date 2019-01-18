package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TYPE status AS ENUM('on','stop','off');

//CREATE TABLE cart(id BIGSERIAL PRIMARY KEY, description VARCHAR(300) NOT NULL, 
//startday TIMASTAMP NOT NULL, endday TIMESTAMP, stts status NOT NULL, client_id BIGINT REFERENCES client(id) ON DELETE RESTRICT ON UPDATE CASCADE);

//CREATE TABLE cart_product(cart_id BIGINT REFERENCES cart (id) ON DELETE CASCADE ON UPDATE CASCADE, 
//product_id BIGINT REFERENCESproduct (id) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(cart_id, product_id));
public class Cart implements Serializable {

    public enum OrderStatus {
        ON, STOP, OFF
    }
    
    public Long id;
    public String desc;
    public Date startDay;
    public Date endDay;
    public OrderStatus status;
    //O-M
    public Client client;
    //M-M
    public List<Product> products;

    public Cart() {}
    
    public Cart(Long id) {
        this.id = id;
    }

    public Cart addId(Long id) {
        this.id = id;
        return this;
    }

    public Cart addDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public Cart addStartDay(Date startDay) {
        this.startDay = startDay;
        return this;
    }

    public Cart addEndDay(Date endDay) {
        this.endDay = endDay;
        return this;
    }

    public Cart addStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public Cart addClient(Client client) {
        this.client = client;
        return this;
    }

    public Cart addProducts(List<Product> products) {
        this.products = products;
        return this;
    }

}
