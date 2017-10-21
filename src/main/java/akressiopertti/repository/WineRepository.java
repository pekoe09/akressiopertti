package akressiopertti.repository;

import akressiopertti.domain.Wine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {

    public List<Wine> findByName(String name);
    
}
