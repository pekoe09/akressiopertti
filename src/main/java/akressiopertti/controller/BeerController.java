package akressiopertti.controller;

import akressiopertti.domain.Beer;
import akressiopertti.service.BeerService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/oluet")
public class BeerController {
    
    @Autowired
    private BeerService beerService;
    @Autowired
    private ControllerUtilities controllerUtilities;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model){
        model.addAttribute("beers", beerService.findAll());
        return "beers";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("beer", beerService.findOne(id));
        return "beer";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Beer beer){
        controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
        return "beer_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Beer beer,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        for(ObjectError error : beerService.checkUniqueness(beer)){
            bindingResult.rejectValue(error.getObjectName(), "error.beer", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
            model.addAttribute("beer", beer);
            return "beer_add";
        }
        Beer savedBeer = null;
        try {
            savedBeer = beerService.save(beer);            
        } catch(IllegalArgumentException exc) {
            controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
            model.addAttribute("beer", beer);
            return "beer_add";
        }
        redirectAttributes.addFlashAttribute("success", "Olut \"" + savedBeer.getName() + "\" tallennettu!");
        return "redirect:/oluet";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model){
        controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
        model.addAttribute("beer", beerService.findOne(id));
        return "beer_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Beer beer,
            BindingResult bindingResult,
            Model model,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes){
        for(ObjectError error : beerService.checkUniqueness(beer)){
            bindingResult.rejectValue(error.getObjectName(), "error.beer", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
            model.addAttribute("beer", beer);
            return "beer_edit";
        }
        Beer savedBeer = null;
        try{
            savedBeer = beerService.save(beer);
        } catch(IllegalArgumentException exc){
            controllerUtilities.addOptionsListsToModel(model, beerService.getOptions());
            model.addAttribute("beer", beer);
            return "beer_edit";
        }
        redirectAttributes.addFlashAttribute("success", "Oluen \"" + savedBeer.getName() + "\" tiedot päivitetty!");
        return "redirect:/oluet";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Beer beer = null;
        try{
            beer = beerService.remove(id);
        } catch(IllegalArgumentException exc){
            redirectAttributes.addFlashAttribute("error", "Poistettavaa olutta ei löydy!");
            return "redirect:/oluet";
        }
        redirectAttributes.addFlashAttribute("success", "Olut \"" + beer.getName() + "\" poistettu!");
        return "redirect:/oluet";
    }
            
    @RequestMapping(value = "/panimot", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String listBreweries() {
        String breweryJSON = beerService.getBreweryArray();
        return breweryJSON;
    }
}
