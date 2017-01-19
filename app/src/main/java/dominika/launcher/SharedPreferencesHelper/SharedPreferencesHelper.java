package dominika.launcher.SharedPreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import dominika.launcher.AllAppsGrid.AppModel;

/**
 * Created by Domi on 18.01.2017.
 */

public class SharedPreferencesHelper {

    private static Context context;
    Type listType;

    public SharedPreferencesHelper(Context context) {
        this.context = context;
    }

    public void saveList(ArrayList<AppModel> appList) throws JSONException {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        try {
            editor.remove("applist");
        } catch (Exception e) {

        }
        JSONArray jsonArray = new JSONArray();
        for(AppModel model:appList){
            jsonArray.put(model.toJSON());
        }

        editor.putString("applist", jsonArray.toString());
        editor.apply();
    }

    public ArrayList<SharedPreferencesAppModel> getList() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String stringJsonArray = sharedPrefs.getString("applist", "0");
        JSONArray jsonArray = null;

        if (stringJsonArray.equals("0")) {
            return null;
        } else {
            try {
                jsonArray = new JSONArray(stringJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<SharedPreferencesAppModel> list = new ArrayList<SharedPreferencesAppModel>();

            String category;
            String label;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    label = jsonArray.getJSONObject(i).getString("package");
                    category = jsonArray.getJSONObject(i).getString("category");
                    list.add(new SharedPreferencesAppModel(label, category));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return list;
        }
    }

}
