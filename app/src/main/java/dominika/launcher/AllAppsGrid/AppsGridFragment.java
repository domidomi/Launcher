package dominika.launcher.AllAppsGrid;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

import dominika.launcher.CustomViewPager;
import dominika.launcher.FragmentsBackgroundEffects;
import dominika.launcher.MainActivity;
import dominika.launcher.R;
import dominika.launcher.ScreenSpace;

/**
 * Created by Domi on 28.10.2016.
 */

public class AppsGridFragment extends GridFragment implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAppListAdapter;
    FragmentsBackgroundEffects fragmentsBackgroundEffects;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("Brak aplikacji");

        // Block moving to left-right on apps
        if(AppsGridFragment.this.getView().getVisibility() == View.VISIBLE) {
            MainActivity activity = (MainActivity) getActivity();
            CustomViewPager vp = (CustomViewPager) activity.getViewPager();
            vp.setPagingEnabled(false);
        }

        fragmentsBackgroundEffects = new FragmentsBackgroundEffects(getContext());
        Bitmap screenBitmap = fragmentsBackgroundEffects.getScreenShot(getActivity().findViewById(R.id.activity_main));

        loadBackground loader = new loadBackground(new taskComplete() {
            @Override
            public void complete(Bitmap resultBitmap) {
                if (AppsGridFragment.this.getView().getVisibility() == View.VISIBLE) {
                    // Its visible
                    Drawable drawable = new BitmapDrawable(getResources(), resultBitmap);
                    //drawable.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
                    AppsGridFragment.this.getView().setBackground(drawable);
                } else {
                    // Either gone or invisible
                }
            }
        });
        loader.execute(screenBitmap);


        mAppListAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAppListAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);

        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        return new InstalledAppsLoader(getActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity activity = (MainActivity) getActivity();
        CustomViewPager vp = (CustomViewPager) activity.getViewPager();
        vp.setPagingEnabled(true);
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
