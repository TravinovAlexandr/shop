package alex.home.angular.sql.search;

import java.util.List;

public interface SearchCondition<T, E> {
    
    List<T> getCondition(List<E> flds);
}
