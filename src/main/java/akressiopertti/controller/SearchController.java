package akressiopertti.controller;

import akressiopertti.domain.SearchHit;
import akressiopertti.service.SearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("hae")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String showDetailedSearch(Model model) {
        
        return "";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String findGeneral(
            Model model,
            @RequestParam String searchTerm,
            @RequestParam String context,
            RedirectAttributes redirectAttributes) {
        List<SearchHit> searchResults = searchService.search(searchTerm, context);
        redirectAttributes.addAttribute("searchResults", searchResults);
        return "redirect:/hakutulokset";
    }
    
    @RequestMapping(value = "/hae/tarkka", method = RequestMethod.POST)
    public String findDetailed() {
        
        return "";
    }
    
}
