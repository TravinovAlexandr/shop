package alex.home.shop.dao;

import alex.home.shop.sql.search.PGMetaColumn;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PGDao {
    
    List<PGMetaColumn> selectPGFieldMeta(String tableName);
    
}
