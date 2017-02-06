package dominika.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import dominika.launcher.AllAppsGrid.AppModel;
import dominika.launcher.AppsByCategory.CategoryAppsModel;
import dominika.launcher.PaintNote.PaintNoteFragment;

public class MainActivity extends AppCompatActivity implements PaintNoteFragment.OnFragmentInteractionListener, OneFragment.OnFragmentInteractionListener, TwoFragment.OnFragmentInteractionListener, ThreeFragment.OnFragmentInteractionListener  {

    private CustomViewPager viewPager;
    private Context context;
    ContextWrapper contextWrapper;
    Activity activity;

    View.OnClickListener mOnClickListener;

    final int PERMISSIONS_CODE = 1;

    boolean FIRST_RUN = true;

    boolean IS_ACTIVITY_VISIBLE = false;


    public static ArrayList<AppModel> mAppsList;

    public static boolean ALL_PERMISSIONS_GRANTED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        Button permissionsButton = (Button) this.findViewById(R.id.permissions_button);
        permissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IS_ACTIVITY_VISIBLE = true;
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + context.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(i);
            }
        });

        checkPermissions();

    }

    public void continueLoading() {
        LinearLayout permissionsLayout = (LinearLayout) findViewById(R.id.permissions_layout);
        permissionsLayout.setVisibility(View.GONE);

        setContentView(R.layout.activity_main);
        contextWrapper = new ContextWrapper(context);

        // Sets view to viewpager
        viewPager = (CustomViewPager) this.findViewById(R.id.viewpager);
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
        //adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*Toast.makeText(MainActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();*/
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




    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }


    public void checkPermissions() {
        // Here, thisActivity is the current activity
        List<String> permissionsNeeded = new ArrayList<String>();

        if (FIRST_RUN) {
            FIRST_RUN = false;
        }

        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Lista kontaktów");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_SMS))
            permissionsNeeded.add("Lista SMS");
        if (!addPermission(permissionsList, android.Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Lista połączeń");
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Aparat");
        if (!addPermission(permissionsList, android.Manifest.permission.WAKE_LOCK))
            permissionsNeeded.add("Podtrzymanie działania w tle");

        if (permissionsList.size() == 0) {
            continueLoading();
            return;
        }


        activity = this;

        if (permissionsList.size() > 0) {
            // If permissions on newer systems
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "Musisz zapewnić dostęp do: " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), PERMISSIONS_CODE);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), PERMISSIONS_CODE);
            return;
        } else {
            continueLoading();
        }

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), PERMISSIONS_CODE);
            }
        };

    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Wyjdź", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_CODE:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WAKE_LOCK, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    continueLoading();
                    return;
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Któreś ze zezwoleń zostało zablokowane", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (IS_ACTIVITY_VISIBLE) {
            IS_ACTIVITY_VISIBLE = false;
            checkPermissions();
        }
    }
}
