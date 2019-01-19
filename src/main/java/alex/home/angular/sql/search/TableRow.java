package alex.home.angular.sql.search;

import java.util.List;

public interface TableRow<T, E> {
    
    List<T> getRows(List<E> flds);
}
