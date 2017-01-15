package dominika.launcher.AppsByCategory;

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
import java.util.ArrayList;

import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AppsByCategory.CategoriesAppsLoader;
import dominika.launcher.AllAppsGrid.InstalledAppsLoader;
import dominika.launcher.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by Domi on 28.11.2016.
 */

public class CategoryHttpHelper extends AsyncTask<Void, Void, ArrayList<AppModel>>{

    ArrayList<AppModel> appsList;

    CategoriesAppsLoader loader;

    public AsyncResponse delegate = null;


    /*public CategoryHttpHelper(ArrayList<AppModel> appsList) {
        this.appsList = appsList;

    }*/

    public CategoryHttpHelper(CategoriesAppsLoader loader) {
        this.loader = loader;

    }

    @Override
    protected ArrayList<AppModel> doInBackground(Void... arg0) {
        appsList = loader.getAppsList();
        // For each app - get the category and store it in variable
        for (int i=0; i < appsList.size(); i++) {
            String category = "Other";

            try {
                URL url = new URL("https://play.google.com/store/apps/details?id=" + appsList.get(i).getAppInfo().packageName + "&hl=en");
                Log.d("Otwieram połączenie ", url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                int status = connection.getResponseCode();

                if(status != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "http response code is " + connection.getResponseCode());
                    appsList.get(i).setmCategory(category);
                    continue;
                }

                /* JSOUP */
                Document doc = Jsoup.connect(url.toString()).get();
                Element element = doc.select("span[itemprop = genre]").first();
                category = element.text();
                Log.d("Genre = ", category);

                appsList.get(i).setmCategory(category);
                continue;

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

            // Will be set to null if try didn't work
            appsList.get(i).setmCategory(category);

        }

        return appsList;
    }


    @Override
    protected void onPostExecute(ArrayList<AppModel> appModels) {
        super.onPostExecute(appModels);

        Log.d("Skończył szukanie: ", "HTTP HELPER");
        loader.setAppsList(appModels);

        delegate.processFinish("pupka");
    }
}
