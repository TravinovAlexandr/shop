package alex.home.angular.controller;

import alex.home.angular.dao.AdminDao;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {
    
    private AdminDao adminDao;
    private PasswordEncoder passwordEncoder;
    
    @PostMapping(value = "/insertAdmin", produces = "application/json")
    @ResponseBody public ResponseRsWrapper insertAdmin(@RequestBody RegDto dto, Authentication auth) {
        final ResponseRsWrapper resp = new ResponseRsWrapper();
        if (dto != null && ValidationUtil.isFirstAdmin(auth)
                && ValidationUtil.isCorrectNick(dto.getNick())
                && ValidationUtil.isCorrectPassword(dto.getPassword())) {
            if (adminDao.insertAdmin(dto.getNick(), passwordEncoder.encode(dto.getPassword()), dto.getRole())) {
                return resp.addResponse("Ok").addResponseMessage(HttpStatus.OK);
            } else {
                return resp.addResponse("InternalError").addResponseMessage("INTERNAL_SERVER_ERROR");
            }
        }
        return resp.addResponse("Bat").addResponseMessage("BAD");
    }
    
    @ResponseBody public ResponseRsWrapper deleteAdmin(@RequestParam("nick") String nick, Authentication auth) {
        if (nick != null && ValidationUtil.isFirstAdmin(auth)) {
        }
        return null;
    }
    
    @Autowired
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
        
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
  
}
