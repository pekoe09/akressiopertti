package akressiopertti.profiles;

import akressiopertti.domain.User;
import akressiopertti.service.UserService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("production")
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
            u1.setUsername("b");
            u1.setPassword("b");
            userService.save(u1);
        }
    }
}
