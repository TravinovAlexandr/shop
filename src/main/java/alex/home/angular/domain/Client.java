package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TABLE client(id BIGSERIAL PRIMARY KEY, age SMALLINT, name VARCHAR(100), email VARCHAR(255), 
//mobile VARCHAR(20), home VARCHAR(20), info VARCHAR(100), address VARCHAR(100), cookie VARCHAR(100), lastdate TIMESTAMP);
public class Client implements Serializable {
    
    public Long id;
    public Byte age;
    public String name;
    public String email;
    public String mobilePhone;
    public String homePhone;
    public String infoCompany;
    public String address;
    public String cookie;
    public Date lastData;
    //M-O
    public List<Cart> orders;
    
    public Client() {}
    
        public Client(Long id, Byte age, String name, String email, String mobilePhone, 
            String homePhone, String infoCompany, String address, String cookie, Date lastData) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.homePhone = homePhone;
        this.infoCompany = infoCompany;
        this.address = address;
        this.cookie = cookie;
        this.lastData = lastData;
    }

    public Client addId(Long id) {
        this.id = id;
        return this;
    }

    public Client addAge(Byte age) {
        this.age = age;
        return this;
    }

    public Client addName(String name) {
        this.name = name;
        return this;
    }

    public Client addEmail(String email) {
        this.email = email;
        return this;
    }

    public Client addMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
        return this;
    }

    public Client addHomePhone(String homePhone) {
        this.homePhone = homePhone;
        return this;
    }

    public Client addInfoCompany(String infoCompany) {
        this.infoCompany = infoCompany;
        return this;
    }

    public Client addAddress(String address) {
        this.address = address;
        return this;
    }

    public Client addCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public Client addLastData(Date lastData) {
        this.lastData = lastData;
        return this;
    }

    public Client addOrders(List<Cart> orders) {
        this.orders = orders;
        return this;
    }
    

}
