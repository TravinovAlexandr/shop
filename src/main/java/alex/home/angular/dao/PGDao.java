package alex.home.angular.dao;

import alex.home.angular.sql.search.PGMetaColumn;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PGDao {
    
    List<PGMetaColumn> selectPGFieldMeta(String tableName);
    
}
