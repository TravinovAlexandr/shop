package alex.home.shop.utils.reports;

import net.sf.jasperreports.engine.JRStyle;

public interface ReportConfig {

    String DEFAULT_FONT = "/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/fonts/LiberationSans-Regular.ttf";

    String DEFAULT_ENCODING = "Cp1251";

    String DEFAULT_NAME = "alexShop";

    JRStyle getDocumentConfig();

    void setFont(String font);

    void setEncoding(String encoding);

    void setEmbedded(boolean isEmbedded);

    void setDocumentName(String name);
}
