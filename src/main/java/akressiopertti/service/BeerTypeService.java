package akressiopertti.service;

import akressiopertti.domain.BeerType;
import akressiopertti.repository.BeerTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class BeerTypeService {
    
    @Autowired
    private BeerTypeRepository beerTypeRepository;

    public List<BeerType> findAll() {
        return beerTypeRepository.findAll();
    }

    public BeerType findOne(Long id) {
        return beerTypeRepository.findOne(id);
    }
    
    public List<ObjectError> checkUniqueness(BeerType beerType) {
        List<ObjectError> errors = new ArrayList<>();
        BeerType anotherBeerType = beerTypeRepository.findByName(beerType.getName());
        if(anotherBeerType != null && (beerType.getId() == null || !anotherBeerType.getId().equals(beerType.getId()))) {
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }

    public BeerType save(BeerType beerType) {
        return beerTypeRepository.save(beerType);
    }

    public BeerType remove(Long id) {
        BeerType beerType = beerTypeRepository.findOne(id);
        if(beerType == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        beerTypeRepository.delete(id);
        return beerType;
    }

}
