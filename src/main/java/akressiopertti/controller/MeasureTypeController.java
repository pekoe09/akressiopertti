package akressiopertti.controller;

import akressiopertti.domain.MeasureType;
import akressiopertti.service.MeasureTypeService;
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
@RequestMapping("mittatyypit")
public class MeasureTypeController {
    
    @Autowired
    private MeasureTypeService measureTypeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("measureTypes", measureTypeService.findAll());
        return "measuretypes";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute MeasureType measureType
        ){
        return "measuretype_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute MeasureType measureType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : measureTypeService.checkUniqueness(measureType)){
            bindingResult.rejectValue(error.getObjectName(), "error.measureType", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("measureType", measureType);
            return "measuretype_add";
        }
        MeasureType savedMeasureType = measureTypeService.save(measureType);
        redirectAttributes.addFlashAttribute("success", "Mittatyyppi " + savedMeasureType.getName() + " tallennettu!");
        return "redirect:/mittatyypit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model.addAttribute("measureType", measureTypeService.findOne(id));
        return "measuretype_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute MeasureType measureType,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : measureTypeService.checkUniqueness(measureType)){
            bindingResult.rejectValue(error.getObjectName(), "error.measureType", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("measureType", measureType);
            return "measuretype_edit";
        }
        MeasureType savedMeasureType = measureTypeService.save(measureType);
        redirectAttributes.addFlashAttribute("success", "Mittatyypin " + savedMeasureType.getName() + " tiedot päivitetty!");
        return "redirect:/mittatyypit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        MeasureType measureType = null;
        try {
            measureType = measureTypeService.remove(id);
        } catch (NullPointerException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa mittatyyppiä ei löydy!");
            return "redirect:/mittatyypit";
        }
        redirectAttributes.addFlashAttribute("success", "Mittatyyppi " + measureType.getName() + " poistettu!");
        return "redirect:/mittatyypit";
    }
}
