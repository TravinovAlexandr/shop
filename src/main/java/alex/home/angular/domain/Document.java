package alex.home.angular.domain;

//CREATE TABLE doc (docid serial not null, docname varchar (100), mimetype varchar(100),  pathtoreport varchar (500), clob report);

public class Document {
    
    public Integer id;
    public String documentName;
    public String mimeType;
    public String pathToReport;
    public byte[] report;
    
    public Document() {}
    
    public Document(String documentName, String mimeType, String pathToReport) {
        this.documentName = documentName;
        this.mimeType = mimeType;
        this.pathToReport = pathToReport;
    }
    
    public Document(String documentName, String mimeType, String pathToReport, byte[] report) {
        this.documentName = documentName;
        this.mimeType = mimeType;
        this.pathToReport = pathToReport;
        this.report = report;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPathToReport() {
        return pathToReport;
    }

    public void setPathToReport(String pathToReport) {
        this.pathToReport = pathToReport;
    }

    public byte[] getReport() {
        return report;
    }

    public void setReport(byte[] report) {
        this.report = report;
    }
    
}
