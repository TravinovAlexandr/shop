package alex.home.angular.domain;

import java.io.Serializable;

public class Picture implements Serializable {
    
    private Long id;
    private byte[] img;
    private Admin user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Admin getUser() {
        return user;
    }

    public void setUser(Admin user) {
        this.user = user;
    }
}
