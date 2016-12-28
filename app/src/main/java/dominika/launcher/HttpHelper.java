package dominika.launcher;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Domi on 28.11.2016.
 */

public class HttpHelper extends AsyncTask<URL, Void, String>{

    @Override
    protected String doInBackground(URL... params) {
        String category = null;

        try {
            URL url = new URL(params[0].toString());
            Log.d("Otwieram połączenie ", url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            int status = connection.getResponseCode();

            if(status != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "http response code is " + connection.getResponseCode());
                return null;
            }

            /* JSOUP */
            Document doc = Jsoup.connect(params[0].toString()).get();
            Element element = doc.select("span[itemprop = genre]").first();
            category = element.text();
            Log.d("Genre = ", category);
            return category;

            // Convert the InputStream into a string
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("Catch: ", "UnsupportedEncodingException");
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d("Catch: ", "ProtocolException");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("Catch: ", "MalformedURLException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Catch: ", "IOException");
        }
        return category;

    }
}
