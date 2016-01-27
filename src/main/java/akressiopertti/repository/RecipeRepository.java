package akressiopertti.repository;

import akressiopertti.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{

    public Recipe findByTitle(String title);
    
}
