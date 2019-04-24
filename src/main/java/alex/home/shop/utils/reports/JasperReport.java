package alex.home.shop.utils.reports;

public interface JasperReport {

    byte[] createReport(ReportConfig config, JasperContentData data);

}
