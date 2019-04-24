package alex.home.shop.dto;

import alex.home.shop.annotation.NotNullVal;
import alex.home.shop.annotation.NotUndefined;
import alex.home.shop.domain.Product;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class InsertProdDto extends Product implements Serializable {

    public String mainImgUrl;
    @NotUndefined
    public String tag;
    @NotNullVal
    public MultipartFile mainImg;
    public String[] additionImgUrls;
    public MultipartFile[] additionImgs;
    public List<Integer> categoryIds;
    public List<Integer> tagIds;

    public InsertProdDto() {
    }

    public String getMainImgUrl() {
        return mainImgUrl;
    }

    public void setMainImgUrl(String mainImgUrl) {
        this.mainImgUrl = mainImgUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MultipartFile getMainImg() {
        return mainImg;
    }

    public void setMainImg(MultipartFile mainImg) {
        this.mainImg = mainImg;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public String[] getAdditionImgUrls() {
        return additionImgUrls;
    }

    public void setAdditionImgUrls(String[] additionImgUrls) {
        this.additionImgUrls = additionImgUrls;
    }

    public MultipartFile[] getAdditionImgs() {
        return additionImgs;
    }

    public void setAdditionImgs(MultipartFile[] additionImgs) {
        this.additionImgs = additionImgs;
    }
}
