package akressiopertti.service;

import akressiopertti.domain.Beer;
import akressiopertti.domain.BeerType;
import akressiopertti.repository.BeerRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class BeerService {
    
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BeerTypeService beerTypeService;

    public List<Beer> findAll() {
        return beerRepository.findAll();
    }

    public Beer findOne(Long id) {
        return beerRepository.findOne(id);
    }

    public Map<String, List> getOptions() {
        Map<String, List> options = new HashMap<>();
        List<BeerType> beerTypes = beerTypeService.findAll();
        options.put("BeerTypes", beerTypes);
        return options;
    }

    public List<ObjectError> checkUniqueness(Beer beer) {
        List<ObjectError> errors = new ArrayList<>();
        Beer anotherBeer = beerRepository.findByName(beer.getName());
        if(anotherBeer != null && (beer.getId() == null || !beer.getId().equals(anotherBeer.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }

    public Beer save(Beer beer) {
        beer = beerRepository.save(beer);
        BeerType beerType = beer.getBeerType();
        if(beerType != null){
            beerType.getBeers().add(beer);
            beerTypeService.save(beerType);
        }
        return beer;
    }

    public Beer remove(Long id) {
        Beer beer = beerRepository.findOne(id);
        if(beer == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        beerRepository.delete(id);
        return beer;
    }

}
