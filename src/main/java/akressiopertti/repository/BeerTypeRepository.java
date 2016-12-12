package akressiopertti.repository;

import akressiopertti.domain.BeerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerTypeRepository extends JpaRepository<BeerType, Long> {
    
    public BeerType findByName(String name);
    
}
