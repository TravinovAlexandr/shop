package alex.home.angular.domain;

import java.io.Serializable;

//CREATE TABLE img(id BIGSERIAL PRIMARY KEY, url VARCHAR(200) NOT NULL);
public class Img implements Serializable {
    
    public Long id;
    public String url;
    
    public Img(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
