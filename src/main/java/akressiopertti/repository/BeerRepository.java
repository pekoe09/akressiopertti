package akressiopertti.repository;

import akressiopertti.domain.Beer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    public Beer findByName(String name);

//    @Query("select b.brewery from Beer b")
//    public List<String> findBreweries();
    
}
