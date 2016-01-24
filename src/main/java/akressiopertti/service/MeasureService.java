package akressiopertti.service;

import akressiopertti.domain.Measure;
import akressiopertti.domain.MeasureType;
import akressiopertti.domain.RecipeIngredient;
import akressiopertti.repository.MeasureRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasureService {    
    
    @Autowired
    private MeasureRepository measureRepository;
    @Autowired
    private MeasureTypeService measureTypeService;
    
    public List<Measure> findAll(){
        return measureRepository.findAll();
    }
    
    public Measure findOne(Long id){
        return measureRepository.findOne(id);
    }
    
    public Measure save(Measure measure){
        return measureRepository.save(measure);
    }
    
    public Measure remove(Long id){
        Measure measure = measureRepository.findOne(id);
        measureRepository.delete(id);
        return measure;
    }

    public Map<String, List> getOptions() {
        List<MeasureType> measureTypes = measureTypeService.findAll();
        Map<String, List> options = new HashMap<>();
        options.put("MeasureTypes", measureTypes);
        return options;
    }

    void addRecipeToMeasure(RecipeIngredient recipeIngredient) {
        Measure measure = recipeIngredient.getMeasure();
        if(measure != null){
            measure.getRecipeIngredients().add(recipeIngredient);
            measureRepository.save(measure);
        }
    }
}
