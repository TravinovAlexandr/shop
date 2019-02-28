package alex.home.angular.controller;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Product;
import alex.home.angular.utils.DateUtil;
import alex.home.angular.utils.email.EmailData;
import alex.home.angular.utils.email.EmailSender;
import alex.home.angular.utils.reports.JasperContentData;
import alex.home.angular.utils.reports.JasperReport;
import alex.home.angular.utils.reports.PdfReportKit;
import alex.home.angular.utils.reports.PdfReportConfig;
import alex.home.angular.utils.reports.PdfReport;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import alex.home.angular.utils.reports.Report;
import alex.home.angular.utils.reports.ReportConfig;

@Controller
public class PageController {
    
    @Autowired
    EmailSender emailSender;
    
    @Autowired
    ProductDao pd;

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }
    
    @GetMapping("/auth")
    public String getAuthPage() {
        return "auth";
    }
    
    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin";
    }
    
    
//    @GetMapping(value = "/ang", produces = MediaType.APPLICATION_PDF_VALUE)
//    @ResponseBody public byte[] getAng() {
//        try {
//        List<Product> l = pd.singleStrArgListProdRet("select id, name,description, price from product where id in (30,34,2,16,15,14,13,11,12);");
//        String a ="/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/jasperreports/client_products.jasper";
//        
//        Report pd = new PdfReportKit();
//        byte[] bytes = pd.getReport(l, a);
//        
//        EmailData ed = new EmailData("travinovalexandr@gmail.com", "contract","your products", "travinovalexandr@gmail.com", 
//                "client_products.pdf","application/pdf" , DateUtil.getCurrentTimestamp(), bytes);
//        
//        emailSender.sendEmailWithAttachments(ed);
//        } catch (RuntimeException ex) {
//            
//        }
//        return bytes;
//    }
}
