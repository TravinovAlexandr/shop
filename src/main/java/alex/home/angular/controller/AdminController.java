package alex.home.angular.controller;

import alex.home.angular.dao.AdminDao;
import alex.home.angular.dao.CompanyDao;
import alex.home.angular.dto.InsertCompanyDto;
import alex.home.angular.dto.RegDto;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {
    
    private AdminDao adminDao;
    private CompanyDao companyDao;
    private PasswordEncoder passwordEncoder;
    
    @PostMapping(value = "/insertAdmin", produces = "application/json")
    @ResponseBody public ResponseRsWrapper insertAdmin(@RequestBody RegDto dto, Authentication auth) {
        final ResponseRsWrapper resp = new ResponseRsWrapper();
        if (dto != null && ValidationUtil.isFirstAdmin(auth)
                && ValidationUtil.isCorrectNick(dto.getNick())
                && ValidationUtil.isCorrectPassword(dto.getPassword())) {
            if (adminDao.insertAdmin(dto.getNick(), passwordEncoder.encode(dto.getPassword()), dto.getRole())) {
                return resp.addMessage("Ok").addResponseMessage(HttpStatus.OK);
            } else {
                return resp.addMessage("InternalError").addResponseMessage("INTERNAL_SERVER_ERROR");
            }
        }
        return resp.addMessage("Bad").addResponseMessage("BAD");
    }
    
    @PostMapping(value = "/insertCompany", produces = "application/json")
    @ResponseBody public ResponseRsWrapper insertCompany(@RequestBody InsertCompanyDto dto, Authentication auth) {
        final ResponseRsWrapper rrw = new ResponseRsWrapper();
        if (dto != null && ValidationUtil.isAdminAuthenticated(auth)) {
            companyDao.insertCompany(dto.getName(), dto.getDesc(), dto.getAddress());
            return rrw.addMessage("1");
        } 
        return rrw.addMessage("0");
    }
    
    
    @Autowired
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @Autowired
    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }
        
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
  
}
