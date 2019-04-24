package alex.home.shop.domain;

import java.util.Objects;

public class Category {
    
    public Integer id;
    public Integer pid;
    public String name;
    public String description;
    public Boolean isLeaf;
    
    public Category() {}
       
    public Category(Integer id, Integer pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }
    
    public Category(Integer id, Integer pid, String name, String description) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.description = description;
    }
    
    public Category(Integer id, Integer pid, String name, String description, Boolean isLeaf) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.isLeaf = isLeaf;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.pid);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.description);
        hash = 97 * hash + Objects.hashCode(this.isLeaf);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.pid, other.pid)) {
            return false;
        }
        if (!Objects.equals(this.isLeaf, other.isLeaf)) {
            return false;
        }
        return true;
    }
    
}
