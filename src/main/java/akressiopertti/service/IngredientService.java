package akressiopertti.service;

import akressiopertti.domain.FoodStuff;
import akressiopertti.domain.Ingredient;
import akressiopertti.repository.IngredientRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
