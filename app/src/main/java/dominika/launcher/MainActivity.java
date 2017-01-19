package dominika.launcher;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AppsByCategory.CategoryAppsModel;
import dominika.launcher.PaintNote.PaintNoteFragment;

public class MainActivity extends AppCompatActivity implements PaintNoteFragment.OnFragmentInteractionListener, OneFragment.OnFragmentInteractionListener, TwoFragment.OnFragmentInteractionListener, ThreeFragment.OnFragmentInteractionListener  {

    private CustomViewPager viewPager;
    private Context context;

    public static ArrayList<AppModel> mAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        ContextWrapper contextWrapper = new ContextWrapper(context);

        // Sets view to viewpager
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Retrieve current system wallpaper and set it to the homescreen
        setSystemWallpaper();

        // Set status bar and navbar to transparent
        setTransparentBars(context);

        // Calculate available screen size and set margins to avoid collapsing UI elements
        setScreenSpace(contextWrapper);

        CategoryAppsModel mCategoryAppsModel = new CategoryAppsModel();
        mCategoryAppsModel.showListOfCategories();

        mAppsList = new ArrayList<AppModel>();
    }

    /*
    * Sets up a viewpager - adds fragments to the adapter.
    * */
    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PaintNoteFragment(), "PAINT");
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //Communicating between fragments
    public void onFragmentInteraction(Uri uri){
        // can be empty
    }


    // Gets current system wallpaper and sets it to the homescreen
    private void setSystemWallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final ImageView imageView = (ImageView) findViewById(R.id.homescreen_wallpaper);

        imageView.setImageDrawable(wallpaperDrawable);
    }

    private void setTransparentBars(Context context) {
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void setScreenSpace(ContextWrapper contextWrapper) {
        ScreenSpace mScreenSpace = new ScreenSpace(this);
        mScreenSpace.calculate(contextWrapper);
    }

    public CustomViewPager getViewPager() {
        return viewPager;
    }

    public static ArrayList<AppModel> getmAppsList() {
        return mAppsList;
    }

    public static void setmAppsList(ArrayList<AppModel> mAppsList) {
        MainActivity.mAppsList = mAppsList;
    }

}
