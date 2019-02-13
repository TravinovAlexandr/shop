package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//CREATE TABLE product(id BIGSERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL, quant INT, description VARCHAR(200), reccomend BOOL DEFAULT 'f', 
//price REAL, mark SMALLINT, buystat INT, start TIMESTAMP NOT NULL, last TIMESTAMP, 
//exist BOOLEAN, img_id BIGINT REFERENCES img (id) ON DELETE RESTRICT ON UPDATE CASCADE);

//CREATE TABLE category_product(category_id BIGINT REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE, 
//product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY (category_id, product_id));
public class Product implements Serializable {

    public Long id;
    public Long imgId;
    public Integer buyStat;
    public Integer quantity;
    public Integer mark;
    public Float price;
    //O-O
    public String imgUrl;
    public String name;
    public String description;
    public Boolean isExist;
    public Boolean isReccomend;
    public Date startDate;
    public Date lastBuyDate;
    //M-M
    public List<Category> category;
    //M-O
    public List<Comment> comments;
    
    public Product() {}
    
    public Product(Long id, Integer buyStat, Integer quantity, Integer mark, 
            Float price, String name, String description, Boolean isExist, Boolean isReccomend, Date startDate, Date lastBuyDate) {
   
        this.id = id;
        this.buyStat = buyStat;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.description = description;
        this.isExist = isExist;
        this.isReccomend = isReccomend;
        this.startDate = startDate;
        this.lastBuyDate = lastBuyDate;
    }
    
    //With image
    public Product(Long id, Integer buyStat, Integer quantity, Integer mark, 
            Float price, String name, String description, Boolean isExist, Boolean isReccomend, Date startDate, Date lastBuyDate, String imgUrl) {
   
        this.id = id;
        this.buyStat = buyStat;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.description = description;
        this.isExist = isExist;
        this.isReccomend = isReccomend;
        this.startDate = startDate;
        this.lastBuyDate = lastBuyDate;
        this.imgUrl = imgUrl;
    }    
    
    public  Product(Long id, String name, String description , Float price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Integer getBuyStat() {
        return buyStat;
    }

    public void setBuyStat(Integer buyStat) {
        this.buyStat = buyStat;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsExist() {
        return isExist;
    }

    public void setIsExist(Boolean isExist) {
        this.isExist = isExist;
    }

    public Boolean getIsReccomend() {
        return isReccomend;
    }

    public void setIsReccomend(Boolean isReccomend) {
        this.isReccomend = isReccomend;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getLastBuyDate() {
        return lastBuyDate;
    }

    public void setLastBuyDate(Date lastBuyDate) {
        this.lastBuyDate = lastBuyDate;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", imgId=" + imgId + ", buyStat=" + buyStat + ", quantity=" + quantity + ", mark=" + mark + ", price=" + price + ", imgUrl=" + imgUrl + ", name=" + name + ", description=" + description + ", isExist=" + isExist + ", startDate=" + startDate + ", lastBuyDate=" + lastBuyDate + ", category=" + category + ", comments=" + comments + '}';
    }
    
}
