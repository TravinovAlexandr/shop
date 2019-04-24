package alex.home.shop.dto;

import alex.home.shop.domain.Tag;
import java.io.Serializable;
import java.util.List;

public class TagCount implements Serializable {
    
    public List<Tag> tags;
    public Integer count;

    public TagCount() {}
    
    public TagCount(List<Tag> tags, Integer count) {
        this.tags = tags;
        this.count = count;
    }
   
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
}
