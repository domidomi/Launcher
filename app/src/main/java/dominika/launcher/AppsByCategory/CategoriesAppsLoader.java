package dominika.launcher.AppsByCategory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AppsByCategory.AsyncResponse;
import dominika.launcher.AppsByCategory.CategoryAppsModel;
import dominika.launcher.AppsByCategory.CategoryHttpHelper;
import dominika.launcher.MainActivity;
import dominika.launcher.SharedPreferencesHelper.SharedPreferencesAppModel;
import dominika.launcher.SharedPreferencesHelper.SharedPreferencesHelper;
import dominika.launcher.TwoFragment;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Domi on 25.10.2016.
 *
 * Displays only apps which have valid launch intent.
 * Creates a list of apps from all installed apps on the system using application model class.
 */

public class CategoriesAppsLoader extends AsyncTaskLoader<ArrayList<AppModel>> implements AsyncResponse{

    public ArrayList<AppModel> mAppsToDeliver;

    String clickedFolder;

    final PackageManager mPackageManager;
    PackageIntentReceiver mPackageController;

    ArrayList<AppModel> appsList;
    ArrayList<AppModel> appsFromCategory;
    ArrayList<AppModel> appsToCheckCategory;
    SharedPreferencesHelper sharedPreferencesHelper;
    dominika.launcher.DateTimeTemperature.AsyncResponse mydelegate;
    CategoryHttpHelper helper;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public CategoriesAppsLoader(Context context) {
        super(context);
        this.clickedFolder = TwoFragment.clickedFolder;
        mPackageManager = context.getPackageManager();
    }

    public void setDelegate(dominika.launcher.DateTimeTemperature.AsyncResponse delegate){
        this.mydelegate = delegate;
    }

    @Override
    public ArrayList<AppModel> loadInBackground() {

        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefs.edit().remove("applist").apply();*/


        // Get list of installed apps
        List<ApplicationInfo> allApps = mPackageManager.getInstalledApplications(0);

        if (allApps == null) {
            allApps = new ArrayList<ApplicationInfo>();
        }

        final Context mContext = getContext();

        // Load each app and its label
        // Create a list of apps objects equal to number of installed apps
        appsList = new ArrayList<AppModel>(allApps.size());
        appsFromCategory = new ArrayList<AppModel>();

        for (int i=0; i < allApps.size(); i++) {
            String appPackage = allApps.get(i).packageName;

            // Get only apps which can be launched by user
            if (mContext.getPackageManager().getLaunchIntentForPackage(appPackage) != null ) {
                // Copy app data to new app object
                AppModel app = new AppModel(mContext, allApps.get(i));
                app.loadLabel(mContext);
                app.setmCategory("null"); // Every app will have category named "other" in case
                Log.d("Package: ", app.getApplicationPackageName());
                appsList.add(app);
            }

        }

        Collections.sort(appsList, ALPHA_COMPARATOR);


        Log.d("Ile apek ogólnie? : ", Integer.toString(appsList.size()));

        appsToCheckCategory = new ArrayList<AppModel>();
        sharedPreferencesHelper = new SharedPreferencesHelper(mContext);

        if (MainActivity.mAppsList.isEmpty()) {

            ArrayList<SharedPreferencesAppModel> list = sharedPreferencesHelper.getList();

            if(list != null) {
                for (int i = 0; i < appsList.size(); i++) {
                    for (int j = 0; j < list.size(); j++ ) {
                        if (appsList.get(i).getLabel().equals(list.get(j).getmAppLabel())) {
                            appsList.get(i).setmCategory(list.get(j).getmCategory());
                        }
                    }
                }

                ArrayList<AppModel> knownApps = new ArrayList<AppModel>();
                for (int i = 0; i < appsList.size(); i++) {
                    if (appsList.get(i).getmCategory() != null) {
                        knownApps.add(appsList.get(i));
                        Log.d("Znam apkę: ", knownApps.get(knownApps.size()-1).getLabel());
                    }
                }

                MainActivity.setmAppsList(knownApps);
            } else {
                Log.d("PUSTE ", "SHARED PREFERENCES");
            }
        }

        // Check if known apps list is not empty - if is empty then we know that any of app has known category
        // It will be important at the first run of the launcher
        if (!MainActivity.mAppsList.isEmpty()) {
            Log.d("Już znam jakieś apki: ", Integer.toString(MainActivity.mAppsList.size()));

            // Check if recognized app wasn't deleted from device
            ArrayList<String> labelsApps = new ArrayList<String>();
            for (int i = 0; i < appsList.size(); i++) {
                labelsApps.add(appsList.get(i).getLabel());
            }

            ArrayList<AppModel> appsStillOnDevice = new ArrayList<AppModel>();

            for (int j = 0; j < MainActivity.mAppsList.size(); j++) {
                if (labelsApps.contains(MainActivity.mAppsList.get(j).getLabel())) {
                    appsStillOnDevice.add(MainActivity.mAppsList.get(j));
                } else {
                    continue;
                }
            }
            MainActivity.setmAppsList(appsStillOnDevice);

            Log.d("Tyle bez usunietych: ", Integer.toString(MainActivity.getmAppsList().size()));

            // Check if app is already known
            ArrayList<String> labels = new ArrayList<String>();
            for (int i = 0; i < MainActivity.mAppsList.size(); i++) {
                labels.add(MainActivity.mAppsList.get(i).getLabel());
            }

            // Check if app is already in known apps list
            for (int j = 0; j < appsList.size(); j++) {
                if (labels.contains(appsList.get(j).getLabel())) {
                        continue;
                    } else {
                        appsToCheckCategory.add(appsList.get(j));
                    }
            }
            appsList = appsToCheckCategory;
        }

        Log.d("Ile do sprawdzenia? : ", Integer.toString(appsList.size()));

        // List all apps (just for information)
        for (int i=0; i < appsList.size(); i++) {

            Log.d("Package nr ", Integer.toString(i));
            Log.d("Nazwa: ", appsList.get(i).getApplicationPackageName());
            Log.d("Label: ", appsList.get(i).getLabel());
        }

        return appsList;
    }

    public void setAppsList(ArrayList<AppModel> appsList) {
        this.appsList = appsList;
    }

    public ArrayList<AppModel> getAppsList() {
        return appsList;
    }

    public void turnOffWakeLock() {
        if(wakeLock != null){
            wakeLock.release();
        }
    }


    private void searchForAllCategories() {

        powerManager = (PowerManager) getContext().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "MyWakelockTag");
        Log.d("Włączam", "wake lock");
        wakeLock.acquire();

        // Creating helper and passing all apps list
        helper = new CategoryHttpHelper(this);

        helper.delegate = this;
        // Execute retrieving a category for each app
        helper.execute();
    }

    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.

        wakeLock.release();
        Log.d("Wyłączam", "wake lock");

        // If the known list is empty add all apps - it will run the first time when launcher is running
        if (MainActivity.mAppsList.isEmpty()) {
            MainActivity.setmAppsList(appsList);
        } else {
            // Add every filtered app to the final list
            for (int i=0; i<appsList.size(); i++) {
                MainActivity.mAppsList.add(appsList.get(i));
                Collections.sort(MainActivity.mAppsList, ALPHA_COMPARATOR);
            }
        }


        Log.d("Wpisuje do shared ", "preferences");
        for (int i=0; i<MainActivity.getmAppsList().size(); i++) {
            Log.d("Apka: ", MainActivity.getmAppsList().get(i).getmCategory());
        }

        try {
            sharedPreferencesHelper.saveList(MainActivity.getmAppsList());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Loading of categories: ", "DONE");

        // Now get only apps from that folder
        Log.d("Filtering of apps: ", "STARTED");
        filterApps(MainActivity.getmAppsList());

        // If loader was stopped (interrupted) - don't deliver anything
        if (isReset()) {
            if (appsFromCategory != null) {
                onReleaseResources(appsFromCategory);
            }
        }

        ArrayList<AppModel> allApps = appsFromCategory;
        mAppsToDeliver = appsFromCategory;

        // Passing the list of categories to main activity list
        /*MainActivity.setCategoryApps(appsFromCategory);*/

        if (isStarted()) {
            // If result is already avaiable deliver it.
            super.deliverResult(appsFromCategory);
        }

        // At this point we can release the resources associated with
        // 'allApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (allApps != null) {
            onReleaseResources(appsFromCategory);
        }
    }

    private void filterApps(ArrayList<AppModel> appsList) {
        // Get list of all categories
        ArrayList<List<String>> listOfCategories = new CategoryAppsModel().getListOfCategories();

        // Get list of categories names
        String[] categoriesNames = new CategoryAppsModel().getCategoriesNames();

        ArrayList<AppModel> matchingApps = new ArrayList<AppModel>();

        Log.d("Cliked folder: ", clickedFolder);

        // For each category
        for (int i=0; i<listOfCategories.size(); i++) {
            // If category name is the same as category of clicked folder
            if(categoriesNames[i].toUpperCase().equals(clickedFolder.toUpperCase())){
                // Now we are searching through all apps - which apps belong to that folder
                for (int j=0; j<appsList.size(); j++) {
                    // Get app category
                    String appCategory = appsList.get(j).getmCategory();
                    // If that category is in clicked folder array of categories add it to the list
                    if (listOfCategories.get(i).contains(appCategory)) {
                        matchingApps.add(appsList.get(j));
                        Log.d("Matching app category: ", appsList.get(j).getmCategory());
                    }
                }
            } else {
                Log.d("Category name is: ", categoriesNames[i].toUpperCase());
            }
        }

        setAppsFromCategory(matchingApps);

        Log.d("Filtering of apps: ", "DONE");
        for (int i=0; i<appsFromCategory.size(); i++) {
            Log.d("Apps from category: ", appsFromCategory.get(i).getLabel());
        }

    }

    public ArrayList<AppModel> getAppsFromCategory() {
        return appsFromCategory;
    }

    public void setAppsFromCategory(ArrayList<AppModel> appsFromCategory) {
        this.appsFromCategory = appsFromCategory;
    }

    public void deliverResult(ArrayList<AppModel> appsList) {
        Log.d("Loading of categories: ", "STARTED");
        searchForAllCategories();
    }

    @Override
    protected void onStartLoading() {

        if (mAppsToDeliver != null) {
            // If result is already avaiable deliver it.
            deliverResult(mAppsToDeliver);
        }

        // watch for changes in app install and uninstall operation
        if (mPackageController == null) {
            mPackageController = new PackageIntentReceiver(this);
        }

        if (takeContentChanged() || mAppsToDeliver == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Cancel loading if everything was done
        cancelLoad();
    }

    public void cancelRunningThings() {
    }

    @Override
    public void onCanceled(ArrayList<AppModel> apps) {
        super.onCanceled(apps);
        Log.d("ASYNC", "DELEGATE2");
        if(mydelegate != null){
            Log.d("ASYNC", "DELEGATE1");
            mydelegate.processFinish(new JSONObject());
        }
        Log.d("Wyłączam", " helper");
        turnOffWakeLock();

        // If needed release the resources associated with 'apps'.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        // To be sure if loading was done.
        onStopLoading();

        // If needed release the resources associated with 'apps'.
        if (mAppsToDeliver != null) {
            onReleaseResources(mAppsToDeliver);
            mAppsToDeliver = null;
        }

        // Stop monitoring for changes.
        if (mPackageController != null) {
            getContext().unregisterReceiver(mPackageController);
            mPackageController = null;
        }
    }

    /**
     * Helper method to do the cleanup work if needed, for example if we're
     * using Cursor, then we should be closing it here
     *
     * @param apps
     */
    protected void onReleaseResources(ArrayList<AppModel> apps) {
        // do nothing
    }

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator mCollator = Collator.getInstance();
        @Override
        public int compare(AppModel name1, AppModel name2) {
            return mCollator.compare(name1.getLabel(), name2.getLabel());
        }
    };
}
