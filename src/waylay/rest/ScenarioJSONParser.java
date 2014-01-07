package waylay.rest;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import waylay.client.scenario.Condition;
import waylay.client.scenario.Scenario;
import waylay.client.scenario.ScenarioStatus;

import android.util.Log;

public class ScenarioJSONParser {
	public static final String TAG = "Scenario";
	
	public static ArrayList<Scenario> getAllScenariosViaJSON(String jsonString) throws Exception {
		Log.d(TAG, "getAllScenariosViaJSON");
		ArrayList<Scenario> remoteScenarioStatuses = new ArrayList<Scenario>();
        JSONArray array = new JSONArray(jsonString);
        for(int i=0; i < array.length(); i++){
            JSONObject value = array.getJSONObject(i);
            JSONObject target = value.getJSONObject("target");
            String name = value.getString("name");
            String status = value.getString("status");
            Number refreshRate = (Number) value.getLong("frequency");

            JSONObject conditionObj = value.getJSONObject("condition");
            Condition condition = new Condition((Number)conditionObj.getLong("threshold"),
                    (Number) conditionObj.getLong("operator"), conditionObj.getString("stopState"));
            
            Long id = value.getLong("ID");
            Log.d(TAG, "getAllScenariosViaJSON got scenario "+id);
            remoteScenarioStatuses.add(new Scenario(name, (String) target.get("name"), id, ScenarioStatus.getStatus(status),
                    condition, refreshRate.intValue()));
        }
        return remoteScenarioStatuses;
    }

}