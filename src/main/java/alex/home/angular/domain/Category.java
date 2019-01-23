package alex.home.angular.domain;

//CREATE TABLE  category(id BIGSERIAL PRIMARY KEY, name VARCHAR(30) NOT NULL, description VARCHAR(100));

public class Category {
    
    public Long id;
    public String name;
    public String description;
    
    public Category() {}
    
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
}
