package akressiopertti.repository;

import akressiopertti.domain.Measure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasureRepository extends JpaRepository<Measure, Long> {
    
}
