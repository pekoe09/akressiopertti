package akressiopertti.service;

import akressiopertti.domain.Grape;
import akressiopertti.domain.GrapeContent;
import akressiopertti.domain.Wine;
import akressiopertti.domain.WineType;
import akressiopertti.repository.WineRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    
    public List<Wine> findByName(String name) {
        return wineRepository.findByName(name);
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

    public Wine save(Wine wine, JSONArray grapeData) {
        //removes all old cross-refs in grapes and wine types
        if(wine.getId() != null) {
            Wine oldWine = findOne(wine.getId());
            for(GrapeContent grapeContent: oldWine.getGrapes()) {
                grapeService.removeWineFromGrape(grapeContent.getGrape(), oldWine);
                removeGrapeContent(grapeContent);
            }
            wineTypeService.removeWineFromWineType(oldWine);
        }                
                
        Wine savedWine = wineRepository.save(wine);       
        
        // adds cross-ref to wine type
        WineType wineType = savedWine.getWineType();
        if(wineType != null) {
            wineType.getWines().add(wine);
            wineTypeService.save(wineType);
        }
        
        // adds grape content and cross-ref to each grape
        List<GrapeContent> grapes = new ArrayList<>();
        for(int i = 0; i < grapeData.size(); i++) {
            JSONObject grapeDatum = (JSONObject)grapeData.get(i);
            GrapeContent grapeContent = new GrapeContent();
            grapeContent.setWine(savedWine);
            Grape grape = grapeService.findOne(Long.parseLong((String)grapeDatum.get("grapeId")));
            grapeContent.setGrape(grape);
            grapeContent.setContent(Integer.parseInt((String)grapeDatum.get("contentPc")));
            GrapeContent savedGrapeContent = grapeService.save(grapeContent);
            savedWine.getGrapes().add(grapeContent);
            grape.getWines().add(savedWine);
            grapeService.save(grape);
        }
        savedWine = wineRepository.save(savedWine);
        
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

    private void removeGrapeContent(GrapeContent grapeContent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
