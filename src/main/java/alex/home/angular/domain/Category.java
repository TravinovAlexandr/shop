package alex.home.angular.domain;

//CREATE TABLE  category(id BIGSERIAL PRIMARY KEY, name VARCHAR(30) NOT NULL, description VARCHAR(100));

public class Category {
    
    public long id;
    public String name;
    public String description;
    //O-M
    //public List<Product> products;
    
    public Category() {}
    
    public Category(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
