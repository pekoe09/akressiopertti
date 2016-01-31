package akressiopertti.controller;

import akressiopertti.domain.User;
import akressiopertti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {   
    
    @Autowired
    private UserService userService;
        
    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    
    @ModelAttribute
    public void globalAttributes(Model model) {
        User user = userService.getAuthenticatedUser();
        model.addAttribute("userId", user != null ? user.getId() : null);
        model.addAttribute("nameOfUser", user != null ? user.getName() : null);
    }
}
