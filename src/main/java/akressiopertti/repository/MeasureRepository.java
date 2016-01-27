package akressiopertti.repository;

import akressiopertti.domain.Measure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasureRepository extends JpaRepository<Measure, Long> {

    public Measure findByName(String name);

    public Measure findByPartitive(String partitive);

    public Measure findByAbbreviation(String abbreviation);
    
}
