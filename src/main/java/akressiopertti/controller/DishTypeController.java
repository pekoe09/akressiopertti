package akressiopertti.controller;

import akressiopertti.domain.DishType;
import akressiopertti.service.DishTypeService;
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
@RequestMapping("/ruokatyypit")
public class DishTypeController {
    
    @Autowired
    private DishTypeService dishTypeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("dishTypes", dishTypeService.findAll());
        return "dishtypes";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute DishType dishType
        ){
        return "dishtype_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute DishType dishType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : dishTypeService.checkUniqueness(dishType)){
            bindingResult.rejectValue(error.getObjectName(), "error.dishtype", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("dishType", dishType);
            return "dishtype_add";
        }
        DishType savedDishType = dishTypeService.save(dishType);
        redirectAttributes.addFlashAttribute("success", "Ruokatyyppi " + savedDishType.getName() + " tallennettu!");
        return "redirect:/ruokatyypit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model.addAttribute("dishType", dishTypeService.findOne(id));
        return "dishtype_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa",  method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute DishType dishType,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : dishTypeService.checkUniqueness(dishType)){
            bindingResult.rejectValue(error.getObjectName(), "error.dishtype", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("dishType", dishType);
            return "dishtype_edit";
        }
        DishType savedDishType = dishTypeService.save(dishType);
        redirectAttributes.addFlashAttribute("success", "Ruokatyypin " + savedDishType.getName() + " tiedot päivitetty!");
        return "redirect:/ruokatyypit";
    }
    
    @RequestMapping(value = "/{id}/poista",  method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        DishType dishType = null;
        try {
            dishType = dishTypeService.remove(id);
        } catch (NullPointerException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa ruokatyyppiä ei löydy!");
            return "redirect:/ruokatyypit";       
        }
        
        redirectAttributes.addFlashAttribute("success", "Ruokatyyppi " + dishType.getName() + " poistettu!");
        return "redirect:/ruokatyypit";
    }
}
