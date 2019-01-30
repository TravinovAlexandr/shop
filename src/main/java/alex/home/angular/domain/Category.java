package alex.home.angular.domain;

//CREATE TABLE  category(id BIGSERIAL PRIMARY KEY, name VARCHAR(30) NOT NULL, description VARCHAR(100));

import java.util.Objects;


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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Category other = (Category) obj;
        
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name) && Objects.equals(this.description, other.description);
    }
    
    
    
}
