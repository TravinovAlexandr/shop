package alex.home.angular.dto;

import java.io.Serializable;

public class LimitOffset implements Serializable {

    public String stringId;
    public Integer id;
    public Integer limit;
    public Integer offset;
    
    public LimitOffset() {}
    
    public LimitOffset(Integer id, Integer limit, Integer offset) {
        this.id = id;
        this.limit = limit;
        this.offset = offset;
    }
    
    public LimitOffset(String stringId, Integer limit, Integer offset) {
        this.stringId = stringId;
        this.limit = limit;
        this.offset = offset;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
