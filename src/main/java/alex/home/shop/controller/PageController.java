package alex.home.shop.controller;

import alex.home.shop.dao.ProductDao;
import alex.home.shop.domain.Product;
import alex.home.shop.utils.DateUtil;
import alex.home.shop.utils.email.EmailData;
import alex.home.shop.utils.email.EmailSender;
import alex.home.shop.utils.reports.JasperContentData;
import alex.home.shop.utils.reports.JasperReport;
import alex.home.shop.utils.reports.PdfReportKit;
import alex.home.shop.utils.reports.PdfReportConfig;
import alex.home.shop.utils.reports.PdfReport;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import alex.home.shop.utils.reports.Report;
import alex.home.shop.utils.reports.ReportConfig;

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
    
}
