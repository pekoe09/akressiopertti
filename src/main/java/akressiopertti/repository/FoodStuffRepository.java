package akressiopertti.repository;

import akressiopertti.domain.FoodStuff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodStuffRepository extends JpaRepository<FoodStuff, Long> {

    public FoodStuff findByName(String name);
    
}
