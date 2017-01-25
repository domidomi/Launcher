package dominika.launcher.DateTimeTemperature;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dominika.launcher.Constants;
import dominika.launcher.DateTimeTemperature.AsyncResponse;
import dominika.launcher.MainActivity;
import dominika.launcher.OneFragment;
import dominika.launcher.R;

/**
 * Created by Domi on 24.01.2017.
 */

public class Weather {

    View view;
    Context context;
    String temperature = "N/A";
    String city = "N/A";
    LocationGetter locationGetter;
    TemperatureGetter temperatureGetter;

    public Weather(View view, Context context) {
        this.view = view;
        this.context = context;
        locationGetter = new LocationGetter();
        temperatureGetter = new TemperatureGetter();
    }

    public void getWeather() {
        try {
            locationGetter.getLocation();
        } catch (Exception e) {
            Toast.makeText(context, "Nie udało się pobrać temperatury dla Twojej lokacji.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public class LocationGetter implements AsyncResponse {

        public LocationGetter() {
        }

        public void getLocation() {
            if (isNetworkAvailable()) {
                LocationHelper locationHelper = new LocationHelper();
                locationHelper.delegate = this;
                locationHelper.execute(Constants.URL_LOCATION_INFO);
            } else {
                Toast.makeText(context, "Włącz internet.", Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public void processFinish(JSONObject output) {

            if (output == null) {
                Toast.makeText(context, "Nie udało się pobrać Twojej lokacji.", Toast.LENGTH_LONG).show();
            } else {

                try {
                    //JsonObject json = new JsonParser().parse(output).getAsJsonObject();
                    JSONObject jsonObj = output;
                    city = jsonObj.get("city").toString();
                    String countryCode = jsonObj.get("countryCode").toString();

                    TextView textView = (TextView) view.findViewById(R.id.city_textView);
                    textView.setText(city);

                    String url = city + "," + countryCode + "&units=metric" + "&APPID=" + Constants.WEATHER_APP_API_KEY;
                    temperatureGetter.getTemperature(url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class TemperatureGetter implements AsyncResponse {

        public TemperatureGetter() {
        }

        public void getTemperature(String url) {
            if (isNetworkAvailable()) {

                TemperatureHelper temperatureHelper = new TemperatureHelper();
                temperatureHelper.delegate = this;
                temperatureHelper.execute(Constants.URL_WEATHER_INFO + url);

            } else {
                Toast.makeText(context, "Włącz internet.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void processFinish(JSONObject output) {

            if (output == null) {
                Toast.makeText(context, "Nie udało się pobrać temperatury dla Twojej lokacji.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jObj = output;
                    JSONObject main = jObj.getJSONObject("main");

                    float temp = (float) main.getDouble("temp");

                    temperature = Integer.toString((int)temp) + "°C";

                    TextView textView = (TextView) view.findViewById(R.id.temperature_textView);
                    textView.setText(temperature);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
