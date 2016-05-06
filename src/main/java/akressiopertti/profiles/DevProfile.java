package akressiopertti.profiles;

import akressiopertti.domain.Course;
import akressiopertti.domain.DishType;
import akressiopertti.domain.FoodStuff;
import akressiopertti.domain.Ingredient;
import akressiopertti.domain.Measure;
import akressiopertti.domain.MeasureType;
import akressiopertti.domain.User;
import akressiopertti.service.CourseService;
import akressiopertti.service.DishTypeService;
import akressiopertti.service.FoodStuffService;
import akressiopertti.service.IngredientService;
import akressiopertti.service.MeasureService;
import akressiopertti.service.MeasureTypeService;
import akressiopertti.service.UserService;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.PostConstruct;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(value = {"dev", "default"})
public class DevProfile {
    
    @Autowired
    private FoodStuffService foodStuffService;
    @Autowired
    private DishTypeService dishTypeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private MeasureTypeService measureTypeService;
    @Autowired
    private MeasureService measureService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init(){
        
        // add user
        User u1 = new User();
        u1.setName("oletusadmin");
        u1.setIsAdmin(true);
        u1.setUsername("a");
        u1.setPassword("a");
        userService.save(u1);
        
        // add foodstuff
        FoodStuff fs1 = new FoodStuff();
        fs1.setName("Kala");
        fs1.setIsFoodCategory(true);
        fs1 = foodStuffService.save(fs1);
        FoodStuff fs2 = new FoodStuff();
        fs2.setName("Äyriäiset");
        fs2.setIsFoodCategory(true);
        fs2 = foodStuffService.save(fs2);
        
        // add dish types
        DishType dt1 = new DishType();
        dt1.setName("Keitto");
        dt1 = dishTypeService.save(dt1);
        DishType dt2 = new DishType();
        dt2.setName("Pata");
        dt2 = dishTypeService.save(dt2);
        
        // add courses
        Course c1 = new Course();
        c1.setName("Alkuruoka");
        c1.setOrdinality(1);
        c1 = courseService.save(c1);
        Course c2 = new Course();
        c2.setName("Pääruoka");
        c2.setOrdinality(2);
        c2 = courseService.save(c2);
        Course c3 = new Course();
        c3.setName("Jälkiruoka");
        c3.setOrdinality(3);
        c3 = courseService.save(c3);
        
        // add measure types
        MeasureType mt1 = new MeasureType();
        mt1.setName("Tilavuusmitta");
        mt1 = measureTypeService.save(mt1);
        MeasureType mt2 = new MeasureType();
        mt2.setName("Painomitta");
        mt2 = measureTypeService.save(mt2);
        MeasureType mt3 = new MeasureType();
        mt3.setName("Lukumäärä");
        mt3 = measureTypeService.save(mt3);
        
        // add measures
        Measure m1 = new Measure();
        m1.setName("litra");
        m1.setPartitive("litraa");
        m1.setAbbreviation("l");
        m1.setMeasureType(mt1);  
        m1 = measureService.save(m1);
        Measure m2 = new Measure();
        m2.setName("desilitra");
        m2.setPartitive("desilitraa");
        m2.setAbbreviation("dl");
        m2.setMeasureType(mt1);  
        m2 = measureService.save(m2);
        Measure m3 = new Measure();
        m3.setName("teelusikallinen");
        m3.setPartitive("teelusikallista");
        m3.setAbbreviation("tl");
        m3.setMeasureType(mt1);  
        m3 = measureService.save(m3);
        mt1.getMeasures().add(m1);
        mt1.getMeasures().add(m2);
        mt1.getMeasures().add(m3);
        mt1 = measureTypeService.save(mt1);
        
        // add ingredients
        Ingredient i1 = new Ingredient();
        i1.setName("Lohi");
        i1.setPartitive("Lohta");
        i1.setFoodStuff(fs1);
        i1 = ingredientService.save(i1);
        
        Ingredient i2 = new Ingredient();
        i2.setName("Silakka");
        i2.setPartitive("Silakkaa");
        i2.setFoodStuff(fs1);
        i2 = ingredientService.save(i2);
    }        
}
