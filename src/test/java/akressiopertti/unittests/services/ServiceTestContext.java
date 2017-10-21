package akressiopertti.unittests.services;

import akressiopertti.repository.BeerRepository;
import akressiopertti.repository.BeerTypeRepository;
import akressiopertti.repository.CourseRepository;
import akressiopertti.repository.DishTypeRepository;
import akressiopertti.repository.FoodStuffRepository;
import akressiopertti.repository.GrapeRepository;
import akressiopertti.repository.IngredientRepository;
import akressiopertti.repository.MeasureRepository;
import akressiopertti.repository.MeasureTypeRepository;
import akressiopertti.repository.RecipeIngredientRepository;
import akressiopertti.repository.RecipeRepository;
import akressiopertti.repository.ShoppingListRepository;
import akressiopertti.repository.UserRepository;
import akressiopertti.repository.WineRepository;
import akressiopertti.repository.WineTypeRepository;
import akressiopertti.service.BeerService;
import akressiopertti.service.BeerTypeService;
import akressiopertti.service.CourseService;
import akressiopertti.service.DishTypeService;
import akressiopertti.service.FoodStuffService;
import akressiopertti.service.GrapeService;
import akressiopertti.service.IngredientService;
import akressiopertti.service.MeasureService;
import akressiopertti.service.MeasureTypeService;
import akressiopertti.service.RecipeService;
import akressiopertti.service.UserService;
import akressiopertti.service.WineService;
import akressiopertti.service.WineTypeService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceTestContext {
    
    @Bean
    public BeerService beerService() {
        return new BeerService();
    }
    
    @Bean
    public BeerTypeService beerTypeService() {
        return new BeerTypeService();
    }
    
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
    public GrapeService grapeService() {
        return new GrapeService();
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
    public WineService wineService() {
        return new WineService();
    }
    
    @Bean
    public WineTypeService wineTypeService() {
        return new WineTypeService();
    }
    
    @Bean 
    public BeerRepository beerRepository() {
        return Mockito.mock(BeerRepository.class);
    }
    
    @Bean 
    public BeerTypeRepository beerTypeRepository() {
        return Mockito.mock(BeerTypeRepository.class);
    }
    
    @Bean
    public CourseRepository courseRepository() {
        return Mockito.mock(CourseRepository.class);
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
    public GrapeRepository grapeRepository() {
        return Mockito.mock(GrapeRepository.class);
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
    
    @Bean
    public WineTypeRepository wineTypeRepository() {
        return Mockito.mock(WineTypeRepository.class);
    }
}
