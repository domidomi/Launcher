package dominika.launcher.AppsByCategory;

import android.app.LoaderManager;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dominika.launcher.AllAppsGrid.AppListAdapter;
import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AppsByCategory.CategoriesAppsLoader;
import dominika.launcher.AllAppsGrid.GridFragment;
import dominika.launcher.AllAppsGrid.InstalledAppsLoader;
import dominika.launcher.CustomViewPager;
import dominika.launcher.FragmentsBackgroundEffects;
import dominika.launcher.MainActivity;
import dominika.launcher.R;
import dominika.launcher.ScreenSpace;
import dominika.launcher.TwoFragment;

/**
 * Created by Domi on 29.11.2016.
 */

public class CategoriesGridFragment extends GridFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAppListAdapter;
    int category;
    FragmentsBackgroundEffects fragmentsBackgroundEffects;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("Brak aplikacji");

        // Block moving to left-right on categories
        if(CategoriesGridFragment.this.getView().getVisibility() == View.VISIBLE) {
            MainActivity activity = (MainActivity) getActivity();
            CustomViewPager vp = (CustomViewPager) activity.getViewPager();
            vp.setPagingEnabled(false);
        }

        fragmentsBackgroundEffects = new FragmentsBackgroundEffects(getContext());
        Bitmap screenBitmap = fragmentsBackgroundEffects.getScreenShot(getActivity().findViewById(R.id.activity_main));

        loadBackground loader = new loadBackground(new taskComplete() {
            @Override
            public void complete(Bitmap resultBitmap) {
                if (CategoriesGridFragment.this.getView().getVisibility() == View.VISIBLE) {
                    // Its visible
                    Drawable drawable = new BitmapDrawable(getResources(), resultBitmap);
                    CategoriesGridFragment.this.getView().setBackground(drawable);
                } else {
                    // Either gone or invisible
                }
            }
        });
        loader.execute(screenBitmap);


        /*byte[] byteArray =  this.getArguments().getByteArray("screenShot");
        screenShot = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/

        mAppListAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAppListAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);




        startLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity activity = (MainActivity) getActivity();
        CustomViewPager vp = (CustomViewPager) activity.getViewPager();
        vp.setPagingEnabled(true);
    }

    public void startLoading() {
        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        // Load all installed apps
        return new CategoriesAppsLoader(getActivity());
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


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public class loadBackground extends AsyncTask<Bitmap, Void, Bitmap> {

        taskComplete done;

        public loadBackground(taskComplete task) {
            done = task;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            // Calculate how much of bitmap we have to cut
            ContextWrapper contextWrapper = new ContextWrapper(getContext());
            ScreenSpace mScreenSpace = new ScreenSpace(getActivity());

            Integer marginBottom = mScreenSpace.getBottomMargin(contextWrapper);
            Integer marginTop =  mScreenSpace.getTopMargin(contextWrapper);

            return fragmentsBackgroundEffects.cropBitmap(params[0], marginTop, marginBottom);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            done.complete(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    interface taskComplete{
        void complete (Bitmap resultBitmap);
    }
}