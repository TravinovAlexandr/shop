package alex.home.shop.utils.reports;

import static alex.home.shop.utils.reports.ReportConfig.DEFAULT_ENCODING;
import static alex.home.shop.utils.reports.ReportConfig.DEFAULT_FONT;
import static alex.home.shop.utils.reports.ReportConfig.DEFAULT_NAME;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseStyle;

public class PdfReportConfig implements ReportConfig {

    public String font;
    public String encoding;
    public String docName;
    public boolean isEmbedded;
    private JRStyle jbs = new JRBaseStyle();

    @Override
    public JRStyle getDocumentConfig() {
        jbs.setPdfEncoding(encoding == null ? DEFAULT_ENCODING : encoding);
        jbs.setPdfEmbedded(isEmbedded == false ? false : isEmbedded);
        jbs.setPdfFontName(font == null ? DEFAULT_FONT : font);
        docName = (docName == null) ? DEFAULT_NAME : docName;
        return jbs;
    }

    @Override
    public void setFont(String font) {
        this.font = font;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void setEmbedded(boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
    }

    @Override
    public void setDocumentName(String docName) {
        this.docName = docName;
    }
}
