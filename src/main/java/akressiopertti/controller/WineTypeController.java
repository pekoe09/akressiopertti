package akressiopertti.controller;

import akressiopertti.domain.WineType;
import akressiopertti.service.WineTypeService;
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
@RequestMapping("/viinityypit")
public class WineTypeController {
    
    @Autowired
    private WineTypeService wineTypeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model) {
        model.addAttribute("winetypes", wineTypeService.findAll());
        return "winetypes";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute WineType wineType) {
        return "winetype_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute WineType wineType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : wineTypeService.checkUniqueness(wineType)){
            bindingResult.rejectValue(error.getObjectName(), "error.grape", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("winetype", wineType);
            return "winetype_add";
        }
        WineType savedWineType = wineTypeService.save(wineType);
        redirectAttributes.addFlashAttribute("success", "Viinityyppi " + savedWineType.getName() + " tallennettu!");
        return "redirect:/viinityypit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model){
        model.addAttribute("winetype", wineTypeService.findOne(id));
        return "winetype_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute WineType wineType,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : wineTypeService.checkUniqueness(wineType)) {
            bindingResult.rejectValue(error.getObjectName(), "error.wineType", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("winetype", wineType);
            return "winetype_edit";
        }
        WineType savedWineType = wineTypeService.save(wineType);
        redirectAttributes.addFlashAttribute("success", "Viinityypin " + wineType.getName() + " tiedot päivitetty!");
        return "redirect:/viinityypit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        WineType wineType = null;
        try  {
            wineType = wineTypeService.remove(id);
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa viinityyppiä ei löydy!");
            return "redirect:/viinityypit";
        }
        redirectAttributes.addFlashAttribute("success", "Viinityyppi " + wineType.getName() + " poistettu!");
        return "redirect:/viinityypit";
    }
}
