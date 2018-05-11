package Util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Used to parse request bodies for keys
 * */
public class JSONhelper {

    private org.json.simple.parser.JSONParser parser;
    public JSONhelper(){
        parser = new org.json.simple.parser.JSONParser();
    }
    private JSONObject targetObject;

    public boolean parseBody(String requestBody){
        try {
            this.targetObject = (JSONObject) parser.parse(requestBody);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public  String getValueFromKey(String key){
        return (String)this.targetObject.get(key);
    }
}
