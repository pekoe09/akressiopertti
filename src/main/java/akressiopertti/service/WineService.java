package akressiopertti.service;

import akressiopertti.domain.Grape;
import akressiopertti.domain.Wine;
import akressiopertti.domain.WineType;
import akressiopertti.repository.WineRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class WineService {
    
    @Autowired
    private WineRepository wineRepository;
    @Autowired
    private GrapeService grapeService;
    @Autowired
    private WineTypeService wineTypeService;

    public List<Wine> findAll() {
        return wineRepository.findAll();
    }

    public Wine findOne(Long id) {
        return wineRepository.findOne(id);
    }

    public Map<String, List> getOptions() {
        Map<String, List> options = new HashMap<>();
        List<WineType> wineTypes = wineTypeService.findAll();
        options.put("WineTypes", wineTypes);
        List<Grape> grapes = grapeService.findAll();
        options.put("Grapes", grapes);
        return options;
    }

    public List<ObjectError> checkUniqueness(Wine wine) {
        List<ObjectError> errors = new ArrayList<>();
        
        return errors;
    }

    public Wine save(Wine wine) {
        wine = wineRepository.save(wine);
        WineType wineType = wine.getWineType();
        if(wineType != null) {
            wineType.getWines().add(wine);
            wineTypeService.save(wineType);
        }
        List<Grape> grapes = wine.getGrapes();
        for(Grape grape : grapes) {
            grape.getWines().add(wine);
            grapeService.save(grape);
        }
        return wine;
    }

    public Wine remove(Long id) {
        Wine wine = wineRepository.findOne(id);
        if (wine == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());            
        }
        wineRepository.delete(id);
        return wine;
    }
 
}
