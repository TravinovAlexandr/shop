package alex.home.shop.controller;

import alex.home.shop.dao.CartDao;
import alex.home.shop.dto.ResponseRsWrapper;
import alex.home.shop.exception.AdminException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {
    
    private HttpServletResponse hsr;
    private CartDao cartDao;
    
    @PostMapping("/createCart")
    public ResponseRsWrapper createCart(ResponseRsWrapper rrw) {
        try {
            return rrw.addResponse(cartDao.createCartGetId());
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }

    @Autowired
    public void sethttpServletResponse(HttpServletResponse hsr) {
        this.hsr = hsr;
    }

    @Autowired
    public void setCartDao(CartDao cartDao) {
        this.cartDao = cartDao;
    }
    
    
}
