package alex.home.angular.dao;

import alex.home.angular.domain.Img;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgDao {
    
    Img selectImgById(Integer id);
    
    void updateImg(Integer imgId, String url);
}
