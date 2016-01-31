package akressiopertti.repository;

import akressiopertti.domain.Recipe;
import akressiopertti.domain.RecipeIngredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    public List<RecipeIngredient> findByRecipe(Recipe recipe);
    
}
