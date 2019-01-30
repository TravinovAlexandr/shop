package alex.home.angular.sql;

public class PGMeta {
    
    public static final int PG_VARCHAR = 1043;
    public static final int PG_TEXT = 25;
    public static final int PG_REAL = 700;
    public static final int PG_BOOLEAN = 16;
    public static final int PG_BIGINT = 20;
    public static final int PG_TIMESTAMP = 1114;
    public static final int PG_INT = 23;
    public static final int PG_SMALLINT = 21;
    public static final int PG_UUID = 2950;
    
    public static final String PRODUCT_TABLE = "product";
    public static final String COMMENT_TABLE = "coments";
    public static final String CATEGORY_TABLE = "category";
    public static final String IMG_TABLE = "img";
    public static final String CART_TABLE = "cart";
    public static final String ADMIN_TABLE = "admin";
    public static final String CLIENT_TABLE = "client";
    public static final String PRODUCT_COMMENTS_TABLE = "product_comments";
    public static final String CATEGORY_PRODUCTS_TABLE = "category_products";
    public static final String CARTS_PRODUCTS = "carts_products";
    public static final String CLIENT_CARTS = "client_carts";
    
    public static final String PG_INVALID_ATTNAME [] = new String [] { "tableoid", "cmax", "xmax", "cmin", "xmin", "ctid", "id", "img_id" };
    
    public static final String CHAR_COND [] = new String [] { "=", "Like" };
    public static final String NUM_COND [] = new String [] { ">", "<", "=", "<>", ">=<"};
    public static final String DATE_TIME_COND [] = new String [] { ">", "<", "Between" };
    public static final String BOOLEAN_COND [] = new String [] { "True", "False" };
    
}