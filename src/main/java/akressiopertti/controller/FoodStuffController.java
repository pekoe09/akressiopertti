package akressiopertti.controller;

import akressiopertti.domain.FoodStuff;
import akressiopertti.service.FoodStuffService;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("ruoka-aineet")
public class FoodStuffController {
    
    @Autowired
    private FoodStuffService foodStuffService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("foodStuffs", foodStuffService.findAll());
        return "foodstuffs";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute FoodStuff foodStuff
        ){
        return "foodstuff_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute FoodStuff foodStuff,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : foodStuffService.checkUniqueness(foodStuff)){
            bindingResult.rejectValue(error.getObjectName(), "error.foodstuff", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("foodStuff", foodStuff);
            return "foodstuff_add";
        } 
        FoodStuff savedFoodStuff = foodStuffService.save(foodStuff);
        redirectAttributes.addFlashAttribute("success", "Ruoka-aine " + savedFoodStuff.getName() + " tallennettu!");
        return "redirect:/ruoka-aineet";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model.addAttribute("foodStuff", foodStuffService.findOne(id));
        return "foodstuff_edit";
    }    
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(   
            @Valid @ModelAttribute FoodStuff foodStuff,
            BindingResult bindingResult,            
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : foodStuffService.checkUniqueness(foodStuff)){
            bindingResult.rejectValue(error.getObjectName(), "error.foodStuff", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("foodStuff", foodStuff);
            return "foodstuff_edit";
        }
        FoodStuff savedFoodStuff = foodStuffService.save(foodStuff);
        redirectAttributes.addFlashAttribute("success", "Ruoka-aineen " + savedFoodStuff.getName() + " tiedot päivitetty!");
        return "redirect:/ruoka-aineet";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        FoodStuff foodStuff =  null;
        try {
            foodStuff = foodStuffService.remove(id);
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa ruoka-ainetta ei löydy!");
            return "redirect:/ruoka-aineet";           
        }
        redirectAttributes.addFlashAttribute("success", "Ruoka-aine " + foodStuff.getName() + " poistettu!");
        return "redirect:/ruoka-aineet";
    }
}
