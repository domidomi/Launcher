package dominika.launcher.DateTimeTemperature;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import dominika.launcher.AsyncTaskResult;
import dominika.launcher.Constants;

/**
 * Created by Domi on 25.01.2017.
 */

public class TemperatureHelper extends AsyncTask<String, Void, AsyncTaskResult<JSONObject>> {

    public AsyncResponse delegate = null;
    Exception error = null;

    public TemperatureHelper() {
    }

    @Override
    protected AsyncTaskResult<JSONObject> doInBackground(String... params) {
        try {
            // get  JSONObject from the server
            String json = Jsoup.connect(params[0]).ignoreContentType(true).execute().body();
            JSONObject jObj = new JSONObject(json);

            return new AsyncTaskResult<JSONObject>(jObj);
        } catch ( Exception anyError) {
            return new AsyncTaskResult<JSONObject>(anyError);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<JSONObject> result) {

        super.onPostExecute(result);
        if ( result.getError() != null ) {
            System.err.println("Error.");
            result = null;
            delegate.processFinish(null);
        }  else if ( isCancelled()) {
            // cancel handling here
            System.err.println("Canceled.");
            result = null;
            delegate.processFinish(null);
        } else {
            JSONObject realResult = result.getResult();
            delegate.processFinish(realResult);
        }

    }


}
