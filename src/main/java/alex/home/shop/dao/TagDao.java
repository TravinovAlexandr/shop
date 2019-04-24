package alex.home.shop.dao;

import alex.home.shop.domain.Tag;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDao {

    Object selectTagsLike(String subQuery, Integer limit, Integer offset);
    
    Object selectOrphanedTags(Integer limit, Integer offset);
    
    List<Tag> selectTagsWhereProdId(Integer prodId);
    
    int insertTagGetId(String tagName);
    
    void insertTag(String name);
    
    void updateTag(Integer id, String name);
    
    void removeTagWhereId(Integer id);
    
    void removeOrphanedTags();
}
