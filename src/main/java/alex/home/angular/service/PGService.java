package alex.home.angular.service;

import alex.home.angular.dao.PGDao;
import alex.home.angular.sql.search.PGField;
import java.sql.ResultSet;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PGService implements PGDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override @Nullable
    public List<PGField> selectPGFieldMeta(@Nullable String tableName) {
        if (tableName != null) {
            try {
                String sql = "SELECT attname, atttypid FROM pg_attribute WHERE attrelid = '"+ tableName  + "'::regclass;";
                return jdbcTemplate.query(sql, (ResultSet rs, int i) ->  new PGField(rs.getString("attname"), rs.getInt("atttypid")));
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
