package dominika.launcher.AllAppsGrid;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;


import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dominika.launcher.AppsByCategory.AsyncResponse;
import dominika.launcher.AppsByCategory.CategoryAppsModel;
import dominika.launcher.AppsByCategory.CategoryHttpHelper;
import dominika.launcher.TwoFragment;

/**
 * Created by Domi on 25.10.2016.
 *
 * Displays only apps which have valid launch intent.
 * Creates a list of apps from all installed apps on the system using application model class.
 */

public class InstalledAppsLoader extends AsyncTaskLoader<ArrayList<AppModel>> implements AsyncResponse{

    public ArrayList<AppModel> mAppsToDeliver;

    /*
    * Code may be:
    * - categories
    * - allApps
    * */
    String code;
    String clickedFolder;

    final PackageManager mPackageManager;
    PackageIntentReceiver mPackageController;

    ArrayList<AppModel> appsList;
    ArrayList<AppModel> appsFromCategory;

    public InstalledAppsLoader(Context context, String code) {
        super(context);
        this.code = code;
        this.clickedFolder = TwoFragment.clickedFolder;
        mPackageManager = context.getPackageManager();
    }

    @Override
    public ArrayList<AppModel> loadInBackground() {

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
                appsList.add(app);
            }

        }

        Collections.sort(appsList, ALPHA_COMPARATOR);

        Log.d("Ile apek? : ", Integer.toString(appsList.size()));

        // List all apps (just for information)
        for (int i=0; i < appsList.size(); i++) {

            Log.d("Package nr ", Integer.toString(i));
            Log.d("Nazwa: ", appsList.get(i).getApplicationPackageName());
            Log.d("Label: ", appsList.get(i).getLabel());
        }

        /*// If we want to retrieve app by category (not all apps) do:
        if (code.equals("categories")) {
            Log.d("Loading of categories: ", "STARTED");
            searchForAllCategories(appsList);
        }*/
        return appsList;
    }

    public void setAppsList(ArrayList<AppModel> appsList) {
        this.appsList = appsList;
    }

    public ArrayList<AppModel> getAppsList() {
        return appsList;
    }



    private void searchForAllCategories() {

        // Creating helper and passing all apps list
        CategoryHttpHelper helper = new CategoryHttpHelper(this);

        helper.delegate = this;

        // Execute retrieving a category for each app
        helper.execute();
    }

    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.

        Log.d("Loading of categories: ", "DONE");
        Log.d("Filtering of apps: ", "STARTED");
        filterApps(appsList);

        // If loader was stopped (interrupted) - don't deliver anything
        if (isReset()) {
            if (appsFromCategory != null) {
                onReleaseResources(appsFromCategory);
            }
        }

        ArrayList<AppModel> allApps = appsFromCategory;
        mAppsToDeliver = appsFromCategory;

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

        // If we want to retrieve app by category (not all apps) do:
        if (code.equals("categories")) {
            Log.d("Loading of categories: ", "STARTED");
            searchForAllCategories();
        } else {
            // If loader was stopped (interrupted) - don't deliver anything
            if (isReset()) {
                if (appsList != null) {
                    onReleaseResources(appsList);
                }
            }

            ArrayList<AppModel> allApps = appsList;
            mAppsToDeliver = appsList;

            if (isStarted()) {
                // If result is already avaiable deliver it.
                super.deliverResult(appsList);
            }

            // At this point we can release the resources associated with
            // 'allApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (allApps != null) {
                onReleaseResources(allApps);
            }

        }

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

    @Override
    public void onCanceled(ArrayList<AppModel> apps) {
        super.onCanceled(apps);

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
