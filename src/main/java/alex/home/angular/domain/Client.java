package alex.home.angular.domain;

import java.io.Serializable;
import java.util.List;

//CREATE TABLE client(id BIGSERIAL PRIMARY KEY, age SMALLINT, name VARCHAR(100) NOT NULL, email VARCHAR(255) NOT NULL, 
//mobile VARCHAR(20), home VARCHAR(20), info VARCHAR(100) NOT NULL, address VARCHAR(100));
public class Client implements Serializable {
    
    public long id;
    public byte age;
    public String name;
    public String email;
    public String mobilePhone;
    public String homePhone;
    public String informationForCompany;
    public String address;
    //M-O
    public List<Cart> orders;

}
