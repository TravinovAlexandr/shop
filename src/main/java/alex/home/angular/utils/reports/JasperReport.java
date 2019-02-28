package alex.home.angular.utils.reports;

public interface JasperReport {

    byte[] createReport(ReportConfig config, JasperContentData data);

}
