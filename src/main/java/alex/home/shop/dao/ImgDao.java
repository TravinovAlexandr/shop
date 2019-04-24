package alex.home.shop.dao;

import alex.home.shop.domain.Img;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgDao {
    
//    Img selectImgById(Integer id);
    
    void updateImg(Integer imgId, String url);
}
