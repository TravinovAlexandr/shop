package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;

//CREATE TABLE coments(id BIGSERIAL PRIMARY KEY, product_id BIGINT REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE, 
//header VARCHAR(30) NOT NULL, body  VARCHAR(100) NOT NULL, nick VARCHAR(30) NOT NULL, date TIMESTAMP);
public class Comment implements Serializable {
    
    public Long id;
    public Long productId;
    public String header;
    public String body;
    public String nick;
    public Date date;
    
    public Comment() {}
    
    public Comment(Long id, String header, String body, String nick, Date date) {
        this.id = id;
        this.header = header;
        this.body = body;
        this.nick = nick;
        this.date = date;
    }

}
