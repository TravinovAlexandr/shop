package alex.home.shop.domain;

import java.io.Serializable;
import java.util.Date;

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
    public Integer deliveryId;
    public Short productsQuantity;
    public Float volume;
    public Float weight;
    public String uid;
    public String description;
    public String clientName;
    public String clientEmail;
    public String clientAddress;
    public String clientTelephone;
    public String clientWish;
    public Date deliveryDate;
    public Date start;
    public Date end;
    public Date lastAccess;
    public OrderStatus status;
    
    public Cart() {}
    
    //type initCart
    public Cart(String clientName, String clientEmail, String clientAddress, String clientTelephone, String clientWish, Integer deliveryId) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientAddress = clientAddress;
        this.clientTelephone = clientTelephone;
        this.clientWish = clientWish;
        this.deliveryId = deliveryId;
    }
    
    //type tableCart
    public Cart(Integer id, Short productsQuantity, Float volume, Float weight, Date start, Date deliveryDate, Date lastAccess, String clientEmail, 
            String clientTelephone, String uid) {
        this.id = id;
        this.productsQuantity = productsQuantity;
        this.volume = volume;
        this.weight = weight;
        this.start = start;
        this.deliveryDate = deliveryDate;
        this.lastAccess = lastAccess;
        this.clientEmail = clientEmail;
        this.clientTelephone = clientTelephone;
        this.uid = uid;
    }
    
    //type clientCart
    public Cart(Short productsQuatity, Float volume, Float weight, String uid, String clientName, String clientEmail, String clientAddress, String clientTelephone, 
            String clientWish, Date start, Date deliveryDate, OrderStatus status) {
        this.productsQuantity = productsQuatity;
        this.uid = uid;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientAddress = clientAddress;
        this.clientTelephone = clientTelephone;
        this.clientWish = clientWish;
        this.volume = volume;
        this.weight = weight;
        this.start = start;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }
    
    //type adminCart
    public Cart(Integer id, Integer deliveryId, Short productsQuatity, Float volume, Float weight, String uid, String description, String clientName, String clientEmail, 
            String clientAddress, String clientTelephone, String clientWish, Date start, Date end, Date lastAccess, Date deliveryDate, OrderStatus status) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.productsQuantity = productsQuatity;
        this.uid = uid;
        this.description = description;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientAddress = clientAddress;
        this.clientTelephone = clientTelephone;
        this.clientWish = clientWish;
        this.volume = volume;
        this.weight = weight;
        this.start = start;
        this.end = end;
        this.lastAccess = lastAccess;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Cart setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public Cart setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
        return this;
    }

    public Short getProductsQuantity() {
        return productsQuantity;
    }

    public Cart setProductsQuantity(Short productsQuantity) {
        this.productsQuantity = productsQuantity;
        return this;
    }

    public Float getVolume() {
        return volume;
    }

    public Cart setVolume(Float volume) {
        this.volume = volume;
        return this;
    }

    public Float getWeight() {
        return weight;
    }

    public Cart setWeight(Float weight) {
        this.weight = weight;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public Cart setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Cart setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getClientName() {
        return clientName;
    }

    public Cart setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public Cart setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
        return this;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public Cart setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
        return this;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public Cart setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
        return this;
    }

    public String getClientWish() {
        return clientWish;
    }

    public Cart setClientWish(String clientWish) {
        this.clientWish = clientWish;
        return this;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Cart setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public Cart setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getEnd() {
        return end;
    }

    public Cart setEnd(Date end) {
        this.end = end;
        return this;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public Cart setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Cart setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }
}
