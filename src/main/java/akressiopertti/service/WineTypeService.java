package akressiopertti.service;

import akressiopertti.domain.WineType;
import akressiopertti.repository.WineTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class WineTypeService {
    
    @Autowired
    private WineTypeRepository wineTypeRepository;

    public List<WineType> findAll() {
        return wineTypeRepository.findAll();
    }

    public List<ObjectError> checkUniqueness(WineType wineType) {
        List<ObjectError> errors = new ArrayList<>();
        WineType anotherWineType = wineTypeRepository.findByName(wineType.getName());
        if(anotherWineType != null && (wineType.getId() == null || !anotherWineType.getId().equals(wineType.getId()))) {
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        } 
        return errors;
    }

    public WineType save(WineType wineType) {
        return wineTypeRepository.save(wineType);
    }

    public WineType findOne(Long id) {
        return wineTypeRepository.findOne(id);
    }

    public WineType remove(Long id) {
        WineType wineType = wineTypeRepository.findOne(id);
        if(wineType == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        wineTypeRepository.delete(id);
        return wineType;
    }
    
}
