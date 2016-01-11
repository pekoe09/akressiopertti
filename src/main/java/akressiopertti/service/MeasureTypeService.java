package akressiopertti.service;

import akressiopertti.domain.MeasureType;
import akressiopertti.repository.MeasureTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        measureTypeRepository.delete(id);
        return measureType;
    }
    
}
