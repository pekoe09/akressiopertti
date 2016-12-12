package akressiopertti.repository;

import akressiopertti.domain.Grape;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrapeRepository extends JpaRepository<Grape, Long> {
    
    public Grape findByName(String name);
    
}
