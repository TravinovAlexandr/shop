package alex.home.angular.domain;

import java.io.Serializable;

//CREATE TABLE img(id BIGSERIAL PRIMARY KEY, url VARCHAR(200) NOT NULL);
//сделать 1 ко многим
public class Img implements Serializable {
    
    public Integer id;
    public String url;
    
    public Img(Integer id, String url) {
        this.id = id;
        this.url = url;
    }
}
