package akressiopertti.service;

import akressiopertti.domain.Grape;
import akressiopertti.domain.GrapeContent;
import akressiopertti.domain.Wine;
import akressiopertti.repository.GrapeContentRepository;
import akressiopertti.repository.GrapeRepository;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class GrapeService {
    
    @Autowired
    private GrapeRepository grapeRepository;
    @Autowired
    private GrapeContentRepository grapeContentRepository;

    public List<Grape> findAll() {
        return grapeRepository.findAll();
    }    
    
    public Grape findOne(Long id) {
        return grapeRepository.findOne(id);
    } 

    public List<ObjectError> checkUniqueness(Grape grape) {
        List<ObjectError> errors = new ArrayList<>();
        Grape anotherGrape = grapeRepository.findByName(grape.getName());
        if(anotherGrape != null && (grape.getId() == null || !anotherGrape.getId().equals(grape.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }

    public Grape save(Grape grape) {
        return grapeRepository.save(grape);
    }

    public Grape remove(Long id) {
        Grape grape = grapeRepository.findOne(id);
        if(grape == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        grapeRepository.delete(id);
        return grape;
    }

    public String getGrapesArray() {
        JSONArray responseArray = new JSONArray();
        JSONObject grapeObject;
        
        List<Grape> grapes = grapeRepository.findAll();
        for(Grape grape : grapes){
            grapeObject = new JSONObject();
            grapeObject.put("name", grape.getName());
            grapeObject.put("id", grape.getId());
            responseArray.add(grapeObject);
        }
        
        return responseArray.toJSONString();
    }

    public void removeWineFromGrape(Grape grape, Wine wine) {
        int index = -1;
        for(int i = 0; i < grape.getWines().size(); i++) {
            if(grape.getWines().get(i).getId() == wine.getId()) {
                index = i;
                break;
            }
        }
        if(index > -1) {
            grape.getWines().remove(index);
        }
        save(grape);
    }

    public GrapeContent removeGrapeContent(GrapeContent grapeContent) {
        GrapeContent savedGrapeContent = grapeContentRepository.findOne(grapeContent.getId());
        if(savedGrapeContent == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + grapeContent.getId());
        }
        grapeContentRepository.delete(grapeContent);
        return grapeContent;
    }

    public GrapeContent save(GrapeContent grapeContent) {
        return grapeContentRepository.save(grapeContent);
    }
   
}
