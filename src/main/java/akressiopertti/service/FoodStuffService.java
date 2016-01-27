package akressiopertti.service;

import akressiopertti.domain.FoodStuff;
import akressiopertti.repository.FoodStuffRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class FoodStuffService {
    
    @Autowired
    private FoodStuffRepository foodStuffRepository;
    
    public List<FoodStuff> findAll(){
        return foodStuffRepository.findAll();
    }

    public FoodStuff findOne(Long id) {
        return foodStuffRepository.findOne(id);
    }
    
    public FoodStuff save(FoodStuff foodStuff) {
        return foodStuffRepository.save(foodStuff);
    }

    public FoodStuff remove(Long id) {
        FoodStuff foodStuff = foodStuffRepository.findOne(id);
        foodStuffRepository.delete(foodStuff);
        return foodStuff;
    }    

    public List<ObjectError> checkUniqueness(FoodStuff foodStuff) {
        List<ObjectError> errors = new ArrayList<>();
        FoodStuff anotherFoodStuff= foodStuffRepository.findByName(foodStuff.getName());
        if(anotherFoodStuff != null &&  anotherFoodStuff.getId().equals(foodStuff.getId())){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }
}
