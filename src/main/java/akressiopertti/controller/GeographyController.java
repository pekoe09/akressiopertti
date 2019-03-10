package akressiopertti.controller;

import akressiopertti.domain.Geography;
import akressiopertti.service.GeographyService;
import akressiopertti.service.RelationViolationException;
import javax.validation.Valid;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("alueet")
public class GeographyController {
    
    @Autowired
    private GeographyService geographyService;
    @Autowired
    private ControllerUtilities controllerUtilities;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("geographies", geographyService.findAll());
        return "geographies";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Geography geography
    ){
        controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
        return "geography_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Geography geography,
            BindingResult bindingResult,
            Model model,
            @RequestParam String parentData,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : geographyService.checkUniqueness(geography)){
            bindingResult.rejectValue(error.getObjectName(), "error.geography", error.getDefaultMessage());            
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("geography", geography);
            controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
            return "geography_add";
        }
        Geography savedGeography = null;
        try {
            savedGeography = geographyService.save(geography, controllerUtilities.getJSONArrayFromString(parentData));
        } catch (ParseException exc) {
            model.addAttribute("geography", geography);
            controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
            return "geography_add";
        }
        redirectAttributes.addFlashAttribute("success", "Alue " + savedGeography.getName() + " tallennettu!");
        return "redirect:/alueet";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
    ){
        controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
        model.addAttribute("geography", geographyService.findOne(id));        
        return "geography_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Geography geography,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            @RequestParam String parentData,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : geographyService.checkUniqueness(geography)){
            bindingResult.rejectValue(error.getObjectName(), "error.geography", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
            model.addAttribute("geography", geography);            
            return "geography_edit";
        }
        Geography savedGeography = null;
        try {
            savedGeography = geographyService.save(geography, controllerUtilities.getJSONArrayFromString(parentData));
        } catch (ParseException exc) {
            controllerUtilities.addOptionsListsToModel(model, geographyService.getOptions());
            model.addAttribute("geography", geography);            
            return "geography_edit";
        }        
        redirectAttributes.addFlashAttribute("success", "Alueen " + savedGeography.getName() + " tiedot päivitetty!");
        return "redirect:/alueet";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        Geography geography = null;
        try {
            geography = geographyService.remove(id);
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa aluetta ei löydy!");
            return "redirect:/alueet";
        } catch (RelationViolationException rve) {
            redirectAttributes.addFlashAttribute("error", "Aluetta ei voi poistaa, koska siihen on linkitetty muita alueita");
            return "redirect:/alueet";
        }
        redirectAttributes.addFlashAttribute("success", "Alue " + geography.getName() + " poistettu!");
        return "redirect:/alueet";
    }   
}
