package alex.home.angular.dto;

import alex.home.angular.domain.Category;
import alex.home.angular.domain.Product;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class UpdateProd implements Serializable {
    
    public Long productId;
    public Long imageId;
    public List<Long> oldCategoriesId;
    public List<Long> newCategoriesId;
    public List<Category> allCategories;
    public Product product;
    public MultipartFile image;
    
    public UpdateProd() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public List<Long> getOldCategoriesId() {
        return oldCategoriesId;
    }

    public void setOldCategoriesId(List<Long> oldCategoriesId) {
        this.oldCategoriesId = oldCategoriesId;
    }

    public List<Long> getNewCategoriesId() {
        return newCategoriesId;
    }

    public void setNewCategoriesId(List<Long> newCategoriesId) {
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