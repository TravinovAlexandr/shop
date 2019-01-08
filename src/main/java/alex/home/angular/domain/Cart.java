package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TYPE status AS ENUM('on','stop','off');

//CREATE TABLE cart(id BIGSERIAL PRIMARY KEY, description VARCHAR(300) NOT NULL, 
//startday TIMASTAMP NOT NULL, endday TIMESTAMP, stts status NOT NULL, client_id BIGINT REFERENCES client ON DELETE RESTRICT ON DELETE CASCADE);

//CREATE TABLE cart_orders(cart_id BIGINT REFERENCES cart ON DELETE CASCADE ON UPDATE RESTRICT, 
//product_id BIGINT REFERENCESproduct ON DELETE CASCADE ON UPDATE RESTRICT, PRIMARY KEY(cart_id, product_id));

public class Cart implements Serializable {

    public enum OrderStatus {
        ON, STOP, OFF
    }
    public long id;
    public String description;
    public Date startDay;
    public Date endDay;
    public OrderStatus status;
    //M-M
    public Client client;
    //M-M
    public List<Product> products;

}
