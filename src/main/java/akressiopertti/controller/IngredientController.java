package akressiopertti.controller;

import akressiopertti.domain.Ingredient;
import akressiopertti.service.IngredientService;
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
@RequestMapping("ainekset")
public class IngredientController {
    
    @Autowired
    private IngredientService ingredientService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("ingredients", ingredientService.findAll());
        return "ingredients";
    }
    
    @RequestMapping(value = "/lista", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String listJSON(){
        String ingredientJSON = ingredientService.getIngredientsArray();
        return ingredientJSON;
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Ingredient ingredient
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, ingredientService.getOptions());
        return "ingredient_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Ingredient ingredient,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : ingredientService.checkUniqueness(ingredient)){
            bindingResult.rejectValue(error.getObjectName(), "error.ingredient", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, ingredientService.getOptions());
            return "ingredient_add";
        }
        ingredient = ingredientService.save(ingredient);
        redirectAttributes.addFlashAttribute("success", "Aines "+ ingredient.getName() + " tallennettu!");
        return "redirect:/ainekset";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model = ControllerUtilities.addMappedItemsToModel(model, ingredientService.getOptions());
        model.addAttribute("ingredient", ingredientService.findOne(id));
        return "ingredient_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Ingredient ingredient,
            BindingResult bindingResult,
            Model model,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        for(ObjectError error : ingredientService.checkUniqueness(ingredient)){
            bindingResult.rejectValue(error.getObjectName(), "error.ingredient", error.getDefaultMessage());
        }
        if(bindingResult.hasErrors()){
            model = ControllerUtilities.addMappedItemsToModel(model, ingredientService.getOptions());
            return "ingredient_edit";
        }
        ingredient = ingredientService.save(ingredient);
        redirectAttributes.addFlashAttribute("success", "Aineksen " + ingredient.getName() + " tiedot päivitetty!");
        return "redirect:/ainekset";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        Ingredient ingredient = ingredientService.remove(id);
        redirectAttributes.addFlashAttribute("success", "Aines " + ingredient.getName() + " poistettu!");
        return "redirect:/ainekset";
    }
}
