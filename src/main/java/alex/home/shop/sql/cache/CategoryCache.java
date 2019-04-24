package alex.home.shop.sql.cache;

import java.util.List;
import javax.annotation.Nullable;
 
public class CategoryCache<T> {

    private volatile List<T> currentCategories = null;
    private volatile boolean isValid = false;
    
    public CategoryCache() {} 
    
    public void invalidate() {
        isValid = false;
    }
    
   public boolean isValid() {
        return isValid;
    }
    
   @Nullable
   //при добавлении новой категории происходит инвалидация кеша, если будет проброшенно исключение с дао следует присвоить
   //isValid = true, currentCategories = null. потоки в while вернут null. В этой ситуации на контроллере снова запрос категорий.
   //в случае устойчивых неполадок бд возникнет выше описанный цикл. следует завести счетчик,  превышение - либо exit  
   //либо перехват запроса и редирект на error.html
    public List<T> getCategiries() {
        while (!isValid) {}
        
        return currentCategories;
    }
    
    public synchronized void init(List<T> categories) {
        if (isValid == true) {
            return;
        }
        
        isValid = true;
        currentCategories = categories;
    }
     

}
