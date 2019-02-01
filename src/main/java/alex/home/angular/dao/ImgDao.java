package alex.home.angular.dao;

import alex.home.angular.domain.Img;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgDao {
    
    Img selectImgById(Long id);
    
    void updateImg(Long imgId, String url);
}
