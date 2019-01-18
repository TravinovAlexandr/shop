package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TABLE product(id BIGSERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL, quant INT, description VARCHAR(200), 
//price REAL, mark SMALLINT, buystat INT, start TIMESTAMP NOT NULL, last TIMESTAMP, 
//exist BOOLEAN, img_id BIGINT REFERENCES img (id) ON DELETE RESTRICT ON UPDATE CASCADE);

//CREATE TABLE category_product(category_id BIGINT REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE, 
//product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY (category_id, product_id));
public class Product implements Serializable {

    public Long id;
    public Integer buyStat;
    public Integer quantity;
    public Integer mark;
    public Double price;
    //O-O
    public String imgeUrl;
    public String name;
    public String description;
    public Boolean isExist;
    public Date startDate;
    public Date lastBuyDate;
    //M-M
    public List<Category> category;
    //M-O
    public List<Comment> comments;
    
    public Product() {}
    
    public Product(Long id, Integer buyStat, Integer quantity, Integer mark, 
            Double price, String name, String description, Boolean isExist, Date startDate, Date lastBuyDate) {
   
        this.id = id;
        this.buyStat = buyStat;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.description = description;
        this.isExist = isExist;
        this.startDate = startDate;
        this.lastBuyDate = lastBuyDate;
    }
    
    //With image
    public Product(Long id, Integer buyStat, Integer quantity, Integer mark, 
            Double price, String name, String description, Boolean isExist, Date startDate, Date lastBuyDate, String imgeUrl) {
   
        this.id = id;
        this.buyStat = buyStat;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.description = description;
        this.isExist = isExist;
        this.startDate = startDate;
        this.lastBuyDate = lastBuyDate;
        this.imgeUrl = imgeUrl;
    }    
    
    public  Product(Long id, String name, String description , Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
