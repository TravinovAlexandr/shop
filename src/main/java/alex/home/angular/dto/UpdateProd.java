package alex.home.angular.dto;

import alex.home.angular.domain.Category;
import alex.home.angular.domain.Product;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class UpdateProd implements Serializable {
    
    public Integer productId;
    public Integer imageId;
    public List<Integer> oldCategoriesId;
    public List<Integer> newCategoriesId;
    public List<Category> allCategories;
    public Product product;
    public MultipartFile image;
    
    public UpdateProd() {}

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public List<Integer> getOldCategoriesId() {
        return oldCategoriesId;
    }

    public void setOldCategoriesId(List<Integer> oldCategoriesId) {
        this.oldCategoriesId = oldCategoriesId;
    }

    public List<Integer> getNewCategoriesId() {
        return newCategoriesId;
    }

    public void setNewCategoriesId(List<Integer> newCategoriesId) {
        this.newCategoriesId = newCategoriesId;
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
