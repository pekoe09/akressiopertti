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
        Wine enrichedWine = null;
        if(!wine.isNew()) {
            Wine oldWine = findOne(wine.getId());
            // for pre-existing wine, updates wine type as necessary
            if((oldWine.getWineType() == null && wine.getWineType() != null)
                    || (oldWine.getWineType() != null && wine.getWineType() == null)
                    || (oldWine.getWineType() != null && !oldWine.getWineType().getId().equals(wine.getWineType().getId()))) {
                wineTypeService.setWineType(oldWine, wine.getWineType());
            }
            enrichedWine = updateGrapeContents(wine, oldWine, grapeData);
        } else {
            enrichedWine = wineRepository.save(wine);            
            wineTypeService.addWineToWineType(enrichedWine);
            enrichedWine = addGrapeContents(enrichedWine, grapeData);
        }
        
        return wineRepository.save(enrichedWine);              

    }

    public Wine remove(Long id) {
        Wine wine = wineRepository.findOne(id);
        if (wine == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());            
        }
        wineRepository.delete(id);
        return wine;
    }

    private Wine updateGrapeContents(Wine wine, Wine oldWine, JSONArray grapeData) {
        ArrayList<Long> newGrapeIDs = ServiceUtilities.getObjectIDs(grapeData, "grapeId");
        
        ArrayList<GrapeContent> removedGrapeContents = new ArrayList<>();        
        for(GrapeContent oldGrape : oldWine.getGrapes()) {
            if(!newGrapeIDs.contains(oldGrape.getGrape().getId())) {
                removedGrapeContents.add(oldGrape);
                grapeService.removeWineFromGrape(oldGrape.getGrape(), oldWine);
            }
        }
        for(GrapeContent removedContent : removedGrapeContents) {
            grapeService.removeGrapeContent(removedContent);
        }
        
        for(int i = 0; i < grapeData.size(); i++) {
            JSONObject grapeDatum = (JSONObject)grapeData.get(i);
            Grape grape = grapeService.findOne(Long.parseLong((String)grapeDatum.get("grapeId")));
            Integer share = Integer.parseInt((String)grapeDatum.get("contentPc"));
            boolean found = false;
            for(GrapeContent currGrape : oldWine.getGrapes()) {
                if(currGrape.getGrape().getId().equals(grape.getId())) {
                    currGrape.setContent(share);
                    currGrape = grapeService.save(currGrape);
                    wine.getGrapes().add(currGrape);
                    found = true;
                    break;
                }
            }    
            if(!found) {
                GrapeContent newContent = new GrapeContent();
                newContent.setWine(wine);
                newContent.setGrape(grape);
                newContent.setContent(share);
                newContent = grapeService.save(newContent);
                wine.getGrapes().add(newContent);
                grape.getWines().add(wine);
                grapeService.save(grape);
            }              
        }
        
        return wine;
    }

    private Wine addGrapeContents(Wine wine, JSONArray grapeData) {
        for(int i = 0; i < grapeData.size(); i++) {
            JSONObject grapeDatum = (JSONObject)grapeData.get(i);
            Grape grape = grapeService.findOne(Long.parseLong((String)grapeDatum.get("grapeId")));
            Integer share = Integer.parseInt((String)grapeDatum.get("contentPc"));
            GrapeContent grapeContent = new GrapeContent();
            grapeContent.setWine(wine);
            grapeContent.setGrape(grape);
            grapeContent.setContent(share);
            grapeContent = grapeService.save(grapeContent);
            wine.getGrapes().add(grapeContent);
        }
        return wine;
    }
}
