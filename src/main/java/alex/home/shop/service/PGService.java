package alex.home.shop.service;

import alex.home.shop.dao.PGDao;
import alex.home.shop.sql.search.PGMetaColumn;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PGService implements PGDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override @Nullable
    public List<PGMetaColumn> selectPGFieldMeta(@Nullable String tableName) {
        if (tableName != null) {
            try {
                String sql = "SELECT attname, atttypid FROM pg_attribute WHERE attrelid = '"+ tableName  + "'::regclass;";
                return jdbcTemplate.query(sql, (ResultSet rs, int i) ->  new PGMetaColumn(rs.getString("attname"), rs.getInt("atttypid")))
                        .stream().filter(el -> !el.attname.contains("pg.dropped")).collect(Collectors.toList());
            } catch (DataAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
