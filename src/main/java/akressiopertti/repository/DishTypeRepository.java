package akressiopertti.repository;

import akressiopertti.domain.DishType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishTypeRepository extends JpaRepository<DishType, Long> {
    
}
