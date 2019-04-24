package alex.home.shop.utils.reports;

import java.util.Collection;

public class PdfReportKit implements Report {

    private final ReportConfig dc = new PdfReportConfig();
    private final JasperContentData jcd = new JasperContentData();
    private final JasperReport jr = new PdfReport();

    @Override
    public byte[] getReport(Collection data, String pathToReport) {
        if (data == null || pathToReport == null) {
            return null;
        }

        jcd.inputPath = pathToReport;
        jcd.resultSet = data;
        return jr.createReport(dc, jcd);
    }
    
    @Override
    public PdfReportKit setFont(String font) {
        dc.setFont(font);
        return this;
    }

    @Override
    public PdfReportKit setEncoding(String encoding) {
        dc.setEncoding(encoding);
        return this;
    }

    @Override
    public PdfReportKit setEmbedded(boolean isEmbedded) {
        dc.setEmbedded(isEmbedded);
        return this;
    }

    @Override
    public PdfReportKit setReportName(String docName) {
        dc.setDocumentName(docName);
        return this;
    }

}
