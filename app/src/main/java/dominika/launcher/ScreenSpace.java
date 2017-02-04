package dominika.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by Domi on 14.11.2016.
 */

public class ScreenSpace {
    public Activity activity;
    int topMargin;
    int bottomMargin;
    public static Point availableSpace;

    public ScreenSpace(Activity _activity){
        this.activity = _activity;

    }

    public void calculate(ContextWrapper contextWrapper) {
        topMargin = getTopMargin(contextWrapper);
        bottomMargin = getBottomMargin(contextWrapper);
        setMargins(contextWrapper, topMargin, bottomMargin);
    }

    public int getTopMargin(ContextWrapper contextWrapper) {
        int result = 0;
        int resourceId = contextWrapper.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = contextWrapper.getResources().getDimensionPixelSize(resourceId);
        }

        Log.d("top margin", Integer.toString(result));
        return result;
    }

    public int getBottomMargin(ContextWrapper contextWrapper) {
        int result = 0;
        if (hasNavBar(contextWrapper)) {
            int resourceId = contextWrapper.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = contextWrapper.getResources().getDimensionPixelSize(resourceId);
            }
        }
        Log.d("bot margin", Integer.toString(result));
        return result;
    }

    public boolean hasNavBar(ContextWrapper contextWrapper)
    {
        /*int id = contextWrapper.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && contextWrapper.getResources().getBoolean(id);*/

        Point realSize = new Point();
        availableSpace = new Point();
        boolean hasNavBar = false;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        realSize.x = metrics.widthPixels;
        realSize.y = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getSize(availableSpace);
        if (realSize.y != availableSpace.y) {
            int difference = realSize.y - availableSpace.y;
            int navBarHeight = 0;
            Resources resources = contextWrapper.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId);
            }
            if (navBarHeight != 0) {
                if (difference == navBarHeight) {
                    hasNavBar = true;
                }
            }

        }
        Log.d("real screen size", realSize.toString());
        Log.d("available screen size", availableSpace.toString());
        return hasNavBar;
    }

    private void setMargins(ContextWrapper contextWrapper, int topMargin, int bottomMargin) {
        ViewPager pager = (ViewPager) this.activity.findViewById(R.id.viewpager);
        Log.d("activity", "poszło");
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) pager.getLayoutParams();
        lp.topMargin = topMargin;
        lp.bottomMargin = bottomMargin;
        pager.setLayoutParams(lp);
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }
}
