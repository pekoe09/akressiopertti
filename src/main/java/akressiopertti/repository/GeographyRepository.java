package akressiopertti.repository;

import akressiopertti.domain.Geography;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeographyRepository extends JpaRepository<Geography, Long> {
    
    public Geography findByName(String name);
    
}
