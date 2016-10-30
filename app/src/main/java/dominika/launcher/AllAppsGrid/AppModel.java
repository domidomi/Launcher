package dominika.launcher.AllAppsGrid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by Domi on 25.10.2016.
 *
 * Model of an application object which stores all needed information about an app retrieved from system.
 */

public class AppModel {
    private final Context mContext;
    private final ApplicationInfo mAppInfo;

    private String mAppLabel;
    private Drawable mAppIcon;

    private boolean mMounted;
    private final File mApkFile;

    public AppModel(Context context, ApplicationInfo info) {
        mContext = context;
        mAppInfo = info;

        //Directory of an app
        mApkFile = new File(info.sourceDir);
    }

    // Get info about app
    public ApplicationInfo getAppInfo() {
        return mAppInfo;
    }

    // Get package name
    public String getApplicationPackageName() {
        return getAppInfo().packageName;
    }

    // Get label (name) of an app
    public String getLabel() {
        return mAppLabel;
    }


    // Get icon of an app
    public Drawable getIcon() {
        if (mAppIcon == null) {
            if (mApkFile.exists()) {
                mAppIcon = mAppInfo.loadIcon(mContext.getPackageManager());
                return mAppIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mAppIcon = mAppInfo.loadIcon(mContext.getPackageManager());
                return mAppIcon;
            }
        } else {
            return mAppIcon;
        }

        return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }

    void loadLabel(Context context) {
        if (mAppLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mAppLabel = mAppInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mAppInfo.loadLabel(context.getPackageManager());
                mAppLabel = label != null ? label.toString() : mAppInfo.packageName;
            }
        }
    }


}
