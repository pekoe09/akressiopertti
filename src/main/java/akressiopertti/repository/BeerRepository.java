package akressiopertti.repository;

import akressiopertti.domain.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, Long> {
    
}
