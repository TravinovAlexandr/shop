package alex.home.shop.utils.reports;

import java.util.Collection;

public class JasperContentData {

    public String inputPath;
    public String outputPath;
    public Collection<?> resultSet;

    public JasperContentData() {
    }

    public JasperContentData(String inputPath, Collection<?> resultSet) {
        this.inputPath = inputPath;
        this.resultSet = resultSet;
    }

    public JasperContentData(String inputPath, String outputPath, Collection<?> resultSet) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.resultSet = resultSet;
    }
}