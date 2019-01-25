package alex.home.angular.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropFsLoader implements PropLoader {
    
    @Override
    public Properties load(String path) {
        if (path == null) {
            System.out.println("PropertiesFsLoader: path == null");
            return null;
        }

        try {
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("PropertiesFsLoader: file is not exist  " + path);
                return null;
            } else if (!file.canRead()) {
                System.out.println("PropertiesFsLoader: cant read file " + path);
                return null;
            } else if (file.isDirectory()) {
                System.out.println("PropertiesFsLoader: path to dir not soported " + path);
                return null;
            } 

            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                return props;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
