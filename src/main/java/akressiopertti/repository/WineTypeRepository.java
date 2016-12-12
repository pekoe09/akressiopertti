package akressiopertti.repository;

import akressiopertti.domain.WineType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineTypeRepository extends JpaRepository<WineType, Long> {
    
    public WineType findByName(String name);
    
}
