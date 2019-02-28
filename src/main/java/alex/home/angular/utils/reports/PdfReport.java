package alex.home.angular.utils.reports;

import alex.home.angular.exception.ReportException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PdfReport implements JasperReport {

    @Override
    public byte[] createReport(ReportConfig config, JasperContentData data) {
        if (config == null || data == null) {
            throw new ReportException("IllegalArgumentException");
        }
        
        try {
            JasperPrint print = JasperFillManager.fillReport(data.inputPath, null, new JRBeanCollectionDataSource(data.resultSet));
            print.setDefaultStyle(config.getDocumentConfig());
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException ex) {
            ex.printStackTrace();
            throw new ReportException(ex);
        }
    }
}

