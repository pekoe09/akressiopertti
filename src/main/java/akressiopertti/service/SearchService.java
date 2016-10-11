package akressiopertti.service;

import akressiopertti.domain.SearchHit;
import akressiopertti.domain.User;
import akressiopertti.repository.SearchRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private SearchRepository searchRepository;
    
    public List<SearchHit> search(String searchText, String context) {
        List<SearchHit> searchResults = null;
        
        
        
        if(context.equals("all")) {
            return searchRepository.search(searchText);
        } else if(context.equals("recipes")) {
            
        } else if(context.equals("ingredients")) {
            
        } else if(context.equals("beverages")) {
            
        }       
        return searchResults;
    }
    
}
