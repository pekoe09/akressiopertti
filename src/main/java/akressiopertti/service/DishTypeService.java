package akressiopertti.service;

import akressiopertti.domain.DishType;
import akressiopertti.repository.DishTypeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class DishTypeService {
    
    @Autowired
    private DishTypeRepository dishTypeRepository;

    public List<DishType> findAll() {
        return dishTypeRepository.findAll();
    }
    
    public DishType findOne(Long id) {
        return dishTypeRepository.findOne(id);
    }

    public DishType save(DishType dishType) {
        return dishTypeRepository.save(dishType);
    }
    
    public DishType remove(Long id){
        DishType dishType = dishTypeRepository.findOne(id);
        if(dishType == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        dishTypeRepository.delete(id);
        return dishType;
    }

    public List<ObjectError> checkUniqueness(DishType dishType) {
        List<ObjectError> errors = new ArrayList<>();
        DishType anotherDishtype = dishTypeRepository.findByName(dishType.getName());
        if(anotherDishtype != null && (dishType.getId() == null || !anotherDishtype.getId().equals(dishType.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }
}
