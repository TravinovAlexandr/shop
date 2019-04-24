package alex.home.shop.service;

import alex.home.shop.dao.TagDao;
import alex.home.shop.domain.Tag;
import alex.home.shop.dto.TagCount;
import alex.home.shop.sql.PGMeta;
import alex.home.shop.utils.ValidationUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService implements TagDao {
    
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Object selectTagsLike(String word, Integer limit, Integer offset) {
        if (!ValidationUtil.validateNull(limit, offset, word)) {
            return null;
        }

        try {
            TagCount tc = new TagCount();
            List<Tag> tags = new ArrayList<>();

            namedParameterJdbcTemplate.query("SELECT id, name, (SELECT count(id) AS count FROM " + PGMeta.TAG_TABLE + " WHERE name ILIKE :word) FROM " + PGMeta.TAG_TABLE
                    + " WHERE name ILIKE :word ORDER BY id LIMIT " + limit + " OFFSET " + offset, new MapSqlParameterSource("word", word + "%"), (ResultSet rs, int i) -> {
                        if (i == 0) {
                            tc.count = rs.getInt("count");
                        }
                        tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
                        return null;
                    });
            
            if (tags.isEmpty()) {
                return null;
            }

            tc.tags = tags;
            return tc;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Object selectOrphanedTags(Integer limit, Integer offset) {
        if (!ValidationUtil.validateNull(limit, offset)) {
            return null;
        }

        try {
            TagCount tc = new TagCount();
            List<Tag> tags = new ArrayList<>();
            jdbcTemplate.query("SELECT id, name, (SELECT count(id) FROM " + PGMeta.TAG_TABLE + " WHERE id NOT IN (SELECT tag_id FROM " + PGMeta.PRODUCT_TAGS_TABLE 
                    + ")) AS count FROM " + PGMeta.TAG_TABLE + " WHERE id NOT IN (SELECT tag_id FROM " + PGMeta.PRODUCT_TAGS_TABLE + ")ORDER BY id LIMIT " + limit + " OFFSET " 
                    + offset, (ResultSet rs, int i) -> {
                        if (i == 0) {
                            tc.count = rs.getInt("count");
                        }

                        tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
                        return null;
                    });

            if (tags.isEmpty()) {
                return null;
            }

            tc.tags = tags;
            return tc;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Tag> selectTagsWhereProdId(Integer prodId) {
        if (prodId == null) {
            return null;
        }

        try {
            return jdbcTemplate.query("SELECT id, name FROM" + PGMeta.TAG_TABLE + " WHERE id = " + prodId, (ResultSet rs, int i) -> new Tag(rs.getInt("id"), rs.getString("name")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override 
    @Transactional(propagation = Propagation.REQUIRED)
    public int insertTagGetId(String name) {
        if (name == null) {
            return -1;
        }

        try {
            return jdbcTemplate.query("INSERT INTO " + PGMeta.TAG_TABLE + " (name)VALUES(?) RETURNING id", new Object[] { name }, (ResultSet rs, int i)
                    -> rs.getInt("id")).stream().findFirst().orElse(-1);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override 
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertTag(String name) {
        if (name == null) {
            return;
        }

        try {
            jdbcTemplate.update("INSERT INTO " + PGMeta.TAG_TABLE + " (name)VALUES(?);", name);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateTag(Integer id, String name) {
        if (!ValidationUtil.validateAnyNullEmptyString(id, name)) {
            return;
        }
        
        try {
            jdbcTemplate.update("UPDATE " + PGMeta.TAG_TABLE + " SET name = ? WHERE id =" + id, name);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override 
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeTagWhereId(Integer id) {
        if (id == null) {
            return;
        }

        try {
            jdbcTemplate.update("DELETE FROM " + PGMeta.TAG_TABLE + " WHERE id=" + id);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override 
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOrphanedTags() {
        try {
            jdbcTemplate.update("DELETE FROM " + PGMeta.TAG_TABLE + " WHERE id NOT IN(SELECT tag_id FROM " + PGMeta.PRODUCT_TAGS_TABLE + ");");
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    
}
