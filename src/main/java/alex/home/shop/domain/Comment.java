package alex.home.shop.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//CREATE TABLE coments(id SERIAL PRIMARY KEY, product_id INT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL, 
//body  VARCHAR(100) NOT NULL, nick VARCHAR(30) NOT NULL, date TIMESTAMP NOT NULL);
public class Comment implements Serializable {
    
    public Integer id;
    public Integer productId;
    public String body;
    public String nick;
    public Date start;
    
    public Comment() {}
    
    public Comment(Integer id, String body, String nick, Date start) {
        this.id = id;
        this.body = body;
        this.nick = nick;
        this.start = start;
    }
    
    public Comment(Integer id, String body, String nick) {
        this.id = id;
        this.body = body;
        this.nick = nick;
    }
    
    public Comment(String nick, String body, Date start) {
        this.body = body;
        this.nick = nick;
        this.start = start;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.productId);
        hash = 79 * hash + Objects.hashCode(this.body);
        hash = 79 * hash + Objects.hashCode(this.nick);
        hash = 79 * hash + Objects.hashCode(this.start);
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
        final Comment other = (Comment) obj;
        
        return Objects.equals(this.id, other.id) && Objects.equals(this.productId, other.productId) && Objects.equals(this.start, other.start) 
                && Objects.equals(this.nick, other.nick) && Objects.equals(this.body, other.body);
    }


}
