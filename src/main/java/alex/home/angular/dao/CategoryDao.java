package alex.home.angular.dao;

import alex.home.angular.domain.Category;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
    
/*CREATE OR REPLACE FUNCTION INSERT_CATEGORY(catname VARCHAR, catdesk VARCHAR)
RETURNS INT AS $$
BEGIN
      SELECT id FROM category WHERE name = catname;
      IF NOT FOUND THEN
          INSERT INTO category (name, description) VALUES (catname, catdesk);
              RETURN 1;
      ELSE
          RETURN 0;
      END IF;
END
$$ LANGUAGE plpgsql;*/
    boolean insertCategory(String name, String description);
    
    boolean deleteCategory(String name);
    
 /*CREATE OR REPLACE FUNCTION UPDATE_CATEGORY_NAME(oldname VARCHAR, newname VARCHAR)
RETURNS VOID AS $$
BEGIN
    SELECT  id FROM category WHERE name = catname;
    IF NOT FOUND THEN 
        UPDATE category SET name = newname WHERE name = oldname;
    END IF;
END;
$$
language 'plpgsql';*/
    boolean upadateCategoryName(String oldName , String newName);
    
    boolean updateCategoryDesc(String name ,String desc);
    
    Category selectCategory(String name); 
    
    List<Category> selectAllCategory();
    
}
