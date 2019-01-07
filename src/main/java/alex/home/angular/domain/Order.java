package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Order implements Serializable {
    
    public enum OrderStatus {
        ON, STOPPED, OFF
    }
    
    public long id;
    public Date startDay;
    public Date endDay;
    public OrderStatus status;
    //O-O
    public Client client;
    //M-M
    public Set<Product> products;
    
}
