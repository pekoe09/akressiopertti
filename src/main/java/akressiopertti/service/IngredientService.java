package akressiopertti.service;

import akressiopertti.domain.Course;
import akressiopertti.domain.FoodStuff;
import akressiopertti.domain.Ingredient;
import akressiopertti.domain.RecipeIngredient;
import akressiopertti.repository.IngredientRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class IngredientService {
    
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private FoodStuffService foodStuffService;
    
    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }
    
    public Ingredient findOne(Long id){
        return ingredientRepository.findOne(id);
    }
    
    public Ingredient save(Ingredient ingredient){
        return ingredientRepository.saveAndFlush(ingredient);
    }
    
    public Ingredient remove(Long id) {
        Ingredient ingredient = ingredientRepository.findOne(id);
        ingredientRepository.delete(id);
        return ingredient;
    }

    public Map<String, List> getOptions() {
        List<FoodStuff> foodStuffs = foodStuffService.findAll();
        Map<String, List> options = new HashMap<>();
        options.put("FoodStuffs", foodStuffs);
        return options;
    }

    public String getIngredientsArray() {
        JSONArray responseArray = new JSONArray();
        JSONObject ingredientObject;
        
        List<Ingredient> ingredients = ingredientRepository.findAll();
        for(Ingredient ingredient : ingredients){
            ingredientObject = new JSONObject();
            ingredientObject.put("name", ingredient.getName());
            ingredientObject.put("id", ingredient.getId());
            responseArray.add(ingredientObject);
        }
        
        return responseArray.toJSONString();
    }

    public void addRecipeToIngredient(RecipeIngredient recipeIngredient) {
        Ingredient ingredient = recipeIngredient.getIngredient();
        if(ingredient != null){
            ingredient.getRecipeIngredients().add(recipeIngredient);
            ingredientRepository.save(ingredient);
        }
    }

    public void removeRecipeIngredientFromIngredient(RecipeIngredient recipeIngredient) {
        Ingredient ingredient = recipeIngredient.getIngredient();
        if(ingredient != null){
            ingredient.getRecipeIngredients().remove(recipeIngredient);
            ingredientRepository.save(ingredient);
        }
    }
    
    public List<ObjectError> checkUniqueness(Ingredient ingredient) {
        List<ObjectError> errors = new ArrayList<>();
        Ingredient anotherIngredient = ingredientRepository.findByName(ingredient.getName());
        if(anotherIngredient != null && (ingredient.getId() == null || !anotherIngredient.getId().equals(ingredient.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        anotherIngredient = ingredientRepository.findByPartitive(ingredient.getPartitive());
        if(anotherIngredient != null && (ingredient.getId() == null || !anotherIngredient.getId().equals(ingredient.getId()))){
            errors.add(new ObjectError("partitive", "Nimen partitiivi on jo varattu"));
        }
        return errors;
    }

}
