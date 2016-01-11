/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.service;

import akressiopertti.domain.DishType;
import akressiopertti.repository.DishTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        dishTypeRepository.delete(id);
        return dishType;
    }
}
