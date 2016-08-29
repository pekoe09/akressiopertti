package akressiopertti.service;

import akressiopertti.domain.SearchHit;
import akressiopertti.domain.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private IngredientService ingredientService;
    
    public List<SearchHit> search(String searchTerm, String context) {
        List<SearchHit> searchResults = null;
        if(context.equals("all")) {
            
        } else if(context.equals("recipes")) {
            
        } else if(context.equals("ingredients")) {
            
        } else if(context.equals("beverages0")) {
            
        }       
        return searchResults;
    }
    
}
