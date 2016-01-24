/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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
        recipe = recipeRepository.save(recipe);
        
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for(int i = 0; i < ingredientSet.size(); i++){
            JSONObject ingredientDatum = (JSONObject)ingredientSet.get(i);
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredientService.findOne(Long.parseLong((String)ingredientDatum.get("ingredientId"))));
            recipeIngredient.setMeasure(measureService.findOne(Long.parseLong((String)ingredientDatum.get("measureId"))));
            String amountString = (String)ingredientDatum.get("amount");
            if(amountString.contains(",")){
                amountString.replace(',', '.');
                recipeIngredient.setAmountFloat(Float.parseFloat(amountString));
            }  else {
                recipeIngredient.setAmountInteger(Integer.parseInt(amountString));
            }
            recipeIngredient = recipeIngredientRepository.save(recipeIngredient);
            ingredientService.addRecipeToIngredient(recipeIngredient);
            measureService.addRecipeToMeasure(recipeIngredient);
            
            ingredients.add(recipeIngredient);
        }
        recipe.setRecipeIngredients(ingredients);        
        return recipeRepository.save(recipe);
    }
    
    public Recipe remove(Long id){
        Recipe recipe = recipeRepository.findOne(id);
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
}
