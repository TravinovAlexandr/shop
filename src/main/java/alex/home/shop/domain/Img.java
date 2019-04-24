package alex.home.shop.domain;

import java.io.Serializable;
import java.util.Objects;

public class Img implements Serializable {
    
    public Integer id;
    public String url;
    public Boolean isMainImg;
    
    public Img(String url) {
        this.url = url;
    }
    
    public Img(String url, Boolean isMainImg) {
        this.url = url;
        this.isMainImg = isMainImg;
    }
    
    public Img(Integer id, String url, Boolean isMainImg) {
        this.id = id;
        this.url = url;
        this.isMainImg = isMainImg;
    }

    public Integer getId() {
        return id;
    }

    public Img setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Img setUrl(String url) {
        this.url = url;
        return this;
    }

    public Boolean getIsMainImg() {
        return isMainImg;
    }

    public Img setIsMainImg(Boolean isMainImg) {
        this.isMainImg = isMainImg;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.url);
        hash = 97 * hash + Objects.hashCode(this.isMainImg);
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
        final Img other = (Img) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.isMainImg, other.isMainImg)) {
            return false;
        }
        return true;
    }
    
    
}
