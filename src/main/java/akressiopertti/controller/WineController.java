package akressiopertti.controller;

import akressiopertti.domain.Wine;
import akressiopertti.service.WineService;
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
@RequestMapping("/viinit")
public class WineController {
    
    @Autowired
    private WineService wineService;
    @Autowired
    private ControllerUtilities controllerUtilities;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model) {
        model.addAttribute("wines", wineService.findAll());
        return "wines";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("wine", wineService.findOne(id));
        return "wine";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Wine wine) {
        controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
        return "wine_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Wine wine,
            BindingResult bindingResult,
            Model model,
            @RequestParam String grapeData,
            @RequestParam Long countryId,
            @RequestParam Long regionId,
            RedirectAttributes redirectAttributes) {
        for (ObjectError error : wineService.checkUniqueness(wine)) {
            bindingResult.rejectValue(error.getObjectName(), "error.wine", error.getDefaultMessage());            
        }
        if (bindingResult.hasErrors()) {
            controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
            model.addAttribute("wine", wine);
            return "wine_add";
        }
        Wine savedWine = null;
        try {
            savedWine = wineService.save(
                    wine, 
                    controllerUtilities.getJSONArrayFromString(grapeData),
                    countryId,
                    regionId
            );
        } catch (ParseException exc) {
            controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
            model.addAttribute("wine", wine);
            return "wine_add";
        }
        redirectAttributes.addFlashAttribute("success", "Viini \"" + savedWine.getName() + "\" tallennettu!");
        return "redirect:/viinit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model) {
        controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
        model.addAttribute("wine", wineService.findOne(id));
        return "wine_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Wine wine,
            BindingResult bindingResult,
            Model model,
            @RequestParam String grapeData,
            @RequestParam Long countryId,
            @RequestParam Long regionId,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        for (ObjectError error : wineService.checkUniqueness(wine)) {
            bindingResult.rejectValue(error.getObjectName(), "error.wine", error.getDefaultMessage());
        }
        if (bindingResult.hasErrors()) {
            controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
            model.addAttribute("wine", wine);
            return "wine_edit";
        }
        Wine savedWine = null;
        try {
            savedWine = wineService.save(
                    wine,
                    controllerUtilities.getJSONArrayFromString(grapeData),
                    countryId,
                    regionId
            );
        } catch (ParseException exc) {
            controllerUtilities.addOptionsListsToModel(model, wineService.getOptions());
            model.addAttribute("wine", wine);
            return "wine_edit";
        }
        redirectAttributes.addFlashAttribute("success", "Viinin \"" + savedWine.getName() + "\" tiedot päivitetty!");
        return "redirect:/viinit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Wine wine = null;
        try {
            wine = wineService.remove(id);
        } catch (IllegalArgumentException exc) {
            redirectAttributes.addFlashAttribute("error", "Poistettavaa viiniä ei löydy");
            return "redirect:/viinit";
        }
        redirectAttributes.addFlashAttribute("success", "Viini  \"" + wine.getName() + "\" poistettu!");
        return "redirect:/viinit";
    }
    
}
