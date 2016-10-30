package dominika.launcher.AllAppsGrid;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;


import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dominika.launcher.AllAppsGrid.AppModel;

/**
 * Created by Domi on 25.10.2016.
 *
 * Displays only apps which have valid launch intent.
 * Creates a list of apps from all installed apps on the system using application model class.
 */

public class InstalledAppsLoader extends AsyncTaskLoader<ArrayList<AppModel>> {

    ArrayList<AppModel> mAppsToDeliver;

    final PackageManager mPackageManager;
    PackageIntentReceiver mPackageController;

    public InstalledAppsLoader(Context context) {
        super(context);

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
        ArrayList<AppModel> appsList = new ArrayList<AppModel>(allApps.size());

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

        return appsList;
    }

    public void deliverResult(ArrayList<AppModel> appsList) {

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
