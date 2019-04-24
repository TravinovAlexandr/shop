package alex.home.shop.utils.payments;

import com.yandex.money.api.authorization.AuthorizationParameters;
import com.yandex.money.api.net.clients.ApiClient;
import com.yandex.money.api.net.clients.DefaultApiClient;

public class Yandex {
    
    //To perform request from com.yandex.money.api.methods package you will need to use ApiClient.
    public ApiClient getApiClient() {
        return new DefaultApiClient.Builder().setClientId("your_client_id_here").create();
    }
    
    public void a() {
        AuthorizationParameters ap;
    }
      
}
