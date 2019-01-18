package alex.home.angular.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class ConnectionUtil {
    
    public static boolean closeConnection(@Nullable Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
