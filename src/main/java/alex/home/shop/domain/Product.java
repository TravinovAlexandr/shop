package alex.home.shop.domain;

import alex.home.shop.annotation.Default;
import alex.home.shop.annotation.NotNullVal;
import alex.home.shop.annotation.NotUndefined;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

//CREATE TABLE product(id BIGSERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL, quant INT, description VARCHAR(200), reccomend BOOL DEFAULT 'f', 
//price REAL, mark SMALLINT, buystat INT, start TIMESTAMP NOT NULL, last TIMESTAMP, 
//exist BOOLEAN, img_id BIGINT REFERENCES img (id) ON DELETE RESTRICT ON UPDATE CASCADE);

//CREATE TABLE category_product(category_id BIGINT REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE, 
//product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY (category_id, product_id));

public class Product implements Serializable {
    
    public Integer id;
    @Default("0")
    public Integer quantity;
    @Default("0")
    public Integer markCount;
    @Default("0")
    public Short mark;
    @Default("0")
    public Short discount;
    @Default("0")
    public Float price;
    @Default("0")
    public Float prevPrice;
    public Float weight;    
    public Float length;
    public Float width;
    public Float height;
    public Float volume;
    @NotNullVal 
    @NotUndefined
    public String name;
    @NotNullVal 
    @NotUndefined
    public String description;
    @NotNullVal 
    @NotUndefined
    public String title;
    @NotUndefined
    public String brand;
    @NotUndefined
    public String ean;
    @NotUndefined
    public String mpn;
    @Default("true")
    public Boolean isExist;
    @Default("false")
    public Boolean isRecommend;
    @Default
    public Date start;
    public Date last;
    public List<Img> imgs;
    public List<Tag> tags;
    public List<Category> category;
    public List<Comment> comments;
    
    public Product() {}
    
    //type listMinProduct
    public Product(Integer id, Short mark, String title, Img img) {
        this.id = id;
        this.mark = mark;
        this.title = title;
        this.imgs = Arrays.asList(img);
    }

    //type adminSearchTable
    public Product(Integer id, Integer quantity, Short mark, Float price, String name, String title, String ean, String mpn) {
        this.id = id;
        this.quantity = quantity;
        this.mark = mark;
        this.price = price;
        this.name = name;
        this.title = title;
        this.mpn = mpn;
        this.ean = ean;
    }
    
    //type listProduct
    public Product(Integer id, Short mark, Short discount,  Float price, Float prevPrice, Boolean isExist, String title, Img img) {
        this.id = id;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.isExist = isExist;
        this.title = title;
        this.imgs = Arrays.asList(img);
    }
    
    ////type bigProductWithoutPhisic
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Boolean isExist, String name, String description, String brand, String ean, String mpn) {
        this.id = id;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.isExist = isExist;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mpn = mpn;
        this.ean = ean;
    }
    
    //type bigProductWithoutPhisic + imgs
    public Product(Integer id, String name, Short mark, Short discount, Float price, Float prevPrice, Boolean isExist, String description, 
            String brand, String ean, String mpn, Collection<Img> imgs) {
        this.id = id;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.isExist = isExist;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
    }
    
    //type bigProductWithoutPhisic + imgs + tags
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Boolean isExist, String name, String description, 
            String brand, String ean, String mpn, Collection<Img> imgs, Collection<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.isExist = isExist;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
        this.tags =  new ArrayList(tags);
    }
    
    //type bigProductWithoutPhisic + imgs + tags
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Boolean isExist, String name, String description, String brand, String ean, String mpn,
            Collection<Img> imgs, Collection<Tag> tags, Collection<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.isExist = isExist;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
        this.tags =  new ArrayList(tags);
        this.comments =  new ArrayList(comments);
    }
    
    //type bigProductWithPhisic
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Float weight, Float length, Float width, Float height, Float volume, 
            Boolean isExist, String name, String description, String brand, String ean, String mpn) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.volume = volume;
        this.height = height;
        this.mpn = mpn;
        this.ean = ean;
        this.isExist = isExist;
    }
    
    //type bigProductWithPhisic + imgs
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Float weight, Float length,Float width, Float height, Float volume,
            Boolean isExist, String name, String description, String brand, String ean, String mpn, Collection<Img> imgs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.volume = volume;
        this.isExist = isExist;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
    }
    
    //type bigProductWithPhisic + imgs + tags
    public Product(Integer id, Short mark, Short discount, Float price, Float prevPrice, Float weight, Float length, Float width, Float height, Float volume, Boolean isExist, 
            String name, String description, String brand, String ean, String mpn, Collection<Img> imgs, Collection<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.volume = volume;
        this.isExist = isExist;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
        this.tags =  new ArrayList(tags);
    }
    
    //type bigProductWithPhisic + imgs + tags + comments
    public Product(Integer id,  Short mark, Short discount, Float price, Float prevPrice, Float weight, Float length, Float width, Float height, Float volume, Boolean isExist, 
            String name, String description, String brand, String ean, String mpn, Collection<Img> imgs, Collection<Tag> tags, Collection<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.mark = mark;
        this.discount = discount;
        this.price = price;
        this.prevPrice = prevPrice;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.volume = volume;
        this.isExist = isExist;
        this.mpn = mpn;
        this.ean = ean;
        this.imgs = new ArrayList(imgs);
        this.tags =  new ArrayList(tags);
        this.comments = new ArrayList(comments);
    }

    public Integer getId() {
        return id;
    }

    public Product setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Product setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Integer getMarkCount() {
        return markCount;
    }

    public Product setMarkCount(Integer markCount) {
        this.markCount = markCount;
        return this;
    }

    public Short getMark() {
        return mark;
    }

    public Product setMark(Short mark) {
        this.mark = mark;
        return this;
    }

    public Short getDiscount() {
        return discount;
    }

    public Product setDiscount(Short discount) {
        this.discount = discount;
        return this;
    }

    public Float getPrice() {
        return price;
    }

    public Product setPrice(Float price) {
        this.price = price;
        return this;
    }

    public Float getPrevPrice() {
        return prevPrice;
    }

    public Product setPrevPrice(Float prevPrice) {
        this.prevPrice = prevPrice;
        return this;
    }

    public Float getWeight() {
        return weight;
    }

    public Product setWeight(Float weight) {
        this.weight = weight;
        return this;
    }

    public Float getLength() {
        return length;
    }

    public Product setLength(Float length) {
        this.length = length;
        return this;
    }

    public Float getWidth() {
        return width;
    }

    public Product setWidth(Float width) {
        this.width = width;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    public Product setHeight(Float height) {
        this.height = height;
        return this;
    }
    
    public Float getVolume() {
        return volume;
    }

    public Product setVolume(Float volume) {
        this.volume = volume;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Product setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public Product setBrand(String brand) {
        this.brand = brand;
        return this;
    }
    
    public String getEan() {
        return ean;
    }

    public Product setEan(String ean) {
        this.ean = ean;
        return this;
    }
    
    public String getMpn() {
        return mpn;
    }

    public Product setMpn(String mpn) {
        this.mpn = mpn;
        return this;
    }

    public Boolean getIsExist() {
        return isExist;
    }

    public Product setIsExist(Boolean isExist) {
        this.isExist = isExist;
        return this;
    }

    public Boolean getIsRecommend() {
        return isRecommend;
    }

    public Product setIsRecommend(Boolean isRecommend) {
        this.isRecommend = isRecommend;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public Product setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getLast() {
        return last;
    }

    public Product setLast(Date last) {
        this.last = last;
        return this;
    }

    public List<Img> getImgs() {
        return imgs;
    }

    public Product setImgs(List<Img> imgs) {
        this.imgs = imgs;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Product setTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public List<Category> getCategory() {
        return category;
    }

    public Product setCategory(List<Category> category) {
        this.category = category;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Product setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }
    
    
}
