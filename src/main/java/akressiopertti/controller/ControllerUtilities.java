package akressiopertti.controller;

import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.ui.Model;

public class ControllerUtilities {
    
    public static Model addMappedItemsToModel(Model model, Map<String, List> map){
        for (Map.Entry<String, List> entrySet : map.entrySet()) {
            model.addAttribute(entrySet.getKey(), entrySet.getValue());
        }
        return model;
    }
    
    public static JSONArray getJSONArrayFromString(String data) throws ParseException{
        JSONArray array = null;
        JSONParser parser = new JSONParser();
        array = (JSONArray)parser.parse(data);
        return array;
    }
}
