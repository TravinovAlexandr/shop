package alex.home.angular.dto;

public class ClientInfoProductsSum {
    
    public Float sumPrice;
    public Float price;
    public String prodName;
    public String prodDesc;
    public String clientName;
    public String clientEmail;
    public String clientAddress;
    public String clientTelephone;
    public String clientWish;

    public ClientInfoProductsSum() {}

    public ClientInfoProductsSum(Float sumPrice, String prodName, String prodDesc, Float price, String clientName, String clientEmail, String clientAddress, String clientTelephone, String clientWish) {
        this.sumPrice = sumPrice;
        this.prodName = prodName;
        this.prodDesc = prodDesc;
        this.price = price;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientAddress = clientAddress;
        this.clientTelephone = clientTelephone;
        this.clientWish = clientWish;
    }

    public Float getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Float sumPrice) {
        this.sumPrice = sumPrice;
    }
    
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }

    public String getClientWish() {
        return clientWish;
    }

    public void setClientWish(String clientWish) {
        this.clientWish = clientWish;
    }
    
}
