/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.service;

import akressiopertti.domain.Course;
import akressiopertti.domain.DishType;
import akressiopertti.domain.FoodStuff;
import akressiopertti.domain.Recipe;
import akressiopertti.repository.RecipeRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private FoodStuffService foodStuffService;
    @Autowired
    private DishTypeService dishTypeService;
    @Autowired
    private CourseService courseService;
    
    public List<Recipe> findAll(){
        return recipeRepository.findAll();
    }
    
    public Recipe findOne(Long id){
        return recipeRepository.findOne(id);
    }
    
    public Recipe save(Recipe recipe){
        return recipeRepository.save(recipe);
    }
    
    public Recipe remove(Long id){
        Recipe recipe = recipeRepository.findOne(id);
        recipeRepository.delete(id);
        return recipe;
    }
    
    public Map<String, List> getOptions(){
        Map<String, List> options = new HashMap<>();
        List<FoodStuff> foodStuffs = foodStuffService.findAll();
        options.put("FoodStuffs", foodStuffs);
        List<DishType> dishTypes = dishTypeService.findAll();
        options.put("DishTypes", dishTypes);
        List<Course> courses = courseService.findAll();
        options.put("Courses", courses);
        return options;
    }
}
