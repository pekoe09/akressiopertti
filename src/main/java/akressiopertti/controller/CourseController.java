package akressiopertti.controller;

import akressiopertti.domain.Course;
import akressiopertti.service.CourseService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("ruokalajit")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model
        ){
        model.addAttribute("courses", courseService.findAll());
        return "courses";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.GET)
    public String add(
            Model model,
            @ModelAttribute Course course
        ){
        return "course_add";
    }
    
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    public String save(
            @Valid @ModelAttribute Course course,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ){
        if(bindingResult.hasErrors()){
            return "course_add";
        }
        course = courseService.save(course);
        redirectAttributes.addFlashAttribute("success", "Ruokalaji " + course.getName() + " tallennettu!");
        return "redirect:/ruokalajit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.GET)
    public String edit(
            @PathVariable Long id,
            Model model
        ){
        model.addAttribute("course", courseService.findOne(id));
        return "course_edit";
    }
    
    @RequestMapping(value = "/{id}/muokkaa", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute Course course,
            BindingResult bindingResult,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){
        if(bindingResult.hasErrors()){
            return "course_edit";
        }
        course = courseService.save(course);
        redirectAttributes.addFlashAttribute("success", "Ruokalajin " + course.getName() + " tiedot päivitetty!");
        return "redirect:/ruokalajit";
    }
    
    @RequestMapping(value = "/{id}/poista", method = RequestMethod.POST)
    public String remove(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
        ){        
        Course course = courseService.remove(id);
        redirectAttributes.addFlashAttribute("success", "Ruokalaji " + course.getName() + " poistettu!");
        return "redirect:/ruokalajit";
    }
}
