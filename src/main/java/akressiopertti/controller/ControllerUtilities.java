package akressiopertti.controller;

import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ControllerUtilities {
    
    public void addOptionsListsToModel(Model model, Map<String, List> map){
        for (Map.Entry<String, List> entrySet : map.entrySet()) {
            model.addAttribute(entrySet.getKey(), entrySet.getValue());
        }
    }
    
    public JSONArray getJSONArrayFromString(String data) throws ParseException{
        JSONArray array = null;
        JSONParser parser = new JSONParser();
        array = (JSONArray)parser.parse(data);
        return array;
    }
}
