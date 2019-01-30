package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//CREATE TABLE coments(id BIGSERIAL PRIMARY KEY, product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL, 
//body  VARCHAR(100) NOT NULL, nick VARCHAR(30) NOT NULL, date TIMESTAMP NOT NULL);
public class Comment implements Serializable {
    
    public Long id;
    public Long productId;
    public String body;
    public String nick;
    public Date date;
    
    public Comment() {}
    
    public Comment(Long id, String body, String nick, Date date) {
        this.id = id;
        this.body = body;
        this.nick = nick;
        this.date = date;
    }
    
    public Comment(Long id, String body, String nick) {
        this.id = id;
        this.body = body;
        this.nick = nick;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.productId);
        hash = 79 * hash + Objects.hashCode(this.body);
        hash = 79 * hash + Objects.hashCode(this.nick);
        hash = 79 * hash + Objects.hashCode(this.date);
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
        
        return Objects.equals(this.id, other.id) && Objects.equals(this.productId, other.productId) && Objects.equals(this.date, other.date) 
                && Objects.equals(this.nick, other.nick) && Objects.equals(this.body, other.body);
    }

    
}
