package akressiopertti.repository;

import akressiopertti.domain.MeasureType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasureTypeRepository extends JpaRepository<MeasureType, Long> {
    
}
