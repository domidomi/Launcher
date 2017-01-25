package dominika.launcher.DateTimeTemperature;

import org.json.JSONObject;

import dominika.launcher.AsyncTaskResult;

/**
 * Created by Domi on 27.12.2016.
 */

public interface AsyncResponse {
    void processFinish(JSONObject output);
}
