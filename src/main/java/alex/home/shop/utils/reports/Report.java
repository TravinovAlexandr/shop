package alex.home.shop.utils.reports;

import java.util.Collection;

public interface Report {

    byte[] getReport(Collection data, String pathToReport);

    Report setFont(String font);

    Report setEncoding(String encoding);

    Report setEmbedded(boolean isEmbedded);

    Report setReportName(String docName);
}
