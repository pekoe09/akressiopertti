package akressiopertti.unittests.controllers;

import akressiopertti.controller.CourseController;
import akressiopertti.controller.DefaultController;
import akressiopertti.controller.DishTypeController;
import akressiopertti.controller.FoodStuffController;
import akressiopertti.controller.IngredientController;
import akressiopertti.controller.MeasureController;
import akressiopertti.controller.MeasureTypeController;
import akressiopertti.controller.RecipeController;
import akressiopertti.domain.Course;
import akressiopertti.domain.User;
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
import org.mockito.Matchers;
import static org.mockito.Matchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;

@Configuration
public class TestContext {
    
    @Bean
    public CourseController courseController() {
        return new CourseController();
    }
    
    @Bean
    public DefaultController defaultController() {
        return new DefaultController();
    }
    
    @Bean
    public DishTypeController dishTypeController() {
        return new DishTypeController();
    }
    
    @Bean
    public FoodStuffController foodStuffController() {
        return new FoodStuffController();
    }
    
    @Bean
    public IngredientController ingredientController() {
        return new IngredientController();
    }
    
    @Bean
    public MeasureController measureController() {
        return new MeasureController();
    }
    
    @Bean
    public MeasureTypeController measureTypeController() {
        return new MeasureTypeController();
    }
    
    @Bean
    public RecipeController recipeController() {
        return new RecipeController();
    }
    
    @Bean 
    public CourseService courseService() {
        return Mockito.mock(CourseService.class);
    }
    
    @Bean
    public DishTypeService dishTypeService() {
        return Mockito.mock(DishTypeService.class);
    }
    
    @Bean
    public FoodStuffService foodStuffService() {
        return Mockito.mock(FoodStuffService.class);
    }
    
    @Bean
    public IngredientService ingredientService() {
        return Mockito.mock(IngredientService.class);
    }
    
    @Bean
    public MeasureService measureService() {
        return Mockito.mock(MeasureService.class);
    }
    
    @Bean
    public MeasureTypeService measureTypeService() {
        return Mockito.mock(MeasureTypeService.class);
    }
    
    @Bean
    public RecipeService recipeService() {
        return Mockito.mock(RecipeService.class);
    }
    
    @Bean
    public UserService userService() {
        UserService serviceMock = Mockito.mock(UserService.class);
        User u = new User();
        u.setId(1L);
        u.setIsAdmin(true);
        u.setName("a");
        u.setPassword("a");
        u.setUsername("a");
        when(serviceMock.findByUsername(anyString())).thenReturn(u);
        when(serviceMock.getAuthenticatedUser()).thenReturn(u);
        return serviceMock;
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
        UserRepository repoMock = Mockito.mock(UserRepository.class);
        User u = new User();
        u.setId(1L);
        u.setIsAdmin(true);
        u.setName("a");
        u.setPassword("a");
        u.setUsername("a");
        when(repoMock.findByUsername("a")).thenReturn(u);
        return repoMock;
    }
    
    @Bean
    public WineRepository wineRepository() {
        return Mockito.mock(WineRepository.class);
    }
}
