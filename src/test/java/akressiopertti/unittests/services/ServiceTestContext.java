package akressiopertti.unittests.services;

import akressiopertti.domain.Course;
import akressiopertti.repository.BeerRepository;
import akressiopertti.repository.CourseRepository;
import akressiopertti.repository.DishTypeRepository;
import akressiopertti.repository.FoodStuffRepository;
import akressiopertti.repository.IngredientRepository;
import akressiopertti.repository.MeasureRepository;
import akressiopertti.repository.MeasureTypeRepository;
import akressiopertti.repository.RecipeIngredientRepository;
import akressiopertti.repository.RecipeRepository;
import akressiopertti.repository.ShoppingListRepository;
import akressiopertti.repository.UserRepository;
import akressiopertti.repository.WineRepository;
import akressiopertti.service.CourseService;
import akressiopertti.service.DishTypeService;
import akressiopertti.service.FoodStuffService;
import akressiopertti.service.IngredientService;
import akressiopertti.service.MeasureService;
import akressiopertti.service.MeasureTypeService;
import akressiopertti.service.RecipeService;
import akressiopertti.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.mockito.AdditionalAnswers;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;

@Configuration
public class ServiceTestContext {
    
    @Bean
    public CourseService courseService() {
        return new CourseService();
    }
    
    @Bean
    public DishTypeService dishTypeService() {
        return new DishTypeService();
    }
    
    @Bean
    public FoodStuffService foodStuffService() {
        return new FoodStuffService();
    }
    
    @Bean
    public IngredientService ingredientService() {
        return new IngredientService();
    }
    
    @Bean
    public MeasureService measureService() {
        return new MeasureService();
    }
    
    @Bean 
    public MeasureTypeService measureTypeService() {
        return new MeasureTypeService();
    }
    
    @Bean
    public RecipeService recipeService() {
        return new RecipeService();
    }
    
    @Bean
    public UserService userService() {
        return new UserService();
    }
    
    @Bean 
    public BeerRepository beerRepository() {
        return Mockito.mock(BeerRepository.class);
    }
    
    @Bean
    public CourseRepository courseRepository() {
        CourseRepository mockRepo = Mockito.mock(CourseRepository.class);
        
        List<Course> courses = new ArrayList<>();
        Course c1 = new Course();
        c1.setId(1L);
        c1.setName("Alkuruoka");
        c1.setOrdinality(1);
        courses.add(c1);
        Course c2 = new Course();
        c2.setId(2L);
        c2.setName("Pääruoka");
        c2.setOrdinality(2);
        courses.add(c2);
        Course c3 = new Course();
        c3.setId(3L);
        c3.setName("Jälkiruoka");
        c3.setOrdinality(3);
        courses.add(c3);
        
        when(mockRepo.findAll()).thenReturn(courses);
        when(mockRepo.findOne(1L)).thenReturn(c1);
        when(mockRepo.findOne(2L)).thenReturn(c2);
        when(mockRepo.findOne(3L)).thenReturn(c3);
        when(mockRepo.findOne(4L)).thenReturn(null);
        when(mockRepo.findByName("Alkuruoka")).thenReturn(c1);
        when(mockRepo.findByName("Primi Piatti")).thenReturn(null);
//        when(mockRepo.save((Course)anyObject())).thenReturn((Course)returnsFirstArg());
        when(mockRepo.save(c2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(mockRepo).delete(4L);
        
        return mockRepo;
    }
    
    @Bean
    public DishTypeRepository dishTypeRepository() {
        return Mockito.mock(DishTypeRepository.class);
    }
    
    @Bean
    public FoodStuffRepository foodStuffRepository() {
        return Mockito.mock(FoodStuffRepository.class);
    }
    
    @Bean
    public IngredientRepository ingredientRepository() {
        return Mockito.mock(IngredientRepository.class);
    }
    
    @Bean
    public MeasureRepository measureRepository() {
        return Mockito.mock(MeasureRepository.class);        
    }
    
    @Bean
    public MeasureTypeRepository measureTypeRepository() {
        return Mockito.mock(MeasureTypeRepository.class);
    }
    
    @Bean
    public RecipeIngredientRepository recipeIngredientRepository() {
        return Mockito.mock(RecipeIngredientRepository.class);
    }
    
    @Bean
    public RecipeRepository recipeRepository() {
        return Mockito.mock(RecipeRepository.class);
    }
    
    @Bean
    public ShoppingListRepository shoppingListRepository() {
        return Mockito.mock(ShoppingListRepository.class);
    }
    
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
    
    @Bean
    public WineRepository wineRepository() {
        return Mockito.mock(WineRepository.class);
    }
}
