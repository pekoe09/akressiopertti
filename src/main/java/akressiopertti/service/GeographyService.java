package akressiopertti.service;

import akressiopertti.domain.Geography;
import akressiopertti.repository.GeographyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class GeographyService {
    
    @Autowired
    private GeographyRepository geographyRepository;
    
    public List<Geography> findAll() {
        return geographyRepository.findAll();
    }
    
    public Geography findOne(Long id) {
        return geographyRepository.findOne(id);
    }
    
    public Geography save(Geography geography) {
        return geographyRepository.save(geography);
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
        return options;
    }
    
}
