package alex.home.shop.dto;

import alex.home.shop.domain.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryTree implements Serializable {

    private Category category;
    private final List<CategoryTree> nodes = new ArrayList<>();

    public CategoryTree() {}

    public CategoryTree(Category category) {
        this.category = category;
    }

    //create tree on nodes * nodes (without root branches)
    public static CategoryTree createTree(List<Category> cats) {
        if (cats == null || cats.isEmpty()) {
            throw new IllegalArgumentException("Argument is null or collection is empty.");
        }

        CategoryTree root = new CategoryTree();
        List<CategoryTree> nodes = new ArrayList<>();

        cats.forEach((cat) -> {
            if (cat.pid.equals(0)) {
                root.nodes.add(new CategoryTree(cat));
            } else {
                nodes.add(new CategoryTree(cat));
            }
        });

        int size = nodes.size();
        while (size-- > 0) {
            for (int i = 0; i < nodes.size(); i++) {
                root.add(nodes.get(i));
            }
        }

        return root;
    }

    private void add(CategoryTree comp) {
        if (comp.category.pid.equals(0)) {
            throw new IllegalArgumentException("Terminal nodes and root branches have to be added before method call.");
        }

        if (category != null && comp.category.pid.equals(category.id)) {
            for (CategoryTree ct : nodes) {
                if (ct.category.id.equals(comp.category.id)) {
                    return;
                }
            }
            nodes.add(comp);
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).add(comp);
        }
    }

    public Category getCategory() {
        return category;
    }

    public List<CategoryTree> getNodes() {
        return nodes;
    }
}
