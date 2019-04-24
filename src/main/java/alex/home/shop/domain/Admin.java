package alex.home.shop.domain;

import java.util.Date;

public class Admin {
    
    public Integer id;
    public String nick;
    public String password;
    public Integer role;
    public Date creationDate;
    
    public Admin() {}
    
    public Admin(String nick, String password, Integer role, Date creationDate) {
        this.nick = nick;
        this.password = password;
        this.role = role;
        this.creationDate = creationDate;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCretionDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
