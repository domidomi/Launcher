package dominika.launcher.AppsByCategory;

import android.app.LoaderManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

import dominika.launcher.AllAppsGrid.AppListAdapter;
import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AllAppsGrid.GridFragment;
import dominika.launcher.AllAppsGrid.InstalledAppsLoader;

/**
 * Created by Domi on 29.11.2016.
 */

public class CategoriesGridFragment extends GridFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAppListAdapter;
    String code = "categories";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No Applications");

        mAppListAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAppListAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);

        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {


        return new InstalledAppsLoader(getActivity(), code);
        /*for (int i=0; i < appsList.size(); i++) {
            Log.d("Apki","i kategorie");
            Log.d(appsList.get(i).getLabel(), appsList.get(i).getmCategory());
        }*/
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        mAppListAdapter.setData(apps);

        if (isResumed()) {
            setGridShown(true);
        } else {
            setGridShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAppListAdapter.setData(null);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        AppModel app = (AppModel) getGridAdapter().getItem(position);
        if (app != null) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());

            if (intent != null) {
                startActivity(intent);
            }
        }
    }


}