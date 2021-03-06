package akressiopertti.service;

import akressiopertti.domain.MeasureType;
import akressiopertti.repository.MeasureTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class MeasureTypeService {
    
    @Autowired
    private MeasureTypeRepository measureTypeRepository;
    
    public List<MeasureType> findAll(){
        return measureTypeRepository.findAll();
    }
    
    public MeasureType findOne(Long id){
        return measureTypeRepository.findOne(id);
    }
    
    public MeasureType save(MeasureType measureType) {
        return measureTypeRepository.save(measureType);
    }
    
    public MeasureType remove(Long id){
        MeasureType measureType = measureTypeRepository.findOne(id);
        if(measureType == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        measureTypeRepository.delete(id);
        return measureType;
    }

    public List<ObjectError> checkUniqueness(MeasureType measureType) {
        List<ObjectError> errors = new ArrayList<>();
        MeasureType anotherMeasureType = measureTypeRepository.findByName(measureType.getName());
        if(anotherMeasureType != null && (measureType.getId() == null || !anotherMeasureType.getId().equals(measureType.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }
    
}
