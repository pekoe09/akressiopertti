package akressiopertti.service;

import akressiopertti.domain.Grape;
import akressiopertti.repository.GrapeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class GrapeService {
    
    @Autowired
    private GrapeRepository grapeRepository;

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
   
}
