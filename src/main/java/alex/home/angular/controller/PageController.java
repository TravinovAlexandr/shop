package alex.home.angular.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
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
