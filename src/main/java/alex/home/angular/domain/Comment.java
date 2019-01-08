package alex.home.angular.domain;

import java.io.Serializable;
import java.util.Date;

//CREATE TABLE coments(id BIGSERIAL PRIMARY KEY, product_id BIGINT REFERENCES product ON DELETE CASCADE ON UPDATE RESTRICT, 
//header VARCHAR(30) NOT NULL, body  VARCHAR(100) NOT NULL, nick VARCHAR(30) NOT NULL, date TIMESTAMP);
public class Comment implements Serializable {
    
    public long id;
    public long productId;
    public String header;
    public String body;
    public String nick;
    public Date date;

}
