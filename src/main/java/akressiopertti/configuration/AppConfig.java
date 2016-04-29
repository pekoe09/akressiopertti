package akressiopertti.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "")
//@PropertySource({ "classpath:application-${envTarget}.properties" })
public class AppConfig {

}
