package akressiopertti.service;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ServiceUtilities {
    
    public static ArrayList<Long> getObjectIDs(JSONArray data, String idFieldName) {
        ArrayList<Long> ids = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            JSONObject datum = (JSONObject)data.get(i);
            ids.add(Long.parseLong((String)datum.get(idFieldName)));            
        }
        return ids;
    }
    
}
