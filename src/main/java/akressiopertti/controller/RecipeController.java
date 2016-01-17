/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.controller;

import akressiopertti.domain.Recipe;
import akressiopertti.service.RecipeService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("reseptit")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("recipes", recipeService.findAll());
        return "recipes";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(
            @PathVariable Long id,
            Model model
        ){
        model.addAttribute("recipe", recipeService.findOne(id));
        return "recipe";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Recipe recipe
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, recipeService.getOptions());
        return "recipe_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Recipe recipe,
            BindingResult bindingResult,
            Model model,
            @RequestParam int preparationHours,
            @RequestParam int preparationMinutes,
            RedirectAttributes redirectAttributes
        ){
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, recipeService.getOptions());
            model.addAttribute("recipe", recipe);
            return "recipe_add";
        }
        int preparationTime = preparationHours * 60 + preparationMinutes;
        recipe.setPreparationTime(preparationTime);
        recipe = recipeService.save(recipe);
        redirectAttributes.addFlashAttribute("success", "Resepti \"" + recipe.getTitle() + "\" on tallennettu!");
        return "redirect:/reseptit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, recipeService.getOptions());
        model.addAttribute("recipe", recipeService.findOne(id));
        return "recipe_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Recipe recipe,
            BindingResult bindingResult,
            Model model,
            @RequestParam int preparationHours,
            @RequestParam int preparationMinutes,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, recipeService.getOptions());
            model.addAttribute("recipe", recipe);
            return "recipe_edit";
        }
        int preparationTime = preparationHours * 60 + preparationMinutes;
        recipe.setPreparationTime(preparationTime);
        recipe = recipeService.save(recipe);
        redirectAttributes.addFlashAttribute("success", "Reseptin \"" + recipe.getTitle() + "\" tiedot päivitetty!");
        return "redirect:/reseptit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        Recipe recipe = recipeService.remove(id);
        redirectAttributes.addFlashAttribute("success", "Resepti \"" + recipe.getTitle() + "\" poistettu!");
        return "redirect:/reseptit";
    }
}
