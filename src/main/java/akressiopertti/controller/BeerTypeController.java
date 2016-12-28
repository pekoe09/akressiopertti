package akressiopertti.controller;

import akressiopertti.domain.BeerType;
import akressiopertti.service.BeerTypeService;
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
@RequestMapping("/oluttyypit")
public class BeerTypeController {
    
    @Autowired
    private BeerTypeService beerTypeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model) {
        model.addAttribute("beerTypes", beerTypeService.findAll());
        return "beertypes";
    }
   
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute BeerType beerType) {
        return "beertype_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute BeerType beerType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : beerTypeService.checkUniqueness(beerType)){
            bindingResult.rejectValue(error.getObjectName(), "error.beerType", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("beerType", beerType);
            return "beertype_add";
        }
        BeerType savedBeerType = beerTypeService.save(beerType);
        redirectAttributes.addFlashAttribute("success", "Oluttyyppi " + savedBeerType.getName() + " tallennettu!");
        return "redirect:/oluttyypit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("beerType", beerTypeService.findOne(id));
        return "beertype_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute BeerType beerType,
            BindingResult bindingResult,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : beerTypeService.checkUniqueness(beerType)){
            bindingResult.rejectValue(error.getObjectName(), "error.beerType", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("beerType", beerType);
            return "beertype_edit";
        }
        BeerType savedBeerType = beerTypeService.save(beerType);
        redirectAttributes.addFlashAttribute("success", "Oluttyypin " + beerType.getName() + " tiedot päivitetty!");
        return "redirect:/oluttyypit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        BeerType beerType = null;
        try {
            beerType = beerTypeService.remove(id);                    
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa oluttyyppiä ei löydy!");
            return "redirect:/oluttyypit";
        }
        redirectAttributes.addFlashAttribute("success", "Oluttyyppi " + beerType.getName() + " poistettu!");
        return "redirect:/oluttyypit";
    }
}
