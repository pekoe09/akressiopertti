package akressiopertti;

//import malva.profiles.DevProfile;
//import malva.profiles.ProdProfile;
import akressiopertti.configuration.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
//@Import({DevProfile.class, ProdProfile.class})
public class Main {
    
    public static void main(String[] args) throws Exception {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SpringApplication.run(Main.class, args);
    }
}
 
