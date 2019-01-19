package alex.home.angular.sql;

public class PGMeta {
    
    public final static int PG_VARCHAR = 1043;
    public final static int PG_TEXT = 25;
    public final static int PG_REAL = 700;
    public final static int PG_BOOLEAN = 16;
    public final static int PG_BIGINT = 20;
    public final static int PG_TIMESTAMP = 1114;
    public final static int PG_INT = 23;
    public final static int PG_SMALLINT = 21;
    public final static int PG_UUID = 2950;
    
    public final static String PRODUCT_TABLE = "product";
    public final static String COMMENT_TABLE = "comment";
    public final static String CATEGORY_TABLE = "category";
    public final static String IMG_TABLE = "img";
    public final static String CART_TABLE = "cart";
    public final static String ADMIN_TABLE = "admin";
    public final static String CLIENT_TABLE = "client";
    public final static String PRODUCT_COMMENTS_TABLE = "product_comments";
    public final static String CATEGORY_PRODUCTS_TABLE = "category_products";
    public final static String CARTS_PRODUCTS = "carts_products";
    public final static String CLIENT_CARTS = "client_carts";
    
    public final static String PG_INVALID_ATTNAME [] = new String [] { "tableoid", "cmax", "xmax", "cmin", "xmin", "ctid", "id", "img_id" };
    
    public final static String CHAR_COND [] = new String [] { "=", "Like" };
    public final static String NUM_COND [] = new String [] { ">", "<", "=", "<>", ">= And <"};
    public final static String DATE_TIME_COND [] = new String [] { ">", "<", "Between" };
    public final static String BOOLEAN_COND [] = new String [] { "True", "False" };
         
}