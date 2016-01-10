package akressiopertti.repository;

import akressiopertti.domain.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {
    
}
