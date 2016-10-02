package akressiopertti.service;

import akressiopertti.domain.Course;
import akressiopertti.domain.DishType;
import akressiopertti.domain.FoodStuff;
import akressiopertti.domain.Measure;
import akressiopertti.domain.Recipe;
import akressiopertti.domain.RecipeIngredient;
import akressiopertti.repository.RecipeIngredientRepository;
import akressiopertti.repository.RecipeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
@Transactional
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private FoodStuffService foodStuffService;
    @Autowired
    private DishTypeService dishTypeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private MeasureService measureService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    
    public List<Recipe> findAll(){
        return recipeRepository.findAll();
    }
    
    public Recipe findOne(Long id){
        return recipeRepository.findOne(id);
    }
    
    public Recipe save(Recipe recipe, JSONArray ingredientSet){
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        List<RecipeIngredient> ingredients = new ArrayList<>();
        
        // adds/updates ingredients (depending on whether RecipeIngredient id exists)        
        List<Long> ingredientIds = new ArrayList<>();
        for(int i = 0; i < ingredientSet.size(); i++){
            JSONObject ingredientDatum = (JSONObject)ingredientSet.get(i); 
            
            // if an id for recipe ingredient is provided, retrieves it for updating
            RecipeIngredient recipeIngredient = null;
            String recipeIngredientIdStr = (String)ingredientDatum.get("recipeIngredientId");
            if(recipeIngredientIdStr != null && recipeIngredientIdStr.trim().length() > 0){
                Long recipeIngredientId = Long.parseLong(recipeIngredientIdStr);
                recipeIngredient = recipeIngredientRepository.findOne(recipeIngredientId);
            } else {
                recipeIngredient = new RecipeIngredient();
            }

            recipeIngredient.setRecipe(savedRecipe);
            recipeIngredient.setIngredient(ingredientService.findOne(Long.parseLong((String)ingredientDatum.get("ingredientId"))));
            recipeIngredient.setMeasure(measureService.findOne(Long.parseLong((String)ingredientDatum.get("measureId"))));
            String amountString = (String)ingredientDatum.get("amount");
            if(amountString.contains(",")){
                amountString = amountString.replace(',', '.');
                recipeIngredient.setAmountFloat(Float.parseFloat(amountString));
            } else if(amountString.contains(".")){
                recipeIngredient.setAmountFloat(Float.parseFloat(amountString));
            } else {
                recipeIngredient.setAmountInteger(Integer.parseInt(amountString));
            }
            recipeIngredient = recipeIngredientRepository.save(recipeIngredient);
            ingredientService.addRecipeToIngredient(recipeIngredient);
            measureService.addRecipeToMeasure(recipeIngredient);
            
            ingredients.add(recipeIngredient);
            ingredientIds.add(recipeIngredient.getId());
        }
        savedRecipe.setRecipeIngredients(ingredients);  
                
        // deletes ingredients which have beeen removed
        List<RecipeIngredient> oldIngredients = recipeIngredientRepository.findByRecipe(savedRecipe);
        List<RecipeIngredient> removedIngredients = new ArrayList<>();
        for(int i = 0; i < oldIngredients.size(); i++){
            if(!ingredientIds.contains(oldIngredients.get(i).getId())){
                removedIngredients.add(oldIngredients.get(i));
            }
        }        
        savedRecipe = recipeRepository.save(savedRecipe);
        for(RecipeIngredient ri : removedIngredients){
            measureService.removeRecipeIngredientFromMeasure(ri);
            ingredientService.removeRecipeIngredientFromIngredient(ri);                   
            recipeIngredientRepository.delete(ri.getId());
        }
        
        return savedRecipe;
    }
    
    public Recipe remove(Long id){
        Recipe recipe = recipeRepository.findOne(id);
        if(recipe == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        recipeRepository.delete(id);
        return recipe;
    }
    
    public Map<String, List> getOptions(){
        Map<String, List> options = new HashMap<>();
        List<FoodStuff> foodStuffs = foodStuffService.findAll();
        options.put("FoodStuffs", foodStuffs);
        List<DishType> dishTypes = dishTypeService.findAll();
        options.put("DishTypes", dishTypes);
        List<Course> courses = courseService.findAll();
        options.put("Courses", courses);
        List<Measure> measures = measureService.findAll();
        options.put("Measures", measures);
        return options;
    }

    public Iterable<ObjectError> checkUniqueness(Recipe recipe) {
        List<ObjectError> errors = new ArrayList<>();
        Recipe anotherRecipe= recipeRepository.findByTitle(recipe.getTitle());
        if(anotherRecipe != null && (recipe.getId() == null || !anotherRecipe.getId().equals(recipe.getId()))){
            errors.add(new ObjectError("title", "Nimi on jo varattu"));
        }
        return errors;
    }
}
