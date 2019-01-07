package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Product implements Serializable {
    
    public long id;
    public int quantity;
    public int mark;
    public double price;
    public List<byte[]> imgs;
    public String name;
    public String desc;
    public Date publicDate;
    //O-M
    public Category category;
    //M-O
    public Set<Comment> comments;
    
}
