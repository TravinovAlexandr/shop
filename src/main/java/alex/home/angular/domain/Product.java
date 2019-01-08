package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

//create table product(id bigserial primary key, name varchar(50) not null, quant INT, description varchar(200) not null, 
//price real not null, mark smallint, buystat integer, start timestamp not null, last timestamp);

//create table category_products(category_id bigint references category on delete restrict on  update restrict, 
//product_id bigint references product on delete cascade on update restrict, primary key (category_id, product_id));
public class Product implements Serializable {

    public long id;
    public int buyStat;
    public int quantity;
    public int mark;
    public double price;
    public byte[] imgs;
    public String name;
    public String description;
    public Date startDate;
    public Date lastBuy;
    //M-M
    public Category category;
    //M-O
    public List<Comment> comments;
    
}
