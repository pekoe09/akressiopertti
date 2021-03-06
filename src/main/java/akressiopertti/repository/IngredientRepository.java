package akressiopertti.repository;

import akressiopertti.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    public Ingredient findByName(String name);

    public Ingredient findByPartitive(String partitive);
    
}
