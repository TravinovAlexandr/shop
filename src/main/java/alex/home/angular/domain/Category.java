package alex.home.angular.domain;

import java.util.Set;

public class Category {
    
    public long id;
    public String name;
    public Set<Category> childCategories;
    //O-M
    public Set<Product> products;
}
