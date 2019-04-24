package alex.home.shop.dto;

import alex.home.shop.domain.Category;
import alex.home.shop.sql.search.SearchElement;
import java.io.Serializable;
import java.util.List;

public class SearchElementsCtegories implements Serializable {
    
    public List<SearchElement> searchElements;
    public List<Category> categories;
    
    public SearchElementsCtegories() {}
    
    public SearchElementsCtegories(List<SearchElement> searchElements, List<Category> categories) {
        this.searchElements = searchElements;
        this.categories = categories;
    }

    public List<SearchElement> getSearchElements() {
        return searchElements;
    }

    public void setSearchElements(List<SearchElement> searchElements) {
        this.searchElements = searchElements;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
}
