package akressiopertti.service;

import akressiopertti.domain.Geography;
import akressiopertti.repository.GeographyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class GeographyService {
    
    @Autowired
    private GeographyRepository geographyRepository;
    
    public List<Geography> findAll() {
        List<Geography> result = geographyRepository.findAll();
        result.sort(Comparator.comparing(Geography::getName));
        return result;
    }
    
    public Geography findOne(Long id) {
        return geographyRepository.findOne(id);
    }
    
    public Geography save(Geography geography, JSONArray parentData) {
        ArrayList<Long> parentIDs = getParentIDs(parentData);
        Geography enrichedGeography = null;
        
        if(geography.isNew()) {
            enrichedGeography = geographyRepository.save(geography);
            enrichedGeography = addParentRelations(enrichedGeography, parentIDs);
        } else {
            Geography oldGeography = findOne(geography.getId());
            enrichedGeography = updateParentRelations(parentIDs, oldGeography);
        }
        
        return geographyRepository.save(enrichedGeography);
    }
    
    private ArrayList<Long> getParentIDs(JSONArray parentData) {
        ArrayList<Long> parentIDs = new ArrayList<>();
        for(int i = 0; i < parentData.size(); i++) {
            JSONObject parentDatum = (JSONObject)parentData.get(i);
            parentIDs.add(Long.parseLong((String)parentDatum.get("parentId")));            
        }
        return parentIDs;
    }
    
    private Geography addParentRelations(Geography child, ArrayList<Long> parentIDs) {
        for(Long parentID : parentIDs) {
            Geography parent = findOne(parentID);
            addChild(parent, child);
            child.AddParent(parent);
        }
        return child;
    }
    
    private Geography updateParentRelations(ArrayList<Long> parentIDs, Geography child) {        
        // if one of the old parents no longer a parent, remove this from
        // its children and the parent from parent list
        ArrayList<Long> removedIDs = new ArrayList<>();
        for(Geography parent : child.getParents()) {
            if(!parentIDs.contains(parent.getId())) {
                removeChild(parent, child);
                removedIDs.add(parent.getId());
            }
        }
        for(Long removedID : removedIDs) {
            child.RemoveParent(removedID);
        }
        
        // add any new parents to parent list and add this as their child
        for(Long parentID : parentIDs) {
            if(!isParent(parentID, child)) {
                Geography newParent = findOne(parentID);
                child.AddParent(newParent);
                addChild(newParent, child);
            }
        }    
        
        return child;
    }
    
    private boolean isParent(Long id, Geography child) {
        for(Geography parent : child.getParents()) {
            if(parent.getId().equals(id)) {
                return true;
            }
        }        
        return false;
    }
    
    private void removeChild(Geography parent, Geography child) {
        parent.RemoveChild(child.getId());
        geographyRepository.save(parent);
    }
    
    private void addChild(Geography parent, Geography child) {
        parent.AddChild(child);
        geographyRepository.save(parent);
    }
    
    public Geography remove(Long id) {
        Geography geography = geographyRepository.findOne(id);
        if(geography == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        geographyRepository.delete(id);
        return geography;
    }
    
    public List<ObjectError> checkUniqueness(Geography geography) {
        List<ObjectError> errors = new ArrayList<>();
        Geography anotherGeography = geographyRepository.findByName(geography.getName());
        if(anotherGeography != null && (geography.getId() == null || !anotherGeography.getId().equals(geography.getId()))) {
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }

    public Map<String, List> getOptions() {
        Map<String, List> options = new HashMap<>();
        List<String> geographyTypes = Arrays.asList("Maa", "Manner", "Viinialue");
        options.put("GeographyTypes", geographyTypes);
        options.put("Parents", findAll());
        return options;
    }
    
}
