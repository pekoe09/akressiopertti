package akressiopertti.service;

import akressiopertti.domain.Measure;
import akressiopertti.domain.MeasureType;
import akressiopertti.domain.RecipeIngredient;
import akressiopertti.repository.MeasureRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

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
        if(measure == null) {
            throw new NullPointerException("Cannot remove object with id " + id.toString());
        }
        measureRepository.delete(id);
        return measure;
    }

    public Map<String, List> getOptions() {
        List<MeasureType> measureTypes = measureTypeService.findAll();
        Map<String, List> options = new HashMap<>();
        options.put("MeasureTypes", measureTypes);
        return options;
    }

    public void addRecipeToMeasure(RecipeIngredient recipeIngredient) {
        Measure measure = recipeIngredient.getMeasure();
        if(measure != null){
            measure.getRecipeIngredients().add(recipeIngredient);
            measureRepository.save(measure);
        }
    }

    public void removeRecipeIngredientFromMeasure(RecipeIngredient recipeIngredient) {
        Measure measure = recipeIngredient.getMeasure();
        if(measure != null){
            measure.getRecipeIngredients().remove(recipeIngredient);
            measureRepository.save(measure);
        }
    }
    
    public List<ObjectError> checkUniqueness(Measure measure) {
        List<ObjectError> errors = new ArrayList<>();
        Measure anotherMeasure= measureRepository.findByName(measure.getName());
        if(anotherMeasure != null && (measure.getId() == null || !anotherMeasure.getId().equals(measure.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        anotherMeasure = measureRepository.findByPartitive(measure.getPartitive());
        if(anotherMeasure != null && (measure.getId() == null || !anotherMeasure.getId().equals(measure.getId()))){
            errors.add(new ObjectError("partitive", "Nimen partitiivi on jo varattu"));
        }
        anotherMeasure = measureRepository.findByAbbreviation(measure.getAbbreviation());
        if(anotherMeasure != null && (measure.getId() == null || !anotherMeasure.getId().equals(measure.getId()))){
            errors.add(new ObjectError("abbreviation", "Nimen lyhenne on jo varattu"));
        }
        return errors;
    }

}
