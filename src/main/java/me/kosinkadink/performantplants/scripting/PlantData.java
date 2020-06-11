package me.kosinkadink.performantplants.scripting;

import me.kosinkadink.performantplants.util.ScriptHelper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PlantData {

    JSONObject data;

    public PlantData(JSONObject data) {
        this.data = data;
    }

    public PlantData(String jsonString) {
        this((JSONObject) JSONValue.parse(jsonString));
    }

    public String createJsonString() {
        return data.toString();
    }

    public void updateData(PlantData plantData) {
        // updates values of data
        JSONObject newData = plantData.getData();
        for (Object key : newData.keySet()) {
            // if current data contains same key, check if same type
            if (data.containsKey(key)) {
                // update value if same type
                if (ScriptHelper.getType(data.get(key)) == ScriptHelper.getType(newData.get(key))) {
                    data.put(key, newData.get(key));
                }
            }
        }
    }

    public JSONObject getData() {
        return data;
    }

    public PlantData clone() {
        JSONObject clone = (JSONObject) JSONValue.parse(createJsonString());
        return new PlantData(clone);
    }

}