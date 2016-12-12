package akressiopertti.controller;

import akressiopertti.domain.Grape;
import akressiopertti.service.GrapeService;
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
@RequestMapping("/rypaleet")
public class GrapeController {
    
    @Autowired
    private GrapeService grapeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model) {
        model.addAttribute("grapes", grapeService.findAll());
        return "grapes";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Grape grape) {
        return "grape_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Grape grape,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : grapeService.checkUniqueness(grape)){
            bindingResult.rejectValue(error.getObjectName(), "error.grape", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("grape", grape);
            return "grape_add";
        }
        Grape savedGrape = grapeService.save(grape);
        redirectAttributes.addFlashAttribute("success", "Rypälelajike " + savedGrape.getName() + " tallennettu!");
        return "redirect:/rypaleet";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model){
        model.addAttribute("grape", grapeService.findOne(id));
        return "grape_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Grape grape,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : grapeService.checkUniqueness(grape)){
            bindingResult.rejectValue(error.getObjectName(), "error.grape", error.getDefaultMessage());            
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("grape", grape);
            return "grape_edit";
        }
        Grape savedGrape = grapeService.save(grape);
        redirectAttributes.addFlashAttribute("success", "Rypälelajikkeen " + savedGrape.getName() + " tiedot päivitetty");
        return "redirect:/rypaleet";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Grape grape = null;
        try {
            grape = grapeService.remove(id);
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa rypälelajiketta ei löydy!");
            return "redirect:/rypaleet";
        }
        redirectAttributes.addFlashAttribute("success", "Rypälelajike " + grape.getName() + " poistettu!");
        return "redirect:/rypaleet";
    }
}
