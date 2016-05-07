package akressiopertti.controller;

import akressiopertti.domain.Measure;
import akressiopertti.service.MeasureService;
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
@RequestMapping("mitat")
public class MeasureController {
    
    @Autowired
    private MeasureService measureService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("measures", measureService.findAll());
        return "measures";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Measure measure
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, measureService.getOptions());
        return "measure_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Measure measure,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : measureService.checkUniqueness(measure)){
            bindingResult.rejectValue(error.getObjectName(), "error.measure", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, measureService.getOptions());
            model.addAttribute("measure", measure);
            return "measure_add";
        }
        measure = measureService.save(measure);
        redirectAttributes.addFlashAttribute("success", "Mitta " + measure.getName() + " tallennettu!");
        return "redirect:/mitat";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, measureService.getOptions());
        model.addAttribute("measure", measureService.findOne(id));
        return "measure_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Measure measure,
            BindingResult bindingResult,
            Model model,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : measureService.checkUniqueness(measure)){
            bindingResult.rejectValue(error.getObjectName(), "error.measure", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, measureService.getOptions());
            model.addAttribute("measure", measure);
            return "measure_edit";
        }
        measure = measureService.save(measure);
        redirectAttributes.addFlashAttribute("success", "Mitan " + measure.getName() + " tiedot päivitetty!");
        return "redirect:/mitat";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        Measure measure = null;
        try {
            measure = measureService.remove(id);
        } catch (NullPointerException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa mittaa ei löydy!");
            return "redirect:/mitat";
        }
        redirectAttributes.addFlashAttribute("success", "Mitta " + measure.getName() + " poistettu!");
        return "redirect:/mitat";
    }
}
