package dominika.launcher;

import android.app.FragmentTransaction;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import dominika.launcher.R;
import dominika.launcher.OneFragment;
import dominika.launcher.ThreeFragment;
import dominika.launcher.TwoFragment;

public class MainActivity extends AppCompatActivity implements OneFragment.OnFragmentInteractionListener, TwoFragment.OnFragmentInteractionListener, ThreeFragment.OnFragmentInteractionListener  {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets view to viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Retrieve current system wallpaper and set it to the homescreen
        setSystemWallpaper();
    }

    /*
    * Sets up a viewpager - adds fragments to the adapter.
    * */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);

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
    public void setSystemWallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final ImageView imageView = (ImageView) findViewById(R.id.homescreen_wallpaper);

        imageView.setImageDrawable(wallpaperDrawable);
    }


}
