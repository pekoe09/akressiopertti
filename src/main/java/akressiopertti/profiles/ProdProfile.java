package akressiopertti.profiles;

import akressiopertti.domain.User;
import akressiopertti.service.UserService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProdProfile {
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init(){
        
        // add default user if no users exist yet
        List<User> users = userService.findAll();
        if(users == null || users.size() == 0){
            User u1 = new User();
            u1.setName("oletusadmin");
            u1.setIsAdmin(true);
            u1.setUsername("a");
            u1.setPassword("a");
            userService.save(u1);
        }
    }
}
