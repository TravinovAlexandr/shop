package alex.home.angular.dao;

import alex.home.angular.sql.search.PGField;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PGDao {
    
    List<PGField> selectPGFieldMeta(String tableName);
    
}
